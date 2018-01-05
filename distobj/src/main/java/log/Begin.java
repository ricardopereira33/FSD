package log;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import manager.Data.Context;

public class Begin implements CatalystSerializable{
    public String status;
    public int txid;
    public int managerid;

    public Begin(){}

    public Begin(String status, int txid, int managerid){
        this.status = status;
        this.txid = txid;
        this.managerid = managerid;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeString(status);
        bufferOutput.writeInt(txid);
        bufferOutput.writeInt(managerid);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        status = bufferInput.readString();
        txid = bufferInput.readInt();
        managerid = bufferInput.readInt();
    }
}
