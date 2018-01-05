package DO;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import java.rmi.UnexpectedException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Ricardo
 */
public class DO {
    private Address address;
    private Util u;
    private Map<Integer, Obj> objs;
    private AtomicInteger id;

    public DO(){
        u = new Util();
    }

    public DO(Address address) {
        u = new Util();
        this.objs = new HashMap<>();
        this.address = address;
        this.id = new AtomicInteger(0);
    }

    public DO(Address address[], int id) {
        u = new Util();
        this.objs = new HashMap<>();
        this.address = address[id];
        this.id = new AtomicInteger(0);
    }

    public Object getElement(int index){
        return objs.get(index);
    }

    public ObjRef oExport(Obj o){
        int idOR = id.incrementAndGet();
        objs.put(idOR, o);

        return new ObjRef(address, idOR, o.getClass().getSimpleName());
    }
    
    public Object oImport(ObjRef or) throws UnexpectedException{
        if(this.address != null && address.port() == this.address.port())
            return null;

        switch(or.cls){
            case "Cart"   : return u.makeCart(or);
            case "Store"  : return u.makeStore(or);
            case "Bank"   : return u.makeBank(or);
            case "Account": return u.makeAccount(or);
            case "Manager": return u.makeManager(or);
        }
        return null;
    }

    public DO clone(){
        DO res = new DO(this.address);
        for(Obj o: objs.values()){
            res.oExport(o);
        }
        return res;
    }

    public void print() {
        objs.values().stream().forEach((o) -> System.out.println("Obj: "+o));
    }

    public void update(Map<Integer, Obj> list) {
        for(Map.Entry<Integer, Obj> e : list.entrySet()){
            objs.put(e.getKey(), e.getValue());
        }
    }
}
