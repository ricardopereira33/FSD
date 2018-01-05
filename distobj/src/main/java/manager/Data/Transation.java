package manager.Data;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transation implements CatalystSerializable{
    private int id;
    private List<Integer> regist;
    private Map<Integer, Integer> registId;
    private int idRes;
    private Map<Integer, Address> address;
    private boolean valid;

    public Transation(){}

    public Transation(int count, Address addrs) {
        this.id = count;
        this.regist = new ArrayList<>();
        this.regist.add(0);
        this.address = new HashMap<>();
        this.registId = new HashMap<>();
        this.address.put(0, new Address(addrs.host()+":"+(addrs.port()+1)));
        this.valid = true;
        this.idRes = 0;
    }

    public int addAddress(int id, Address address){
        if(!this.address.containsKey(id)){
            this.address.put(id, new Address(address.host()+":"+(address.port()+1)));
            idRes++;
            this.registId.put(id, idRes);
        }
        return this.registId.get(id);
    }

    public List<Address> getAddress(){
        List<Address> list = new ArrayList<>(address.values());
        return list;
    }

    public boolean arrived(int resource) {
        if(!regist.contains(resource))
            regist.add(resource);

        if(regist.size() == address.size()){
            regist.clear();
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

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(id);
        serializer.writeObject(regist, bufferOutput);
        serializer.writeObject(registId, bufferOutput);
        bufferOutput.writeInt(idRes);
        serializer.writeObject(address, bufferOutput);
        bufferOutput.writeBoolean(valid);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        id = bufferInput.readInt();
        regist = serializer.readObject(bufferInput);
        registId = serializer.readObject(bufferInput);
        idRes = bufferInput.readInt();
        address = serializer.readObject(bufferInput);
        valid = bufferInput.readBoolean();
    }
}
