package manager.Rep;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import manager.Data.Context;

public class ContextRep implements CatalystSerializable{
    public Context c;

    public ContextRep(){}

    public ContextRep(Context c){
        this.c = c;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
       serializer.writeObject(c);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        c = serializer.readObject(bufferInput);
    }
}
