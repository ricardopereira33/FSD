package manager.Data;

import bank.Impl.Transfer;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Connection;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import log.Abort;
import log.Commit;
import log.Prepare;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Transations {
    private Map<Integer, Transation> trans;
    private int count;
    private boolean valid;
    private ThreadContext tc;
    private Transport tran;

    public Transations(ThreadContext tc, Transport tran) {
        this.trans = new HashMap<>();
        this.count = 1;
        this.tc = tc;
        this.tran = tran;
        this.valid = true;
    }

    public int newTransation(){
        Transation t = new Transation(count);
        count++;
        return count-1;
    }

    public void addResource(int txid, Address address, int rescid){
        Transation t = trans.get(txid);
        t.addAddress(rescid, address);
    }

    public boolean start2PC(int txid) throws ExecutionException, InterruptedException {
        Transation t = trans.get(txid);
        List<Address> list = t.getAddress();
        Connection c = null;

        for(Address a: list){
            tc.execute(() -> tran.client().connect(a))
                    .join().get()
                    .send(new Prepare("Prepare", txid));
        }
        return true;
    }

    public int getSize(int txid) {
        Transation t = trans.get(txid);
        return t.getSize();
    }

    public void checkTransation(int txid) throws ExecutionException, InterruptedException {
        Transation t = trans.get(txid);

        if(t.arrived()){
            List<Address> list = t.getAddress();
            for(Address a: list){
                if(valid){
                    tc.execute(() -> tran.client().connect(a))
                            .join().get()
                            .send(new Commit("Commit"));
                }
                else{
                    tc.execute(() -> tran.client().connect(a))
                            .join().get()
                            .send(new Abort("Abort"));
                }
            }
        }
    }
}
