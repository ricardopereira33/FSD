package bank.Remote;

import bank.Interfaces.Account;
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

public class RemoteAccount implements Account{
    private final ThreadContext tc;
    private final Connection c;
    private final Address address;
    private int id;
    private Util u;
    
    public RemoteAccount(int id, Address address) throws Exception {
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
        tc.serializer().register(TransferRep.class);
        tc.serializer().register(TransferReq.class);
    }


    @Override
    public int getValue() {
        return 0;
    }

    @Override
    public void setValue(int value) {

    }

    @Override
    public void addValue(int value) {

    }

    @Override
    public void rmValue(int value) {

    }
}
