package manager.Data;

import io.atomix.catalyst.transport.Address;

public class Context {
    private int txid;
    private Address address;

    public Context (int txid, Address address){
        this.txid = txid;
        this.address = address;
    }

    public int getTxid() {
        return txid;
    }

    public void setTxid(int txid) {
        this.txid = txid;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
