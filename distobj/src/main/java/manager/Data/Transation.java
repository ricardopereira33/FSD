package manager.Data;

import io.atomix.catalyst.transport.Address;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transation {
    private int id;
    private int regist;
    private Map<Integer, Address> address;

    public Transation(int count) {
        this.id = count;
        this.regist = 0;
        this.address = new HashMap<>();
    }

    public void addAddress(int rescid, Address address){
        this.address.put(rescid, address);
    }

    public List<Address> getAddress(){
        List<Address> list = (List) address.values();
        return list;
    }

    public int getSize() {
        return address.size();
    }

    public boolean arrived() {
        regist++;
        if(regist == address.size()){
            regist = 0;
            return true;
        }
        return false;
    }
}
