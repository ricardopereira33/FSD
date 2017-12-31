package bank;

import bank.Impl.BankImp;
import bank.Interfaces.Bank;
import bank.Rep.TransferRep;
import bank.Req.TransferReq;
import bookstore.DO;
import io.atomix.catalyst.concurrent.Futures;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import log.*;
import pt.haslab.ekit.Log;

public class Server {
    public static void main(String[] args) throws Exception {
        //Initial settings
        Transport t = new NettyTransport();
        SingleThreadContext tc = new SingleThreadContext("srv-%d", new Serializer());
        Address address = new Address(":1934");
        DO d = new DO(new Address("127.0.0.1:1934"));
        Log log = new Log("log_bank");

        // Regist Messages and Handlers
        registMsg(tc);
        registLogHandlers(log, t, tc, address, d);

        System.out.println("Server running...");
    }

    private static void registLogHandlers(Log log, Transport t, ThreadContext tc, Address address, DO d) {
        tc.execute(() ->{
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
                registHandlers(t, tc, address, d);
                Bank b = new BankImp();
                d.oExport(b);

                // Save initial store
                log.append(b);
            });
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
            });
        });
    }

}
