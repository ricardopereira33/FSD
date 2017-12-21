package bookstore.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class StoreMakeCartReq implements CatalystSerializable {
    public int storeid;
    
    public StoreMakeCartReq() {}
    
    public StoreMakeCartReq(int id){
        this.storeid = id;
    }
    
    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(storeid);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        storeid = bufferInput.readInt();
    }
}
