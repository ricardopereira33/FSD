package bookstore;

import DO.DO;
import DO.Backup;
import DO.ObjRef;
import bank.Data.Invoice;
import bookstore.Impl.Book;
import bookstore.Impl.CartImp;
import bookstore.Impl.StoreImp;
import bookstore.Interfaces.Cart;
import bookstore.Interfaces.Store;
import bookstore.Rep.*;
import bookstore.Req.*;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreHandlers {
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

    public StoreHandlers(Transport t, SingleThreadContext tc, Address address, DO d, Log log) {
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
    }

    public void exe(){
        registMsg();
        registLogHandlers();

        System.out.println("ServerStore running...");
    }

    private void registLogHandlers() {
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
                registHandlers(t, tc, address, d);
                readLog();
                Store s = new StoreImp();
                d.oExport(s);
            });
        });
    }

    private void addToVolatil(Object o){
        int i = index.incrementAndGet();
        volatilLog.put(i, o);
    }

    private void readLog() {
        Map<Integer, Object> list = new HashMap<>();
        boolean prepared = false;
        Context ctx = null;
        for(Map.Entry<Integer, Object> e : volatilLog.entrySet()){
            switch(e.getValue().getClass().getName()){
                case "log.Prepare":
                    System.out.println("Log: Prepare");
                    prepared = true;
                    ctx = ((Prepare) e.getValue()).ctx;
                    break;
                case "log.Commit":
                    System.out.println("Log: Commit");
                    prepared = false;
                    //list.values().stream().forEach((o) -> d.oExport(o));
                    //d.print();
                    break;
                case "log.Abort":
                    System.out.println("Log: Abort");
                    prepared = false;
                    list.clear();
                    break;
                case "DO.Backup":
                    System.out.println("Log: Backup");
                    Backup b = (Backup)e.getValue();
                    list.put(b.getId(), b);
                    break;
            }
        }
        if(prepared){
            System.out.println("Ask manager !!");
        }
    }

    private void registHandlers(Transport t, ThreadContext tc, Address address, DO d){
        tc.execute( () -> {
            t.server().listen(address, (c) -> {
                c.handler(StoreSearchReq.class, (m) -> {
                    StoreImp s = (StoreImp) d.getElement(m.storeid);
                    Book b = null;
                    try{
                        b = s.search(m.title);
                        registInManager(m.ctx, s);
                    }
                    catch(Exception e){ e.printStackTrace(); }

                    return Futures.completedFuture(new StoreSearchRep(b));
                });
                c.handler(StoreMakeCartReq.class, (m) -> {
                    StoreImp s = (StoreImp) d.getElement(m.storeid);
                    Cart cart = null;
                    try{
                        cart = s.newCart();
                        registInManager(m.ctx, s);
                    }
                    catch(Exception e){ e.printStackTrace(); }
                    ObjRef ref = d.oExport(cart);
                    log.append(new Backup(ref.id, cart));

                    return Futures.completedFuture(new StoreMakeCartRep(ref));
                });
                c.handler(addHistoryReq.class, (m) -> {
                    StoreImp s = (StoreImp) d.getElement(m.storeid);
                    try{
                        s.addHistory(m.value, m.list);
                        registInManager(m.ctx, s);
                    }
                    catch(Exception e){ e.printStackTrace(); }
                    log.append(new Backup(m.storeid, s));

                    return Futures.completedFuture(new addHistoryRep(true));
                });
                c.handler(CartAddReq.class, (m) -> {
                    Cart cart = (Cart) d.getElement(m.cartid);
                    Book b = m.b;
                    try {
                        registInManager(m.ctx, null);
                    }
                    catch (Exception e) { e.printStackTrace(); }
                    boolean ok = cart.add(b);
                    log.append(new Backup(m.cartid, cart));


                    return Futures.completedFuture(new CartAddRep(ok));
                });
                c.handler(CartBuyReq.class, (m) -> {
                    Cart cart = (Cart) d.getElement(m.cartid);
                    try {
                        registInManager(m.ctx, null);
                    }
                    catch (Exception e) { e.printStackTrace(); }
                    Invoice i = cart.buy();
                    log.append(new Backup(m.cartid, cart));

                    return Futures.completedFuture(new CartBuyRep(true, i));
                });
            });
        });
    }

    private void registMsg(){
        tc.serializer().register(StoreSearchReq.class);
        tc.serializer().register(StoreSearchRep.class);
        tc.serializer().register(StoreMakeCartRep.class);
        tc.serializer().register(StoreMakeCartReq.class);
        tc.serializer().register(CartAddReq.class);
        tc.serializer().register(CartAddRep.class);
        tc.serializer().register(CartBuyReq.class);
        tc.serializer().register(CartBuyRep.class);
        tc.serializer().register(ObjRef.class);
        tc.serializer().register(Abort.class);
        tc.serializer().register(Commit.class);
        tc.serializer().register(Ok.class);
        tc.serializer().register(Prepare.class);
        tc.serializer().register(Rollback.class);
        tc.serializer().register(Address.class);
        tc.serializer().register(NewResourceRep.class);
        tc.serializer().register(NewResourceReq.class);
        tc.serializer().register(Context.class);
        tc.serializer().register(Book.class);
        tc.serializer().register(Invoice.class);
        tc.serializer().register(addHistoryRep.class);
        tc.serializer().register(addHistoryReq.class);
        tc.serializer().register(StoreImp.class);
        tc.serializer().register(CartImp.class);
    }

    private void registInManager(Context ctx, StoreImp s) throws Exception {
        Transport t = new NettyTransport();
        if(tcManager == null) {
           connectManager(ctx, s);
        }
        int managerid = 1;
        NewResourceRep r = null;
        try{
            r = (NewResourceRep) tcManager.execute( () ->
                    conManager.sendAndReceive(new NewResourceReq(ctx.getTxid(), managerid, address))
            ).join().get();
        }
        catch (Exception e){ e.printStackTrace(); }

        if(s != null)
            s.lock();
        this.id = r.idRes;
    }

    private void connectManager(Context ctx, StoreImp s) throws Exception {
        tcManager = new SingleThreadContext("srv-%d", new Serializer());
        tcManager.serializer().register(NewResourceRep.class);
        tcManager.serializer().register(NewResourceReq.class);
        conManager = tcManager.execute( () ->
                t.client().connect(ctx.getAddress())
        ).join().get();
        createClique(ctx.getAddress(), s);
    }

    private void createClique(Address address, StoreImp si) {
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
                si.unlock();
            });
            cli.handler(Rollback.class, (s, m) -> {
                System.out.println("Rollback");
                si.unlock();
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
