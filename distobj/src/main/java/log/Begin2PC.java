package log;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import manager.Data.Context;

public class Begin2PC implements CatalystSerializable{
    public int txid;
    public int managerid;

    public Begin2PC(){}

    public Begin2PC(int txid, int managerid){
        this.txid = txid;
        this.managerid = managerid;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(txid);
        bufferOutput.writeInt(managerid);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        txid = bufferInput.readInt();
        managerid = bufferInput.readInt();
    }
}
