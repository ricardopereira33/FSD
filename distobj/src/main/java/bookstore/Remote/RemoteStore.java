package bookstore.Remote;

import bookstore.Data.Util;
import bookstore.Data.ObjRef;
import bookstore.Interfaces.Book;
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
    }

    @Override
    public Book get(int isbn) {
        return null;
    }

    @Override
    public Book search(String title) throws Exception {
        StoreSearchRep r = (StoreSearchRep) tc.execute(() ->
                c.sendAndReceive(new StoreSearchReq(title, id))
        ).join().get();

        return u.makeBook(r.ref);
    }

    @Override
    public Cart newCart() throws Exception {
        StoreMakeCartRep r = (StoreMakeCartRep) tc.execute(() ->
                c.sendAndReceive(new StoreMakeCartReq(id))
        ).join().get();
        
        return u.makeCart(r.ref);
    }
}