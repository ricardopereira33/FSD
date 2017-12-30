package manager.Data;

import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import log.*;
import pt.haslab.ekit.Clique;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Transations {
    private Map<Integer, Transation> trans;
    private int count;
    private ThreadContext tc;
    private Transport tran;

    public Transations() {
        this.trans = new HashMap<>();
        this.count = 1;
        this.tc = new SingleThreadContext("proto-%d", new Serializer());
        this.tran = new NettyTransport();
    }

    public int newTransation(Address addrs){
        Transation t = new Transation(count, addrs);
        trans.put(count,t);
        count++;
        return count-1;
    }

    public void addResource(int txid, Address address, int rescid){
        Transation t = trans.get(txid);
        t.addAddress(rescid, address);
    }

    public boolean start2PC(int txid, Address ad) {
        Transation t = trans.get(txid);
        List<Address> list = t.getAddress();
        Address[] add = list.toArray(new Address[list.size()]);

        Clique cli = new Clique(tran,0,add);

        System.out.println("1: " + add[0]);
        System.out.println("2: " + add[1]);

        tc.execute(() -> {
            cli.handler(Commit.class, (s, m) -> {
                System.out.println("com");
            });
            cli.handler(Abort.class, (s, m) -> {
                System.out.println("Abort");
                Transation tt = trans.get(txid);
                tt.setValid(false);
            });
            cli.handler(Ok.class, (s, m) -> {
                System.out.println("Ok");
                checkTransation(txid, cli, add.length);
            });
            cli.open().thenRun(() -> {
                System.out.println("open");
                IntStream.range(0, add.length)
                        .forEach(i -> cli.send(i, new Prepare("Prepare", txid)));
            });
        }).join();
        return true;
    }

    public int getSize(int txid) {
        Transation t = trans.get(txid);
        return t.getSize();
    }

    public void checkTransation(int txid, Clique cli, int size){
        Transation t = trans.get(txid);
        if(t.arrived()){
            if(!t.isValid()){
                IntStream.range(1, size)
                        .forEach(i -> cli.send(i, new Rollback("Rollback")));
            }
            else{
                IntStream.range(1, size)
                        .forEach(i -> cli.send(i, new Commit("Commit")));
            }
        }
    }
}
