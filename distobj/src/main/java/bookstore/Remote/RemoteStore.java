package bookstore.Remote;

import DO.Util;
import DO.ObjRef;
import bookstore.Impl.Book;
import bookstore.Interfaces.Cart;
import bookstore.Interfaces.Store;
import bookstore.Rep.StoreMakeCartRep;
import bookstore.Req.StoreSearchReq;
import bookstore.Req.StoreMakeCartReq;
import bookstore.Rep.StoreSearchRep;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Connection;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import manager.Data.Context;
import manager.Remote.RemoteManager;

public class RemoteStore implements Store {
    private final ThreadContext tc;
    private final Connection c;
    private final Address address;
    private final Util u;
    private final int id;
    
    public RemoteStore(int id, Address address) throws Exception {
        Transport t = new NettyTransport();
        u = new Util();
        this.id = id;
        tc = new SingleThreadContext("srv-%d", new Serializer());
        this.address = address;

        registeMsg();

        c = tc.execute(() ->
                t.client().connect(address)
        ).join().get();   
    }

    private void registeMsg(){
        tc.serializer().register(StoreSearchReq.class);
        tc.serializer().register(StoreSearchRep.class);
        tc.serializer().register(StoreMakeCartRep.class);
        tc.serializer().register(StoreMakeCartReq.class);
        tc.serializer().register(ObjRef.class);
        tc.serializer().register(Address.class);
        tc.serializer().register(Context.class);
        tc.serializer().register(Book.class);
    }

    @Override
    public Book get(int isbn) {
        return null;
    }

    @Override
    public Book search(String title) throws Exception {
        Context ctx = RemoteManager.ctx.get();
        StoreSearchRep r = (StoreSearchRep) tc.execute(() ->
                c.sendAndReceive(new StoreSearchReq(title, id, ctx))
        ).join().get();

        return r.b;
    }

    @Override
    public Cart newCart() throws Exception {
        Context ctx = RemoteManager.ctx.get();
        StoreMakeCartRep r = (StoreMakeCartRep) tc.execute(() ->
                c.sendAndReceive(new StoreMakeCartReq(id, ctx))
        ).join().get();
        
        return u.makeCart(r.ref);
    }
}
