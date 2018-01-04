package bank.Req;

import DO.ObjRef;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import manager.Data.Context;

public class transferReq implements CatalystSerializable {
    public int accountid;
    public int value;
    public ObjRef ref;
    public Context ctx;

    public transferReq() {}

    public transferReq(int id, int value, ObjRef ref, Context ctx) {
        this.accountid = id;
        this.value = value;
        this.ref = ref;
        this.ctx = ctx;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(accountid);
        bufferOutput.writeInt(value);
        serializer.writeObject(ref, bufferOutput);
        serializer.writeObject(ctx, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        accountid = bufferInput.readInt();
        value = bufferInput.readInt();
        ref = serializer.readObject(bufferInput);
        ctx = serializer.readObject(bufferInput);
    }
}
