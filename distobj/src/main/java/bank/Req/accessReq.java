package bank.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import manager.Data.Context;

public class accessReq implements CatalystSerializable {
    public String id;
    public int bankid;
    public Context ctx;
    
    public accessReq() {}

    public accessReq(String id, int bankid, Context ctx) {
        this.id = id;
        this.bankid = bankid;
        this.ctx = ctx;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeString(id);
        bufferOutput.writeInt(bankid);
        serializer.writeObject(ctx, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        id = bufferInput.readString();
        bankid = bufferInput.readInt();
        ctx = serializer.readObject(bufferInput);
    }
}
