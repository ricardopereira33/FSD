package bank.Remote;

import DO.ObjRef;
import bank.Interfaces.Account;
import bank.Interfaces.Bank;
import bank.Rep.accessRep;
import DO.Util;
import bank.Req.accessReq;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Connection;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import manager.Data.Context;
import manager.Remote.RemoteManager;

public class RemoteBank implements Bank {
    private final ThreadContext tc;
    private final Connection c;
    private final Address address;
    private final Util u;
    private final int id;
    
    public RemoteBank(int id, Address address) throws Exception {
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
        tc.serializer().register(accessRep.class);
        tc.serializer().register(accessReq.class);
        tc.serializer().register(ObjRef.class);
        tc.serializer().register(Address.class);
        tc.serializer().register(Context.class);
    }


    @Override
    public Account access(String id) {
        accessRep r = null;
        Context ctx = RemoteManager.ctx.get();
        try {
            r = (accessRep) tc.execute(() ->
                    c.sendAndReceive(new accessReq(id, this.id, ctx))
            ).join().get();

            return u.makeAccount(r.ref);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
