package bank.Req;

import DO.ObjRef;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import manager.Data.Context;

public class creditReq implements CatalystSerializable {
    public Context ctx;
    public int accountid;
    public int value;

    public creditReq() {}

    public creditReq(int id, int value, Context ctx) {
        this.accountid = id;
        this.value = value;
        this.ctx = ctx;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(accountid);
        bufferOutput.writeInt(value);
        serializer.writeObject(ctx, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        accountid = bufferInput.readInt();
        value = bufferInput.readInt();
        ctx = serializer.readObject(bufferInput);
    }
}
