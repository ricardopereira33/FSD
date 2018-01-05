package manager;

import DO.DO;
import DO.Obj;
import io.atomix.catalyst.concurrent.Futures;
import log.*;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;

import manager.Impl.ManagerImpl;
import manager.Rep.CommitRep;
import manager.Rep.ContextRep;
import manager.Rep.NewResourceRep;
import manager.Req.CommitReq;
import manager.Req.ContextReq;
import manager.Req.NewResourceReq;
import pt.haslab.ekit.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Ricardo
 */
public class ServerManager {
    
    public static void main(String[] args) throws Exception{
        Address address = new Address("127.0.0.1:1434");
        Transport t = new NettyTransport();
        ThreadContext tc = new SingleThreadContext("srv-%d", new Serializer());
        DO d = new DO(address);

        // log_0 is coord log
        Log l = new Log("log_manager");

        ManagerHandlers mh = new ManagerHandlers(t, tc, d, address, l);
        mh.exe();
    }
}