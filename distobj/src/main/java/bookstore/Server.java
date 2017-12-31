package bookstore;

import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import pt.haslab.ekit.Log;
import DO.DO;

public class Server {

    public static void main(String[] args) throws Exception {
        //Initial settings
        Transport t = new NettyTransport();
        SingleThreadContext tc = new SingleThreadContext("srv-%d", new Serializer());
        Address address = new Address(":1235");
        DO d = new DO(new Address("127.0.0.1:1235"));
        Log log = new Log("log_bookstore");

        StoreHandlers sh = new StoreHandlers(t, tc, address, d, log);
        // Regist Messages and Handlers
        sh.exe();
    }
}
