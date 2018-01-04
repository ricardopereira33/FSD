package bank.Remote;

import DO.ObjRef;
import bank.Interfaces.Account;
import bank.Rep.*;
import DO.Util;
import bank.Req.*;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Connection;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import manager.Data.Context;
import manager.Remote.RemoteManager;

import java.util.List;

public class RemoteAccount implements Account{
    private final ThreadContext tc;
    private final Connection c;
    private final Address address;
    private int id;
    private Util u;
    
    public RemoteAccount(int id, Address address) throws Exception {
        Transport t = new NettyTransport();
        u = new Util();
        tc = new SingleThreadContext("srv-%d", new Serializer());
        this.address = address;
        this.id = id;
        registeMsg();

        c = tc.execute(() ->
                t.client().connect(address)
        ).join().get();
    }

    private void registeMsg() {
        tc.serializer().register(newAccountRep.class);
        tc.serializer().register(newAccountReq.class);
    }

    @Override
    public void transfer(Account ac, int value) {
        transferRep r = null;
        Context ctx = RemoteManager.ctx.get();
        ObjRef or = ((RemoteAccount )ac).getObjRef();
        try {
            r = (transferRep) tc.execute(() ->
                    c.sendAndReceive(new transferReq(id, value, or, ctx))
            ).join().get();

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }


    @Override
    public void credit(int value) {
        creditRep r = null;
        Context ctx = RemoteManager.ctx.get();
        try {
            r = (creditRep) tc.execute(() ->
                    c.sendAndReceive(new creditReq(id, value, ctx))
            ).join().get();

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    @Override
    public void debit(int value) {
        debitRep r = null;
        Context ctx = RemoteManager.ctx.get();
        try {
            r = (debitRep) tc.execute(() ->
                    c.sendAndReceive(new debitReq(id, value, ctx))
            ).join().get();

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    @Override
    public List<String> getHistory() {
        historyRep r = null;
        Context ctx = RemoteManager.ctx.get();
        try {
            r = (historyRep) tc.execute(() ->
                    c.sendAndReceive(new historyReq(id, ctx))
            ).join().get();
            return r.list;
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getId() {
        getIdRep r = null;
        Context ctx = RemoteManager.ctx.get();
        try {
            r = (getIdRep) tc.execute(() ->
                    c.sendAndReceive(new getIdReq(id, ctx))
            ).join().get();
            return r.id;
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }

        return null;
    }

    public ObjRef getObjRef(){
        return new ObjRef(address, id, "Account");
    }
}
