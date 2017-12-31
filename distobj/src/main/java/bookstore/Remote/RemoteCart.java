package bookstore.Remote;

import bookstore.Interfaces.Book;
import bookstore.Rep.CartBuyRep;
import bookstore.Req.CartBuyReq;
import DO.Util;
import bookstore.Interfaces.Cart;
import bookstore.Rep.CartAddRep;
import bookstore.Req.CartAddReq;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Connection;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import manager.Data.Context;
import manager.Remote.RemoteManager;

public class RemoteCart implements Cart{
    private final ThreadContext tc;
    private final Connection c;
    private final Address address;
    private int id;
    private Util u;
    
    public RemoteCart(int id, Address address) throws Exception {
        Transport t = new NettyTransport();
        u = new Util();
        tc = new SingleThreadContext("srv-%d", new Serializer());
        this.address = address;
        this.id = id;
        registeMsg();

        c = tc.execute(() ->
                t.client().connect(address)
        ).join().get();
    }

    private void registeMsg() {
        tc.serializer().register(CartAddReq.class);
        tc.serializer().register(CartAddRep.class);
        tc.serializer().register(CartBuyReq.class);
        tc.serializer().register(CartBuyRep.class);
    }

    @Override
    public boolean add(Book b) {
        CartAddRep r = null;
        Context ctx = RemoteManager.ctx.get();
        try {
            r = (CartAddRep) tc.execute(() ->
                    c.sendAndReceive(new CartAddReq(id, b.getIsbn(), ctx))
            ).join().get();
            
            return r.ok;
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        
        return false;
    }

    @Override
    public int buy() {
        CartBuyRep r = null;
        Context ctx = RemoteManager.ctx.get();
        try {
            r = (CartBuyRep) tc.execute(() ->
                    c.sendAndReceive(new CartBuyReq(id, ctx))
            ).join().get();
            
            return r.price;
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        
        return 0;
    }
}
