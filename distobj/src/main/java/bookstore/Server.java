package bookstore;

import bookstore.Data.Data;
import bookstore.Data.ObjRef;
import bookstore.Impl.StoreImp;
import bookstore.Interfaces.Book;
import bookstore.Interfaces.Cart;
import bookstore.Interfaces.Store;
import bookstore.Rep.CartAddRep;
import bookstore.Rep.CartBuyRep;
import bookstore.Rep.StoreMakeCartRep;
import bookstore.Rep.StoreSearchRep;
import bookstore.Req.CartAddReq;
import bookstore.Req.CartBuyReq;
import bookstore.Req.StoreMakeCartReq;
import bookstore.Req.StoreSearchReq;
import io.atomix.catalyst.concurrent.Futures;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Connection;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import log.*;
import manager.Data.Context;
import manager.Rep.NewResourceRep;
import manager.Req.NewResourceReq;
import pt.haslab.ekit.Log;

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
