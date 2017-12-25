package manager;

import io.atomix.catalyst.transport.Connection;
import log.Abort;
import log.Commit;
import log.Prepare;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import java.io.IOException;
import pt.haslab.ekit.Clique;
import pt.haslab.ekit.Log;

/**
 *
 * @author Ricardo
 */
public class Manager {
    
    public static void main(String[] args) throws Exception{
        Address[] addresses = new Address[]{
            new Address("127.0.0.1:1234"),
            new Address("127.0.0.1:1235"),
            new Address("127.0.0.1:1236"),
        };
        Transport t = new NettyTransport();
        ThreadContext tc = new SingleThreadContext("proto-%d", new Serializer());
        Counter count = new Counter(0);

        // log_0 is coord log
        Log l = new Log("log_manager");
        
        tc.execute(()-> {
            l.handler(Prepare.class, (sender, msg)-> {
                System.out.println("Prepare");
            });
            l.handler(Commit.class, (sender, msg)-> {
                System.out.println("Commit");
            });
            l.handler(Abort.class, (sender, msg)->{
                System.out.println("Abort");
            });
            l.open().thenRun(()-> { 
                System.out.println("Entrei");
                Clique c = new Clique(t,0,addresses);

            }); 
        }).join();
    }
}