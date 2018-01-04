package bank.Req;

import DO.ObjRef;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import manager.Data.Context;

public class debitReq implements CatalystSerializable {
    public int accountid;
    public int value;
    public Context ctx;

    public debitReq() {}

    public debitReq(int id, int value, Context ctx) {
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
        serializer.readObject(bufferInput);
    }
}
