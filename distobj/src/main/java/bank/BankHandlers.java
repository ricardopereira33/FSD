package bank;

import DO.DO;
import DO.ObjRef;
import DO.Backup;
import bank.Impl.BankImp;
import bank.Interfaces.Account;
import bank.Interfaces.Bank;
import bank.Rep.*;
import bank.Req.*;
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

import java.rmi.UnexpectedException;
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

        System.out.println("ServerBank running...");
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
                c.handler(accessReq.class, (m) -> {
                    BankImp b = (BankImp) d.getElement(m.bankid);
                    Account ac = b.access(m.id);
                    try{
                        System.out.println("fdsf: " + m.ctx);
                        registInManager(m.ctx, b);
                    }
                    catch(Exception e){ e.printStackTrace();}

                    return Futures.completedFuture(new accessRep(d.oExport(ac)));
                });
                c.handler(transferReq.class, (m) -> {
                    Account ac = (Account) d.getElement(m.accountid);
                    Account aOther = null;
                    try {
                        aOther = (Account) d.oImport(m.ref);
                        if(aOther == null)
                            aOther = (Account) d.getElement(m.accountid);

                        ac.transfer(aOther, m.value);
                    } catch (UnexpectedException e) { e.printStackTrace(); }

                    return Futures.completedFuture(new transferRep(true));
                });
                c.handler(creditReq.class, (m) -> {
                    Account ac = (Account) d.getElement(m.accountid);
                    ac.credit(m.value);

                    return Futures.completedFuture(new creditRep(true));
                });
                c.handler(debitReq.class, (m) -> {
                    Account ac = (Account) d.getElement(m.accountid);
                    ac.debit(m.value);

                    return Futures.completedFuture(new debitRep(true));
                });
                c.handler(getIdReq.class, (m) -> {
                    Account ac = (Account) d.getElement(m.accountid);

                    return Futures.completedFuture(new getIdRep(ac.getId()));
                });
                c.handler(historyReq.class, (m) -> {
                    Account ac = (Account) d.getElement(m.accountid);

                    return Futures.completedFuture(new historyRep(ac.getHistory()));
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
        tc.serializer().register(accessRep.class);
        tc.serializer().register(accessReq.class);
        tc.serializer().register(transferRep.class);
        tc.serializer().register(transferReq.class);
        tc.serializer().register(creditRep.class);
        tc.serializer().register(creditReq.class);
        tc.serializer().register(debitRep.class);
        tc.serializer().register(debitReq.class);
        tc.serializer().register(getIdRep.class);
        tc.serializer().register(getIdReq.class);
        tc.serializer().register(historyRep.class);
        tc.serializer().register(historyReq.class);
        tc.serializer().register(Abort.class);
        tc.serializer().register(Commit.class);
        tc.serializer().register(Ok.class);
        tc.serializer().register(Prepare.class);
        tc.serializer().register(Rollback.class);
        tc.serializer().register(Address.class);
        tc.serializer().register(NewResourceRep.class);
        tc.serializer().register(NewResourceReq.class);
        tc.serializer().register(Context.class);
        tc.serializer().register(ObjRef.class);
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
