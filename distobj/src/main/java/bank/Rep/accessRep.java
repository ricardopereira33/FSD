package bank.Rep;

import DO.ObjRef;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class accessRep implements CatalystSerializable {
    public ObjRef ref;
    
    public accessRep() {}

    public accessRep(ObjRef ref) {
        this.ref = ref;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        serializer.writeObject(ref, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        ref = serializer.readObject(bufferInput);
    }
}
