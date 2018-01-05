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
        
        tc.execute(()-> {
            l.handler(Prepare.class, (sender, msg)-> {
                System.out.println("Log: Prepare");
            });
            l.handler(Commit.class, (sender, msg)-> {
                System.out.println("Log: Commit");
            });
            l.handler(Abort.class, (sender, msg)->{
                System.out.println("Log: Abort");
            });
            l.open().thenRun(()-> { 
                System.out.println("ServerManager running... ");
                Obj txs = new ManagerImpl(l);
                d.oExport(txs);

                registMsg(tc);
                registHandlers(address, t, tc, d, l);
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

    private static void registHandlers(Address address, Transport t, ThreadContext tc, DO d, Log l) {
        tc.execute( () -> {
            t.server().listen(address, (c) -> {
                c.handler(ContextReq.class, (m) -> {
                    // new Transation
                    ManagerImpl txs = (ManagerImpl) d.getElement(m.managerid);
                    int txid = txs.newTransation(address);

                    return Futures.completedFuture(new ContextRep(txid, address));
                });
                c.handler(NewResourceReq.class, (m) -> {
                    // add resource in the Transation
                    ManagerImpl txs = (ManagerImpl) d.getElement(m.managerid);
                    int idRes = txs.addResource(m.txid, m.address);

                    return Futures.completedFuture(new NewResourceRep(true, idRes));
                });
                c.handler(CommitReq.class, (m) -> {
                    // end Transation
                    ManagerImpl txs = (ManagerImpl) d.getElement(m.managerid);
                    try{ txs.start2PC(m.ctx.getTxid(), address);}
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    return Futures.completedFuture(new CommitRep(true));
                });
                c.onException(e->{
                    System.out.println("Erro: "+ e.getMessage());
                });
            });
        });
    }
}