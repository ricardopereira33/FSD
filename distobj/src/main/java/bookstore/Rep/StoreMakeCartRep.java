package bookstore.Rep;

import bookstore.Data.ObjRef;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

/**
 *
 * @author Ricardo
 */
public class StoreMakeCartRep implements CatalystSerializable{
    public ObjRef ref;
    
    public StoreMakeCartRep() {}
    
    public StoreMakeCartRep(ObjRef ref){
        this.ref = ref;
    }  
    
    @Override
    public void writeObject(BufferOutput<?> bo, Serializer srlzr) {
        srlzr.writeObject(ref, bo);
    }

    @Override
    public void readObject(BufferInput<?> bi, Serializer srlzr) {
        ref = srlzr.readObject(bi);
    }
    
}
