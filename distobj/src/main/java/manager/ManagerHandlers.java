package manager;

import DO.DO;
import DO.Backup;
import DO.Obj;
import io.atomix.catalyst.concurrent.Futures;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import log.*;
import manager.Data.Context;
import manager.Data.Transation;
import manager.Impl.ManagerImpl;
import manager.Interfaces.Manager;
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

public class ManagerHandlers {

    public ThreadContext tc;
    public DO d;
    public Address address;
    public Log l;
    public Transport t;
    public Map<Integer, Object> volatilLog;
    public AtomicInteger index;

    public ManagerHandlers(Transport t, ThreadContext tc, DO d, Address address, Log l) {
        this.t = t;
        this.tc = tc;
        this.d = d;
        this.address = address;
        this.l = l;
        this.volatilLog = new HashMap<>();
        this.index = new AtomicInteger(0);
    }

    public void exe(){
        Obj txs = new ManagerImpl(l);
        d.oExport(txs);

        registMsg(tc);
        registLogHandlers();
    }

    private void registLogHandlers() {
        tc.execute(()-> {
            l.handler(Commit.class, (sender, msg)-> {
                addToVolatil(msg);
            });
            l.handler(Abort.class, (sender, msg)-> {
                addToVolatil(msg);
            });
            l.handler(Begin.class, (sender, msg) ->{
                addToVolatil(msg);
            });
            l.handler(Backup.class, (sender,msg) ->{
                addToVolatil(msg);
            });
            l.open().thenRun(()-> {
                System.out.println("ServerManager running... ");
                readLog();
                registHandlers();
            });
        }).join();
    }

    private void addToVolatil(Object o){
        int i = index.incrementAndGet();
        volatilLog.put(i, o);
    }

    public void readLog() {
        Map<Integer, Obj> list = new HashMap<>();
        boolean beginning = false;
        Begin begin = null;
        for(Map.Entry<Integer, Object> e : volatilLog.entrySet()){
            switch(e.getValue().getClass().getName()){
                case "log.Begin":
                    System.out.println("Log: Begin");
                    beginning = true;
                    begin = (Begin) e.getValue();
                    break;
                case "log.Commit":
                    System.out.println("Log: Commit");
                    beginning = false;
                    d.update(list);
                    d.print();
                    break;
                case "log.Abort":
                    System.out.println("Log: Abort");
                    beginning = false;
                    list.clear();
                    break;
                case "DO.Backup":
                    System.out.println("Log: Backup");
                    Backup b = (Backup)e.getValue();
                    list.put(b.getId(), b.getO());
                    break;
            }
        }
        if(beginning && !list.isEmpty()){
            System.out.println("Entrei");
            d.update(list);
            ManagerImpl mi = (ManagerImpl) d.getElement(begin.managerid);
            mi.start2PC(begin.txid, address, l, false);
        }
        else if(!list.isEmpty()){
            System.out.println("Entrei2");
            d.update(list);
            ManagerImpl mi = (ManagerImpl) d.getElement(begin.managerid);
            mi.start2PC(begin.txid, address, l, true);
        }
    }

    private void registHandlers() {
        tc.execute( () -> {
            t.server().listen(address, (c) -> {
                c.handler(ContextReq.class, (m) -> {
                    // new Transation
                    Obj txs = d.getElement(m.managerid);
                    int txid = ((ManagerImpl) txs).newTransation(address);

                    l.append(new Backup(m.managerid, txs));

                    return Futures.completedFuture(new ContextRep(txid, address));
                });
                c.handler(NewResourceReq.class, (m) -> {
                    // add resource in the Transation
                    ManagerImpl txs = (ManagerImpl) d.getElement(m.managerid);
                    int idRes = txs.addResource(m.txid, m.address);

                    l.append(new Backup(m.managerid, txs));

                    return Futures.completedFuture(new NewResourceRep(true, idRes));
                });
                c.handler(CommitReq.class, (m) -> {
                    // end Transation
                    ManagerImpl txs = (ManagerImpl) d.getElement(m.managerid);
                    try{
                        l.append(new Begin("Begin", m.ctx.getTxid(), m.managerid));
                        txs.start2PC(m.ctx.getTxid(), address, l, false);
                    }
                    catch(Exception e){ e.printStackTrace(); }

                    return Futures.completedFuture(new CommitRep(true));
                });
                c.onException(e->{ e.printStackTrace(); });
            });
        });
    }

    private void registMsg(ThreadContext tc){
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
        tc.serializer().register(ManagerImpl.class);
        tc.serializer().register(Transation.class);
        tc.serializer().register(Obj.class);
        tc.serializer().register(Context.class);
        tc.serializer().register(Begin.class);
    }
}
