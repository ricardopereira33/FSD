package bank;

import DO.DO;
import DO.ObjRef;
import bank.Impl.BankImp;
import bank.Interfaces.Bank;
import bank.Rep.TransferRep;
import bank.Req.TransferReq;
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

public class BankHandlers {
    private Transport t;
    private SingleThreadContext tc;
    private Address address;
    private DO d;
    private Log log;
    private int id;
    private SingleThreadContext tcManager;
    private Connection conManager;
    private Clique cli;

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
    }

    public void exe(){
        registMsg();
        registLogHandlers();

        System.out.println("Server running...");
    }

    private void registLogHandlers() {
        tc.execute(() ->{
            log.handler(Prepare.class, (sender, msg)-> {
                System.out.println("Log: Prepare");
            });
            log.handler(Commit.class, (sender, msg)-> {
                System.out.println("Log: Commit");
            });
            log.handler(Abort.class, (sender, msg)->{
                System.out.println("Log: Abort");
            });
            log.open().thenRun(()-> {
                registHandlers(t, tc, address, d);
                Bank b = new BankImp();
                d.oExport(b);

                // Save initial store
                log.append(b);
            });
        });
    }

    private void registMsg(){
        tc.serializer().register(TransferRep.class);
        tc.serializer().register(TransferReq.class);
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

    private void registHandlers(Transport t, ThreadContext tc, Address address, DO d){
        tc.execute( () -> {
            t.server().listen(address, (c) -> {
                c.handler(TransferReq.class, (m) -> {
                    Bank b = (Bank) d.getElement(m.bankid);
                    boolean res = b.transfer(m.recv, m.send, m.value);
                    try{ registInManager(new Context(m.txid, m.address));}
                    catch(Exception e){ System.out.println("Erro: "+ e.getMessage());}

                    return Futures.completedFuture(new TransferRep(res));
                });
            });
        });
    }

    private void registInManager(Context ctx) throws Exception {
        Transport t = new NettyTransport();
        if(tcManager == null) {
            tcManager = new SingleThreadContext("srv-%d", new Serializer());
            tcManager.serializer().register(NewResourceRep.class);
            tcManager.serializer().register(NewResourceReq.class);
            conManager = tcManager.execute( () ->
                    t.client().connect(ctx.getAddress())
            ).join().get();
            createClique(ctx.getAddress());
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
        this.id = r.idRes;
    }

    private void createClique(Address address) {
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
                log.append(m);
            });
            cli.handler(Rollback.class, (s, m) -> {
                System.out.println("Rollback");
                //rollback();
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
