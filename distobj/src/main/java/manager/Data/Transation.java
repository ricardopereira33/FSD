package manager.Data;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transation {
    private int id;
    private int regist;
    private Map<Integer, Address> address;
    private boolean valid;

    public Transation(int count, Address addrs) {
        this.id = count;
        this.regist = 0;
        this.address = new HashMap<>();
        this.address.put(regist, new Address(addrs.host()+":"+(addrs.port()+1)));
        regist++;
        this.valid = true;
    }

    public void addAddress(int rescid, Address address){
        if(!this.address.containsKey(rescid))
            this.address.put(rescid, new Address(address.host()+":"+(address.port()+1)));
    }

    public List<Address> getAddress(){
        List<Address> list = new ArrayList<>(address.values());
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

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }
}
