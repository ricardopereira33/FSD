package manager.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;

public class NewResourceReq implements CatalystSerializable {
    public int txid;
    public int rescid;
    public Address address;

    public NewResourceReq() {}

    public NewResourceReq(int txid, int rescid, Address address) {
        this.txid = txid;
        this.rescid = rescid;
        this.address = address;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(txid);
        bufferOutput.writeInt(rescid);
        serializer.writeObject(address);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        txid = bufferInput.readInt();
        rescid = bufferInput.readInt();
        address = serializer.readObject(bufferInput);
    }
}
