package manager;

import log.Abort;
import log.Commit;
import log.Prepare;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;

import manager.Data.Transations;
import manager.Req.CommitReq;
import manager.Req.ContextReq;
import manager.Req.NewResourceReq;
import pt.haslab.ekit.Log;

/**
 *
 * @author Ricardo
 */
public class Server {
    
    public static void main(String[] args) throws Exception{
        Address address = new Address(":10202");
        Transport t = new NettyTransport();
        ThreadContext tc = new SingleThreadContext("srv-%d", new Serializer());
        Transations txs = new Transations();
        // log_0 is coord log
        Log l = new Log("log_manager");
        
        tc.execute(()-> {
            l.handler(Prepare.class, (sender, msg)-> {
                System.out.println("Prepare");
            });
            l.handler(Commit.class, (sender, msg)-> {
                System.out.println("Commit");
            });
            l.handler(Integer.class, (sender, msg)-> {

            });
            l.handler(Abort.class, (sender, msg)->{
                System.out.println("Abort");
            });
            l.open().thenRun(()-> { 
                System.out.println("Entrei");
                registHandlers(address, t, tc);
            }); 
        }).join();
    }

    private static void registHandlers(Address address, Transport t, ThreadContext tc) {
        tc.execute( () -> {
            t.server().listen(address, (c) -> {
                c.handler(ContextReq.class, (m) -> {
                    // new Transation

                });
                c.handler(NewResourceReq.class, (m) -> {
                    // add resource in the transation

                });
                c.handler(CommitReq.class, (m) -> {
                    // end Transtion

                });
            });
        });
    }
}