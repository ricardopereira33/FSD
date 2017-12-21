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
import java.io.IOException;
import pt.haslab.ekit.Clique;
import pt.haslab.ekit.Log;

/**
 *
 * @author Ricardo
 */
public class Main {
    
    public static void main(String[] args) throws Exception{
        Address[] addresses = new Address[]{
            new Address("127.0.0.1:1234"),
            new Address("127.0.0.1:1235"),
            new Address("127.0.0.1:1236"),
        };
        int id = 2;
  
        Transport t = new NettyTransport();
        ThreadContext tc = new SingleThreadContext("proto-%d", new Serializer());
        Counter count = new Counter(0);
        // log_0 is coord log
        Log l = new Log("log_"+id);
        
        tc.execute(()-> {
            l.handler(Prepare.class, (sender, msg)-> {
                System.out.println("Prepare");
                //l.append(msg);
            });
            l.handler(Commit.class, (sender, msg)-> {
                System.out.println("Commit");
            });
            l.handler(Integer.class, (sender, msg)-> {
                count.setNum(msg);
            });
            l.handler(Abort.class, (sender, msg)->{
                System.out.println("Abort");
                //l.append(msg);
            });
            l.open().thenRun(()-> { 
                System.out.println("Entrei");
                Clique c = new Clique(t,id,addresses);
                
                Communicate com = new Communicate(addresses, t, tc, c, id, l, count);
                try {
                    com.start();
                } 
                catch (IOException ex) {
                    System.out.println("Erro: "+ex.getMessage());
                }
            }); 
        }).join();
    }
}