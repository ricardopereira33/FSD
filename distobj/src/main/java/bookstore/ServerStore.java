package bookstore;

import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import pt.haslab.ekit.Log;
import DO.DO;

public class ServerStore {

    public static void main(String[] args) throws Exception {
        //Initial settings
        Transport t = new NettyTransport();
        SingleThreadContext tc = new SingleThreadContext("srv-%d", new Serializer());

        Address[] address = new Address[]{
                new Address("127.0.0.1:1135"),
                new Address("127.0.0.1:2134"),
        };
        DO d = new DO(address[0]);
        Log log = new Log("log_bookstore");

        // Regist Messages and Handlers
        StoreHandlers sh = new StoreHandlers(t, tc, address[0], d, log);
        sh.exe();
    }
}
