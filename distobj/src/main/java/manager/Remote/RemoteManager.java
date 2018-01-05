package manager.Remote;

import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Connection;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import manager.Data.Context;
import manager.Interfaces.Manager;
import manager.Rep.CommitRep;
import manager.Rep.ContextRep;
import manager.Rep.NewResourceRep;
import manager.Req.CommitReq;
import manager.Req.ContextReq;
import manager.Req.NewResourceReq;

import java.util.concurrent.ExecutionException;

public class RemoteManager implements Manager{
    private Connection c;
    private ThreadContext tc;
    private Address address;
    private Transport t;
    private int id;
    public static ThreadLocal<Context> ctx = new ThreadLocal<Context>();

    public RemoteManager(int id, Address address) throws ExecutionException, InterruptedException {
        t = new NettyTransport();
        tc = new SingleThreadContext("srv-%d", new Serializer());
        this.address = address;
        this.id = id;
        registeMsg();

        c = tc.execute(() ->
                t.client().connect(address)
        ).join().get();
    }

    private void registeMsg() {
        tc.serializer().register(ContextReq.class);
        tc.serializer().register(ContextRep.class);
        tc.serializer().register(CommitReq.class);
        tc.serializer().register(CommitRep.class);
        tc.serializer().register(NewResourceRep.class);
        tc.serializer().register(NewResourceReq.class);
    }

    @Override
    public void begin() {
        ContextRep r = null;
        try {
            r = (ContextRep) tc.execute(() ->
                    c.sendAndReceive(new ContextReq(id))
            ).join().get();
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        ctx.set(new Context(r.txid, r.address));
    }

    @Override
    public boolean commit() {
        CommitRep r = null;
        Context con = ctx.get();
        try {
            r = (CommitRep) tc.execute(() ->
                    c.sendAndReceive(new CommitReq(con, id))
            ).join().get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        ctx.set(null);
        return r.ok;
    }
}
