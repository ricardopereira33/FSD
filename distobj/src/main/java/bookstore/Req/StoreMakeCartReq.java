package bookstore.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import manager.Data.Context;

public class StoreMakeCartReq implements CatalystSerializable {
    public int storeid;
    public Context ctx;
    
    public StoreMakeCartReq() {}
    
    public StoreMakeCartReq(int id, Context ctx){
        this.storeid = id;
        this.ctx = ctx;
    }
    
    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(storeid);
        serializer.writeObject(ctx, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        storeid = bufferInput.readInt();
        ctx = serializer.readObject(bufferInput);
    }
}
