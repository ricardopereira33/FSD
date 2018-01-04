package bank;

import DO.DO;
import DO.ObjRef;
import DO.Backup;
import bank.Impl.BankImp;
import bank.Interfaces.Account;
import bank.Interfaces.Bank;
import bank.Rep.newAccountRep;
import bank.Req.newAccountReq;
import io.atomix.catalyst.concurrent.Futures;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Connection;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import log.*;
import manager.Data.Context;
import manager.Rep.NewResourceRep;
import manager.Req.NewResourceReq;
import pt.haslab.ekit.Clique;
import pt.haslab.ekit.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BankHandlers {
    private Transport t;
    private ThreadContext tc;
    private Address address;
    private DO d;
    private Log log;
    private int id;
    private ThreadContext tcManager;
    private Connection conManager;
    private Clique cli;
    private HashMap<Integer, Object> volatilLog;
    private AtomicInteger index;

    public BankHandlers(Transport t, SingleThreadContext tc, Address address, DO d, Log log) {
        this.t = t;
        this.tc = tc;
        this.address = address;
        this.d = d;
        this.log = log;
        this.id = 2;
        this.tcManager = null;
        this.conManager = null;
        this.cli = null;
        this.volatilLog = new HashMap<>();
        this.index = new AtomicInteger(0);
    }

    public void exe(){
        registMsg();
        registLogHandlers();

        System.out.println("Server running...");
    }

    private void registLogHandlers() {
        tc.execute(() ->{
            log.handler(Prepare.class, (sender, msg)-> {
                int i = index.incrementAndGet();
                volatilLog.put(i,msg);
            });
            log.handler(Commit.class, (sender, msg)-> {
                int i = index.incrementAndGet();
                volatilLog.put(i,msg);
            });
            log.handler(Abort.class, (sender, msg)->{
                int i = index.incrementAndGet();
                volatilLog.put(i,msg);
            });
            log.handler(Backup.class, (sender, msg) -> {
                int i = index.incrementAndGet();
                volatilLog.put(i,msg);
            });
            log.open().thenRun(()-> {
                registHandlers(t, tc, address, d);
                // readLog();
                Bank b = new BankImp();
                d.oExport(b);
            });
        });
    }

    private void readLog() {
        for(Map.Entry<Integer, Object> e : volatilLog.entrySet()){
            switch(e.getValue().getClass().getName()){
                case "Prepare":
                    System.out.println("Log: Prepare");
                    break;
                case "Commit":
                    System.out.println("Log: Commit");
                    break;
                case "Abort":
                    System.out.println("Log: Abort");
                    break;
                case "Backup":
                    System.out.println("Log: Abort");
                    break;
            }
        }
    }

    private void registHandlers(Transport t, ThreadContext tc, Address address, DO d){
        tc.execute( () -> {
            t.server().listen(address, (c) -> {
                c.handler(newAccountReq.class, (m) -> {
                    BankImp b = (BankImp) d.getElement(m.bankid);
                    Account ac = b.newAccount(m.id);
                    try{
                        registInManager(m.ctx, b);
                    }
                    catch(Exception e){ System.out.println("Erro: "+ e.getMessage());}

                    return Futures.completedFuture(new newAccountRep(d.oExport(ac)));
                });
            });
        });
    }

    private void registInManager(Context ctx, BankImp b) throws Exception {
        Transport t = new NettyTransport();
        if(tcManager == null) {
            connectManager(ctx, b);
        }
        int managerid = 1;
        NewResourceRep r = null;
        try{
            r = (NewResourceRep) tcManager.execute( () ->
                    conManager.sendAndReceive(new NewResourceReq(ctx.getTxid(), managerid, address))
            ).join().get();
        }
        catch (Exception e){
            System.out.println("Erro in Manager: " + e.getMessage());
        }
        b.lock();
        this.id = r.idRes;
    }

    private void registMsg(){
        tc.serializer().register(newAccountRep.class);
        tc.serializer().register(newAccountReq.class);
        tc.serializer().register(ObjRef.class);
        tc.serializer().register(Abort.class);
        tc.serializer().register(Commit.class);
        tc.serializer().register(Ok.class);
        tc.serializer().register(Prepare.class);
        tc.serializer().register(Rollback.class);
        tc.serializer().register(Address.class);
        tc.serializer().register(NewResourceRep.class);
        tc.serializer().register(NewResourceReq.class);
    }

    private void connectManager(Context ctx, BankImp b) throws Exception {
        tcManager = new SingleThreadContext("srv-%d", new Serializer());
        tcManager.serializer().register(NewResourceRep.class);
        tcManager.serializer().register(NewResourceReq.class);
        conManager = tcManager.execute( () ->
                t.client().connect(ctx.getAddress())
        ).join().get();
        createClique(ctx.getAddress(), b);
    }

    private void createClique(Address address, BankImp b) {
        Transport tr = new NettyTransport();
        ThreadContext tc = new SingleThreadContext("proto-%d", new Serializer());
        Address[] addrs = getAddress(address);

        this.cli = new Clique(tr, id, addrs);

        tc.execute(()->{
            cli.handler(Prepare.class, (s, m) -> {
                System.out.println("Prepare");
                log.append(m);
                cli.send(s,new Ok("Ok"));
            });
            cli.handler(Commit.class, (s, m) -> {
                System.out.println("Commit");
                b.unlock();
                log.append(m);
            });
            cli.handler(Rollback.class, (s, m) -> {
                System.out.println("Rollback");
                b.unlock();
            });
            cli.open().thenRun(() ->{
                System.out.println("2PC begin.");
            });
        }).join();
    }

    public Address[] getAddress(Address address) {
        Address manager = new Address(address.host()+":"+(address.port()+1));
        Address own = new Address(this.address.host()+":"+(this.address.port()+1));
        Address[] list = new Address[id+1];

        for(int i = 0; i<id; i++){
            switch (i){
                case 0:
                    list[0] = manager;
                    break;
                default:
                    list[i] = null;
            }
        }
        list[id] = own;

        return list;
    }
}
