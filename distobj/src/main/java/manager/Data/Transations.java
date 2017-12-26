package manager.Data;

import io.atomix.catalyst.transport.Address;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transations {
    private Map<Integer, Transation> trans;
    private int count;

    public Transations() {
        this.trans = new HashMap<>();
        this.count = 1;
    }

    public int newTransation(){
        Transation t = new Transation(count);
        count++;
        return count-1;
    }

    public void addResource(int txid, Address address){
        Transation t = trans.get(txid);
        t.addAddress(address);
    }
}
