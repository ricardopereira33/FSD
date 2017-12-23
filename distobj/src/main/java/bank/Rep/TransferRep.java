package bank.Rep;

import bookstore.Data.ObjRef;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class TransferRep implements CatalystSerializable {
    public boolean ok;
    
    public TransferRep() {}

    public TransferRep(boolean ok) {
        this.ok = ok;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeBoolean(ok);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        ok = bufferInput.readBoolean();
    }
}
