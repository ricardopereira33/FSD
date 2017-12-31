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
    private int idRes;
    private Map<Integer, Address> address;
    private boolean valid;

    public Transation(int count, Address addrs) {
        this.id = count;
        this.regist = 0;
        this.address = new HashMap<>();
        this.address.put(regist, new Address(addrs.host()+":"+(addrs.port()+1)));
        regist++;
        this.valid = true;
        this.idRes = 0;
    }

    public int addAddress(int id, Address address){
        if(!this.address.containsKey(id)){
            this.address.put(id, new Address(address.host()+":"+(address.port()+1)));
            idRes++;
        }
        return idRes;
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
