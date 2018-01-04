package bank.Req;

import DO.ObjRef;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import manager.Data.Context;

public class getIdReq implements CatalystSerializable {
    public int accountid;
    public Context ctx;

    public getIdReq() {}

    public getIdReq( int id, Context ctx) {
       this.accountid = id;
       this.ctx = ctx;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
       bufferOutput.writeInt(accountid);
       serializer.writeObject(ctx, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        accountid = bufferInput.readInt();
        ctx = serializer.readObject(bufferInput);
    }
}
