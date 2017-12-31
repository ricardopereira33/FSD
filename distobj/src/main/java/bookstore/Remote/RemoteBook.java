package bookstore.Remote;

import DO.Util;
import bookstore.Interfaces.Book;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Connection;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;

public class RemoteBook implements Book{
    private final ThreadContext tc;
    private final Connection c;
    private final Address address;
    private int id;
    private Util u;

    public RemoteBook(int id, Address address) throws Exception {
        Transport t = new NettyTransport();
        u = new Util();
        tc = new SingleThreadContext("srv-%d", new Serializer());
        this.id = id;
        this.address = address;

        c = tc.execute(() ->
                t.client().connect(address)
        ).join().get();
    }


    @Override
    public int getIsbn() {
        return id;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getAuthor() {
        return null;
    }
    
    @Override
    public int getPrice() { return 0; }

}
