package manager.Data;

import io.atomix.catalyst.transport.Address;

import java.util.ArrayList;
import java.util.List;

public class Transation {
    private int id;
    private List<Address> address;

    public Transation(int count) {
        this.id = count;
        this.address = new ArrayList<>();
    }

    public void addAddress(Address address){
        this.address.add(address);
    }

    public List<Address> getAddress(){
        return address;
    }
}
