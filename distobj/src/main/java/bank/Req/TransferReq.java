package bank.Req;

import bookstore.Data.ObjRef;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class TransferReq implements CatalystSerializable {
    public String recv, send;
    public int value, bankid;
    
    public TransferReq() {}

    public TransferReq(String recv, String send, int value, int bankid) {
        this.recv = recv;
        this.send = send;
        this.value = value;
        this.bankid = bankid;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeString(recv);
        bufferOutput.writeString(send);
        bufferOutput.writeInt(value);
        bufferOutput.writeInt(bankid);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        recv = bufferInput.readString();
        send = bufferInput.readString();
        value = bufferInput.readInt();
        bankid = bufferInput.readInt();
    }
}
