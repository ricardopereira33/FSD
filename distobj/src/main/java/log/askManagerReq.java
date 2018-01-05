package log;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import manager.Data.Context;

public class AskManagerReq implements CatalystSerializable{
    public Context ctx;
    public int managerid;

    public AskManagerReq(){}

    public AskManagerReq(Context ctx, int managerid){
        this.ctx = ctx;
        this.managerid = managerid;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        serializer.writeObject(ctx, bufferOutput);
        bufferOutput.writeInt(managerid);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        ctx = serializer.readObject(bufferInput);
        managerid = bufferInput.readInt();
    }
}
