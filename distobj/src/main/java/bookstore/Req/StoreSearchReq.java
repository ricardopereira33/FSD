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
    public int txid;
    public Address address;

    public StoreSearchReq() { }

    public StoreSearchReq(String title, int id, Context ctx) {
        this.title = title;
        this.storeid = id;
        this.txid = ctx.getTxid();
        this.address = ctx.getAddress();
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeString(title);
        bufferOutput.writeInt(storeid);
        bufferOutput.writeInt(txid);
        serializer.writeObject(address, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        title = bufferInput.readString();
        storeid = bufferInput.readInt();
        txid = bufferInput.readInt();
        address = serializer.readObject(bufferInput);
    }
}
