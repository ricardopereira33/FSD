package manager.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;

public class NewResourceReq implements CatalystSerializable {
    public int txid;
    public int managerid;
    public Address address;

    public NewResourceReq() {}

    public NewResourceReq(int txid, int id, Address address) {
        this.txid = txid;
        this.managerid = id;
        this.address = address;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(txid);
        bufferOutput.writeInt(managerid);
        serializer.writeObject(address,bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        txid = bufferInput.readInt();
        managerid = bufferInput.readInt();
        address = serializer.readObject(bufferInput);
    }
}
