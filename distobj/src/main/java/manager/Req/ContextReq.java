package manager.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;

public class ContextReq implements CatalystSerializable{
    public int managerid;

    public ContextReq(){}

    public ContextReq(int id){
        this.managerid = id;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(managerid);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        managerid = bufferInput.readInt();
    }
}
