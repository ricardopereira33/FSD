package DO;

import bank.Data.Invoice;
import bookstore.Impl.Book;
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

public abstract class Server {
    public Transport t;
    public ThreadContext tc;
    public Address address;
    public DO d;
    public Log log;
    public int id;
    public ThreadContext tcManager;
    public Connection conManager;
    public Clique cli;
    public HashMap<Integer, Object> volatilLog;
    public AtomicInteger index;
    private boolean prepared;

    public Server(){}

    public Server(Transport t, SingleThreadContext tc, Address address, DO d, Log log) {
        this.t = t;
        this.tc = tc;
        this.address = address;
        this.d = d;
        this.log = log;
        this.id = 1;
        this.tcManager = null;
        this.conManager = null;
        this.cli = null;
        this.volatilLog = new HashMap<>();
        this.index = new AtomicInteger(0);
        this.prepared = false;
        registMsg();
        registLogHandlers();
    }

    public void registLogHandlers() {
        tc.execute(() ->{
            log.handler(Prepare.class, (sender, msg)-> {
                addToVolatil(msg);
            });
            log.handler(Commit.class, (sender, msg)-> {
                addToVolatil(msg);
            });
            log.handler(Abort.class, (sender, msg)->{
                addToVolatil(msg);
            });
            log.handler(Backup.class, (sender, msg) -> {
                addToVolatil(msg);
            });
            log.open().thenRun(()-> {
                readLog();
            });
        });
    }

    private void addToVolatil(Object o){
        int i = index.incrementAndGet();
        volatilLog.put(i, o);
    }

    public void readLog() {
        Map<Integer, Obj> list = new HashMap<>();
        int idLocked = 1;
        Context ctx = null;
        for(Map.Entry<Integer, Object> e : volatilLog.entrySet()){
            switch(e.getValue().getClass().getName()){
                case "log.Prepare":
                    System.out.println("Log: Prepare");
                    prepared = true;
                    idLocked = ((Prepare) e.getValue()).idRes;
                    ctx = ((Prepare) e.getValue()).ctx;
                    break;
                case "log.Commit":
                    System.out.println("Log: Commit");
                    prepared = false;
                    d.update(list);
                    d.print();
                    break;
                case "log.Abort":
                    System.out.println("Log: Abort");
                    prepared = false;
                    list.clear();
                    break;
                case "DO.Backup":
                    System.out.println("Log: Backup");
                    Backup b = (Backup)e.getValue();
                    list.put(b.getId(), b.getO());
                    break;
            }
        }
        checkStatus(ctx, idLocked, list);
    }

    private void checkStatus(Context ctx, int idLocked, Map<Integer, Obj> list) {
        if(prepared){
            Obj o = d.getElement(idLocked);
            try {
                d.update(list);
                registInManager(ctx, o);
            }
            catch (Exception e){ e.printStackTrace(); }
        }
    }

    public void registInManager(Context ctx, Obj o) throws Exception {
        // connect to the manager
        Transport t = new NettyTransport();
        if(tcManager == null) {
            connectManager(ctx, o);
        }
        int managerid = 1;

        // registe resource
        NewResourceRep r = null;
        try{
            r = (NewResourceRep) tcManager.execute( () ->
                    conManager.sendAndReceive(new NewResourceReq(ctx.getTxid(), managerid, address))
            ).join().get();
        }
        catch (Exception e){ e.printStackTrace(); }
        this.id = r.idRes;

        // create the Clique for 2PC
        if(cli == null){
            createClique(ctx.getAddress(), o);
        }

        // lock resource
        if(o != null)
            o.lock();
    }

    private void connectManager(Context ctx, Obj o) throws Exception {
        tcManager = new SingleThreadContext("srv-%d", new Serializer());
        tcManager.serializer().register(NewResourceRep.class);
        tcManager.serializer().register(NewResourceReq.class);
        conManager = tcManager.execute( () ->
                t.client().connect(ctx.getAddress())
        ).join().get();
    }

    private void createClique(Address address, Obj si) {
        Transport tr = new NettyTransport();
        ThreadContext tc = new SingleThreadContext("proto-%d", new Serializer());
        Address[] addrs = getAddress(address);

        this.cli = new Clique(tr, Clique.Mode.ANY, id, addrs);

        tc.execute(()-> {
            cli.handler(Prepare.class, (s, m) -> {
                System.out.println("Prepare");
                m.idRes = si.getIdRes();
                log.append(m);
                cli.send(s, new Ok("Ok"));
            });
            cli.handler(Commit.class, (s, m) -> {
                System.out.println("Commit");
                log.append(m);
                si.unlock();
            });
            cli.handler(Rollback.class, (s, m) -> {
                System.out.println("Rollback");
                si.unlock();
            });
            cli.open().thenRun(() -> {
                System.out.println("2PC begin.");
                if(prepared){
                    cli.send(0, new Ok("Ok"));
                }
            });
            cli.onException((e) -> e.printStackTrace());
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

    private void registMsg(){
        tc.serializer().register(ObjRef.class);
        tc.serializer().register(Abort.class);
        tc.serializer().register(Commit.class);
        tc.serializer().register(Ok.class);
        tc.serializer().register(Prepare.class);
        tc.serializer().register(Rollback.class);
        tc.serializer().register(Address.class);
        tc.serializer().register(Context.class);
        tc.serializer().register(Book.class);
        tc.serializer().register(Invoice.class);
        tc.serializer().register(NewResourceRep.class);
        tc.serializer().register(NewResourceReq.class);
        tc.serializer().register(Backup.class);
    }
}
