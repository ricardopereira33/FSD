package bookstore.Req;



import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import manager.Data.Context;

public class StoreSearchReq implements CatalystSerializable {
    public String title;
    public int storeid;
    public Context ctx;

    public StoreSearchReq() { }

    public StoreSearchReq(String title, int id, Context ctx) {
        this.title = title;
        this.storeid = id;
        this.ctx = ctx;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeString(title);
        bufferOutput.writeInt(storeid);
        serializer.writeObject(ctx, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        title = bufferInput.readString();
        storeid = bufferInput.readInt();
        ctx = serializer.readObject(bufferInput);
    }
}
