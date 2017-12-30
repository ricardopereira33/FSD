package bookstore;

import bookstore.Data.*;
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
    private Map<Integer, Object> objs;
    private AtomicInteger id;
    
    public DO(Address address) {
        u = new Util();
        this.objs = new HashMap<>();
        this.address = address;
        this.id = new AtomicInteger(0);
    }

    public Object getElement(int index){
        return objs.get(index);
    }

    public ObjRef oExport(Object o){
        int idOR = id.incrementAndGet();
        objs.put(idOR, o);

        return new ObjRef(address, idOR, o.getClass().getSimpleName());
    }
    
    public Object oImport(ObjRef or) throws UnexpectedException{
        switch(or.cls){
            case "Bank"   : return u.makeCart(or);
            case "Store"  : return u.makeStore(or);
            case "Book"   : return u.makeBook(or);
        }
        return null;
    }
}
