package bookstore;

import bookstore.Data.Data;
import bookstore.Data.ObjRef;
import bookstore.Impl.StoreImp;
import bookstore.Interfaces.Book;
import bookstore.Interfaces.Cart;
import bookstore.Interfaces.Store;
import bookstore.Rep.CartAddRep;
import bookstore.Rep.CartBuyRep;
import bookstore.Rep.StoreMakeCartRep;
import bookstore.Rep.StoreSearchRep;
import bookstore.Req.CartAddReq;
import bookstore.Req.CartBuyReq;
import bookstore.Req.StoreMakeCartReq;
import bookstore.Req.StoreSearchReq;
import io.atomix.catalyst.concurrent.Futures;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Connection;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import log.*;
import manager.Communicate;
import pt.haslab.ekit.Clique;
import pt.haslab.ekit.Log;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    public static void main(String[] args) throws Exception {
        //Initial settings
        Transport t = new NettyTransport();
        SingleThreadContext tc = new SingleThreadContext("srv-%d", new Serializer());
        Address address = new Address(":10000");
        DO d = new DO(new Address("127.0.0.1:10000"));
        Log log = new Log("log_bookstore");

        // Regist Messages and Handlers
        registMsg(tc);
        registLogHandlers(log, t, tc, address, d);

        System.out.println("Server running...");
    }

    private static void registLogHandlers(Log log, Transport t, ThreadContext tc, Address address, DO d) {
        log.handler(Prepare.class, (sender, msg)-> {
            System.out.println("Prepare");
        });
        log.handler(Commit.class, (sender, msg)-> {
            System.out.println("Commit");
        });
        log.handler(Abort.class, (sender, msg)->{
            System.out.println("Abort");
        });
        log.handler(Data.class, (sender, msg) -> {

        });
        log.open().thenRun(()-> {
            registHandlers(t, tc, address, d);
            Store s = new StoreImp();
            d.oExport(s);

            // Save initial store
            log.append(s);
        });
    }

    private static void registLog(Connection c) {
        c.handler(Prepare.class, (msg)-> {
            System.out.println("Prepare");
        });
        c.handler(Commit.class, (msg)-> {
            System.out.println("Commit");
        });
        c.handler(Ok.class, (msg)->{
            System.out.println("Ok");
        });
        c.handler(Abort.class, (msg)->{
            System.out.println("Abort");
        });
        c.handler(Rollback.class, (msg) ->{
            System.out.println("Rollback");
        });
    }

    private static void registMsg(ThreadContext tc){
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
    }

    private static void registHandlers(Transport t, ThreadContext tc, Address address, DO d){
        tc.execute( () -> {
            t.server().listen(address, (c) -> {
                c.handler(StoreSearchReq.class, (m) -> {
                    Store s = (Store) d.getElement(m.storeid);
                    Book b = null;
                    try{ b = s.search(m.title); }
                    catch(Exception e){ System.out.println("SearchError: " + e.getMessage()); }
                    ObjRef ref = d.oExport(b);

                    return Futures.completedFuture(new StoreSearchRep(ref));
                });
                c.handler(StoreMakeCartReq.class, (m) -> {
                    Store s = (Store) d.getElement(m.storeid);
                    Cart cart = null;
                    try{ cart = s.newCart(); }
                    catch(Exception e){ System.out.println("MakeCartError: " + e.getMessage()); }
                    ObjRef ref = d.oExport(cart);

                    return Futures.completedFuture(new StoreMakeCartRep(ref));
                });
                c.handler(CartAddReq.class, (m) -> {
                    Cart cart = (Cart) d.getElement(m.cartid);
                    Book b = (Book) d.getElement(m.isbn);

                    return Futures.completedFuture(new CartAddRep(cart.add(b)));
                });
                c.handler(CartBuyReq.class, (m) -> {
                    Cart cart = (Cart) d.getElement(m.cartid);

                    return Futures.completedFuture(new CartBuyRep(true, cart.buy()));
                });
                registLog(c);
            });
        });
    }
}
