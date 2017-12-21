package bookstore;

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
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;

public class Server {
    public static void main(String[] args) throws Exception {
        Transport t = new NettyTransport();
        SingleThreadContext tc = new SingleThreadContext("srv-%d", new Serializer());
        Address address = new Address(":10000");
        DO d = new DO(new Address("127.0.0.1:10000"));

        registMsg(tc);
        registHandlers(t, tc, address, d);

        Store s = new StoreImp();
        d.oExport(s);

        System.out.println("Server running...");
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

                    return Futures.completedFuture(new CartBuyRep(cart.buy()));
                });
            });
        });
    }
}