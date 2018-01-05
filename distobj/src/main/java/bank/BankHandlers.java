package bank;

import DO.DO;
import DO.ObjRef;
import DO.Obj;
import DO.Server;
import DO.Backup;
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
                    BankImp b = (BankImp) d.getElement(m.bankid);
                    Account ac = b.access(m.id);
                    try{
                        registInManager(m.ctx, b);
                    }
                    catch(Exception e){ e.printStackTrace();}

                    return Futures.completedFuture(new accessRep(d.oExport((Obj) ac)));
                });
                c.handler(transferReq.class, (m) -> {
                    Account ac = (Account) d.getElement(m.accountid);
                    Account aOther = null;
                    try {
                        aOther = (Account) d.oImport(m.ref);
                        if(aOther == null)
                            aOther = (Account) d.getElement(m.accountid);

                        ac.transfer(aOther, m.value);
                    } catch (UnexpectedException e) { e.printStackTrace(); }

                    return Futures.completedFuture(new transferRep(true));
                });
                c.handler(creditReq.class, (m) -> {
                    Account ac = (Account) d.getElement(m.accountid);
                    ac.credit(m.value);

                    return Futures.completedFuture(new creditRep(true));
                });
                c.handler(debitReq.class, (m) -> {
                    Account ac = (Account) d.getElement(m.accountid);
                    ac.debit(m.value);

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
    }
}
