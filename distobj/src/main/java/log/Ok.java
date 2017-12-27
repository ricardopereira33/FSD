package log;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class Ok implements CatalystSerializable {
    public String s;
    public int txid;

    public Ok() {}

    public Ok(String s, int txid) {
        this.s = s;
        this.txid = txid;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeString(s);
        bufferOutput.writeInt(txid);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        s = bufferInput.readString();
        txid = bufferInput.readInt();
    }
}
