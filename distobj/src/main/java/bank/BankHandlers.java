package bank;

import DO.DO;
import DO.ObjRef;
import DO.Obj;
import DO.Server;
import DO.Backup;
import bank.Impl.AccountImp;
import bank.Impl.BankImp;
import bank.Interfaces.Account;
import bank.Rep.*;
import bank.Req.*;
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
import pt.haslab.ekit.Clique;
import pt.haslab.ekit.Log;

import java.rmi.UnexpectedException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BankHandlers extends Server {

    public BankHandlers(Transport t, SingleThreadContext tc, Address address, DO d, Log log) {
        super(t,tc,address,d,log);
    }

    public void exe(){
        Obj b = new BankImp();
        d.oExport(b);

        registMoreMsg();
        registHandlers();

        System.out.println("ServerBank running...");
    }

    private void registHandlers(){
        tc.execute( () -> {
            t.server().listen(address, (c) -> {
                c.handler(accessReq.class, (m) -> {
                    Obj b =  d.getElement(m.bankid);
                    Account ac = ((BankImp) b).access(m.id);
                    try{
                        registInManager(m.ctx, b);
                    }
                    catch(Exception e){ e.printStackTrace();}
                    ObjRef or = d.oExport((Obj) ac);
                    log.append(new Backup(or.id, d.getElement(or.id)));

                    return Futures.completedFuture(new accessRep(or));
                });
                c.handler(transferReq.class, (m) -> {
                    Obj ac =  d.getElement(m.accountid);
                    Account aOther = null;
                    try {
                        aOther = (Account) d.oImport(m.ref);
                        if(aOther == null)
                            aOther = (Account) d.getElement(m.ref.id);

                        ((Account) ac).transfer(aOther, m.value);
                    } catch (UnexpectedException e) { e.printStackTrace(); }
                    log.append(new Backup(m.accountid, ac));
                    log.append(new Backup(m.ref.id, (Obj) aOther));

                    return Futures.completedFuture(new transferRep(true));
                });
                c.handler(creditReq.class, (m) -> {
                    Obj ac = d.getElement(m.accountid);
                    ((Account) ac).credit(m.value);
                    log.append(new Backup(m.accountid, ac));

                    return Futures.completedFuture(new creditRep(true));
                });
                c.handler(debitReq.class, (m) -> {
                    Obj ac = d.getElement(m.accountid);
                    ((Account) ac).debit(m.value);
                    log.append(new Backup(m.accountid, ac));

                    return Futures.completedFuture(new debitRep(true));
                });
                c.handler(getIdReq.class, (m) -> {
                    Account ac = (Account) d.getElement(m.accountid);

                    return Futures.completedFuture(new getIdRep(ac.getId()));
                });
                c.handler(historyReq.class, (m) -> {
                    Account ac = (Account) d.getElement(m.accountid);

                    return Futures.completedFuture(new historyRep(ac.getHistory()));
                });
            });
        });
    }

    private void registMoreMsg(){
        tc.serializer().register(accessRep.class);
        tc.serializer().register(accessReq.class);
        tc.serializer().register(transferRep.class);
        tc.serializer().register(transferReq.class);
        tc.serializer().register(creditRep.class);
        tc.serializer().register(creditReq.class);
        tc.serializer().register(debitRep.class);
        tc.serializer().register(debitReq.class);
        tc.serializer().register(getIdRep.class);
        tc.serializer().register(getIdReq.class);
        tc.serializer().register(historyRep.class);
        tc.serializer().register(historyReq.class);
        tc.serializer().register(Backup.class);
        tc.serializer().register(Obj.class);
        tc.serializer().register(BankImp.class);
        tc.serializer().register(AccountImp.class);
    }
}
