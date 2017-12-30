package manager;

import io.atomix.catalyst.concurrent.Futures;
import log.*;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;

import manager.Data.Transations;
import manager.Rep.CommitRep;
import manager.Rep.ContextRep;
import manager.Rep.NewResourceRep;
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
        Address address = new Address(":1434");
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
            l.handler(Abort.class, (sender, msg)->{
                System.out.println("Abort");
            });
            l.open().thenRun(()-> { 
                System.out.println("Entrei");
                registMsg(tc);
                registHandlers(address, t, tc, txs, l);
            }); 
        }).join();
    }

    private static void registMsg(ThreadContext tc){
        tc.serializer().register(Abort.class);
        tc.serializer().register(Commit.class);
        tc.serializer().register(Ok.class);
        tc.serializer().register(Prepare.class);
        tc.serializer().register(Rollback.class);
        tc.serializer().register(ContextReq.class);
        tc.serializer().register(ContextRep.class);
        tc.serializer().register(NewResourceReq.class);
        tc.serializer().register(NewResourceRep.class);
        tc.serializer().register(CommitReq.class);
        tc.serializer().register(CommitRep.class);
    }

    private static void registHandlers(Address address, Transport t, ThreadContext tc, Transations txs, Log l) {
        tc.execute( () -> {
            t.server().listen(address, (c) -> {
                c.handler(ContextReq.class, (m) -> {
                    // new Transation
                    System.out.println("Recebi um begin.");
                    int txid = txs.newTransation(address);
                    return Futures.completedFuture(new ContextRep(txid, address));
                });
                c.handler(NewResourceReq.class, (m) -> {
                    // add resource in the Transation
                    System.out.println("Novo user :D");
                    txs.addResource(m.txid, m.address, m.rescid);
                    System.out.println("Inscrito");
                    return Futures.completedFuture(new NewResourceRep(true));
                });
                c.handler(CommitReq.class, (m) -> {
                    // end Transation
                    System.out.println("Fim :(.");
                    try{ txs.start2PC(m.txid, address);}
                    catch(Exception e){
                        System.out.println("Erro: " + e.getMessage());
                    }
                    return Futures.completedFuture(new CommitRep(true));
                });
                c.onException(e->{
                    System.out.println("Erro: "+ e.getMessage() +".");
                });
            });
        });
    }
}