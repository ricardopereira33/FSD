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
import manager.Req.CommitReq;
import manager.Req.ContextReq;

import java.util.concurrent.ExecutionException;

public class RemoteManager implements Manager{
    private final Connection c;
    private final ThreadContext tc;
    private final Address address;
    private Context context;

    public RemoteManager(int id, Address address) throws ExecutionException, InterruptedException {
        Transport t = new NettyTransport();
        tc = new SingleThreadContext("srv-%d", new Serializer());
        this.address = address;

        registeMsg();

        c = tc.execute(() ->
                t.client().connect(address)
        ).join().get();
    }

    private void registeMsg() {
        tc.serializer().register(ContextReq.class);
        tc.serializer().register(ContextRep.class);
    }


    @Override
    public void begin() {
        ContextRep r = null;
        try {
            r = (ContextRep) tc.execute(() ->
                    c.sendAndReceive(new ContextReq())
            ).join().get();
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        this.context = r.c;
    }

    @Override
    public boolean commit() {
        CommitRep r = null;
        try {
            r = (CommitRep) tc.execute(() ->
                    c.sendAndReceive(new CommitReq(context))
            ).join().get();
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return false;
        }
        return r.ok;
    }
}
