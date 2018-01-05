package manager.Impl;

import DO.Obj;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import log.*;
import manager.Data.Context;
import manager.Data.Transation;
import pt.haslab.ekit.Clique;
import pt.haslab.ekit.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ManagerImpl extends Obj {
    private Map<Integer, Transation> trans;
    private int count;
    private ThreadContext tc;
    private Transport tran;
    private Clique cli;

    public ManagerImpl(){
        this.trans = new HashMap<>();
        this.count = 1;
        this.tc = new SingleThreadContext("proto-%d", new Serializer());
        this.tran = new NettyTransport();
        this.cli = null;
    }

    public ManagerImpl(Log log) {
        super();
        this.trans = new HashMap<>();
        this.count = 1;
        this.tc = new SingleThreadContext("proto-%d", new Serializer());
        this.tran = new NettyTransport();
        this.cli = null;
    }

    public int newTransation(Address addrs){
        Transation t = new Transation(count, addrs);
        trans.put(count,t);
        count++;
        return count-1;
    }

    public int addResource(int txid, Address address){
        Transation t = trans.get(txid);
        return t.addAddress(address.port(), address);
    }

    public boolean start2PC(int txid, Address ad, Log log, boolean condi) {
        Transation t = trans.get(txid);
        List<Address> list = t.getAddress();
        Address[] add = list.toArray(new Address[list.size()]);

        cli = new Clique(tran,0,add);

        tc.execute(() -> {
            cli.handler(Abort.class, (s, m) -> {
                System.out.println("Abort");
                Transation tt = trans.get(txid);
                tt.setValid(false);
                log.append(m);
            });
            cli.handler(Ok.class, (s, m) -> {
                System.out.println("Ok");
                checkTransation(txid, cli, add.length, s, log);
            });
            cli.open().thenRun(() -> {
                System.out.println("open");
                if(condi){
                    IntStream.range(1, add.length)
                            .forEach(i -> cli.send(i, new Rollback("Rollback")));
                }
                else{
                    IntStream.range(1, add.length)
                            .forEach(i -> cli.send(i, new Prepare("Prepare", new Context(txid, ad))));
                }
            });
            cli.onException((e) -> e.printStackTrace());
        }).join();
        return true;
    }

    public void checkTransation(int txid, Clique cli, int size, int resource, Log log){
        Transation t = trans.get(txid);
        if(t.arrived(resource)){
            if(!t.isValid()){
                IntStream.range(1, size)
                        .forEach(i -> cli.send(i, new Rollback("Rollback")));
                log.append(new Abort("Abort"));
            }
            else{
                IntStream.range(1, size)
                        .forEach(i -> cli.send(i, new Commit("Commit")));
                log.append(new Commit("Commit"));
            }
        }
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(count);
        serializer.writeObject(trans, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        count = bufferInput.readInt();
        trans = serializer.readObject(bufferInput);
    }
}
