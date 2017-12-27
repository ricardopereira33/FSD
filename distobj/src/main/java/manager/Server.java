package manager;

import io.atomix.catalyst.concurrent.Futures;
import log.*;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;

import manager.Data.Context;
import manager.Data.Transations;
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
        Address address = new Address(":10202");
        Transport t = new NettyTransport();
        ThreadContext tc = new SingleThreadContext("srv-%d", new Serializer());
        Transations txs = new Transations(tc, t);
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
                registHandlers(address, t, tc, txs, l);
            }); 
        }).join();
    }

    private static void registHandlers(Address address, Transport t, ThreadContext tc, Transations txs, Log l) {
        tc.execute( () -> {
            t.server().listen(address, (c) -> {
                c.handler(ContextReq.class, (m) -> {
                    // new Transation
                    int txid = txs.newTransation();
                    Context con = new Context(txid, address);
                    return Futures.completedFuture(new ContextRep(con));
                });
                c.handler(NewResourceReq.class, (m) -> {
                    // add resource in the Transation
                    txs.addResource(m.txid, m.address, m.rescid);
                    return Futures.completedFuture(new NewResourceRep());
                });
                c.handler(CommitReq.class, (m) -> {
                    // end Transation
                    try{ txs.start2PC(m.c.getTxid());}
                    catch(Exception e){
                        System.out.println("Erro: " + e.getMessage());
                    }
                });
                c.handler(Commit.class, (m)-> {
                    System.out.println("Commit");
                    l.append(m);
                });
                c.handler(Ok.class, (m)->{
                    System.out.println("Ok");
                    try{txs.checkTransation(m.txid);}
                    catch(Exception e){
                        System.out.println("Erro: "+ e.getMessage());
                    }
                });
                c.onException(e->{
                    System.out.println("Erro: "+ e.getMessage() +". :(");
                });
            });
        });
    }
}