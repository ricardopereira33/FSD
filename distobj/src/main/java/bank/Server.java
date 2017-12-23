package bank;

import bank.Impl.BankImp;
import bank.Interfaces.Bank;
import bank.Rep.TransferRep;
import bank.Req.TransferReq;
import bookstore.DO;
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
import pt.haslab.ekit.Log;

public class Server {
    public static void main(String[] args) throws Exception {
        //Initial settings
        Transport t = new NettyTransport();
        SingleThreadContext tc = new SingleThreadContext("srv-%d", new Serializer());
        Address address = new Address(":10101");
        DO d = new DO(new Address("127.0.0.1:10101"));
        Log log = new Log("log_bank");

        // Regist Messages and Handlers

        registMsg(tc);
        registHandlers(t, tc, address, d);
        registLogHandlers(log);

        Bank b = new BankImp();
        d.oExport(b);

        // Save initial store
        log.append(b);

        System.out.println("Server running...");
    }

    private static void registLogHandlers(Log log) {
        log.handler(Prepare.class, (sender, msg)-> {
            System.out.println("Prepare");
        });
        log.handler(Commit.class, (sender, msg)-> {
            System.out.println("Commit");
        });
        log.handler(Abort.class, (sender, msg)->{
            System.out.println("Abort");
        });
        log.open().thenRun(()-> {

        });
    }

    private static void registLog(Connection c) {
        c.handler(Prepare.class, (msg)-> {
            System.out.println("Prepare");
        });
        c.handler(Commit.class, (msg)-> {
            System.out.println("Commit");
        });
        c.handler(Ok.class, (msg)->{
            System.out.println("Ok");
        });
        c.handler(Abort.class, (msg)->{
            System.out.println("Abort");
        });
        c.handler(Rollback.class, (msg) ->{
            System.out.println("Rollback");
        });
    }

    private static void registMsg(ThreadContext tc){
        tc.serializer().register(TransferRep.class);
        tc.serializer().register(TransferReq.class);
    }

    private static void registHandlers(Transport t, ThreadContext tc, Address address, DO d){
        tc.execute( () -> {
            t.server().listen(address, (c) -> {
                c.handler(TransferReq.class, (m) -> {
                    Bank b = (Bank) d.getElement(m.bankid);
                    boolean res = b.transfer(m.recv, m.send, m.value);

                    return Futures.completedFuture(new TransferRep(res));
                });
                registLog(c);
            });
        });
    }

}
