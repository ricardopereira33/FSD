package bank;

import DO.DO;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import pt.haslab.ekit.Log;

public class Server {
    public static void main(String[] args) throws Exception {
        //Initial settings
        Transport t = new NettyTransport();
        SingleThreadContext tc = new SingleThreadContext("srv-%d", new Serializer());
        Address address = new Address("127.0.0.1:1934");
        DO d = new DO(address);
        Log log = new Log("log_bank");

        // Regist Messages and Handlers
        BankHandlers bh = new BankHandlers(t,tc,address,d,log);
        bh.exe();
    }
}
