package bookstore;

import DO.DO;
import DO.Backup;
import DO.ObjRef;
import DO.Obj;
import DO.Server;
import bank.Data.Invoice;
import bookstore.Impl.Book;
import bookstore.Impl.CartImp;
import bookstore.Impl.StoreImp;
import bookstore.Interfaces.Cart;
import bookstore.Rep.*;
import bookstore.Req.*;
import io.atomix.catalyst.concurrent.Futures;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import manager.Rep.NewResourceRep;
import manager.Req.NewResourceReq;
import pt.haslab.ekit.Log;

public class StoreHandlers extends Server {

    public StoreHandlers(Transport t, SingleThreadContext tc, Address address, DO d, Log log) {
        super(t,tc,address,d,log);
    }

    public void exe(){
        Obj s = new StoreImp();
        d.oExport(s);

        registMoreMsg();
        registHandlers();

        System.out.println("ServerStore running...");
    }

    private void registHandlers(){
        tc.execute( () -> {
            t.server().listen(address, (c) -> {
                c.handler(StoreSearchReq.class, (m) -> {
                    Obj s = d.getElement(m.storeid);
                    Book b = null;
                    try{
                        b = ((StoreImp) s).search(m.title);
                        registInManager(m.ctx, s);
                    }
                    catch(Exception e){ e.printStackTrace(); }

                    return Futures.completedFuture(new StoreSearchRep(b));
                });
                c.handler(StoreMakeCartReq.class, (m) -> {
                    Obj s = d.getElement(m.storeid);
                    Cart cart = null;
                    try{
                        cart = ((StoreImp) s).newCart();
                        registInManager(m.ctx, s);
                    }
                    catch(Exception e){ e.printStackTrace(); }
                    ObjRef ref = d.oExport((Obj) cart);
                    log.append(new Backup(ref.id, d.getElement(ref.id)));

                    return Futures.completedFuture(new StoreMakeCartRep(ref));
                });
                c.handler(addHistoryReq.class, (m) -> {
                    Obj s = d.getElement(m.storeid);
                    try{
                        ((StoreImp) s).addHistory(m.value, m.list);
                        registInManager(m.ctx, s);
                    }
                    catch(Exception e){ e.printStackTrace(); }
                    log.append(new Backup(m.storeid, s));

                    return Futures.completedFuture(new addHistoryRep(true));
                });
                c.handler(CartAddReq.class, (m) -> {
                    Obj cart = d.getElement(m.cartid);
                    Book b = m.b;
                    try {
                        registInManager(m.ctx, null);
                    }
                    catch (Exception e) { e.printStackTrace(); }
                    boolean ok = ((Cart) cart).add(b);
                    log.append(new Backup(m.cartid, cart));

                    return Futures.completedFuture(new CartAddRep(ok));
                });
                c.handler(CartBuyReq.class, (m) -> {
                    Obj cart = d.getElement(m.cartid);
                    try {
                        registInManager(m.ctx, null);
                    }
                    catch (Exception e) { e.printStackTrace(); }
                    Invoice i = ((Cart) cart).buy();
                    log.append(new Backup(m.cartid, (Obj) cart));

                    return Futures.completedFuture(new CartBuyRep(true, i));
                });
            });
        });
    }

    private void registMoreMsg(){
        tc.serializer().register(StoreSearchReq.class);
        tc.serializer().register(StoreSearchRep.class);
        tc.serializer().register(StoreMakeCartRep.class);
        tc.serializer().register(StoreMakeCartReq.class);
        tc.serializer().register(CartAddReq.class);
        tc.serializer().register(CartAddRep.class);
        tc.serializer().register(CartBuyReq.class);
        tc.serializer().register(CartBuyRep.class);
        tc.serializer().register(addHistoryRep.class);
        tc.serializer().register(addHistoryReq.class);
        tc.serializer().register(StoreImp.class);
        tc.serializer().register(CartImp.class);
    }
}
