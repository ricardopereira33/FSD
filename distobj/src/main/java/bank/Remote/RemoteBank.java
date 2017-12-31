package bank.Remote;

import bank.Interfaces.Bank;
import bank.Rep.TransferRep;
import bank.Req.TransferReq;
import DO.Util;
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
        tc.serializer().register(TransferRep.class);
        tc.serializer().register(TransferReq.class);
    }

    @Override
    public boolean transfer(String recv, String send, int value) {
        TransferRep r = null;
        Context ctx = RemoteManager.ctx.get();
        try {
            r = (TransferRep) tc.execute(() ->
                    c.sendAndReceive(new TransferReq(recv, send, value, id,ctx))
            ).join().get();

            return r.ok;
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }

        return false;
    }
}
