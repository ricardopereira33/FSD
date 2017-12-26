package manager.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class NewResourceReq implements CatalystSerializable {
    private int txid;
    private int rescid;

    public NewResourceReq() {}

    public NewResourceReq(int txid, int rescid) {
        this.txid = txid;
        this.rescid = rescid;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(txid);
        bufferOutput.writeInt(rescid);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        txid = bufferInput.readInt();
        rescid = bufferInput.readInt();
    }
}
