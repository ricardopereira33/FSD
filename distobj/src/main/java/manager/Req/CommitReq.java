package manager.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import manager.Data.Context;

public class CommitReq implements CatalystSerializable {
    public int managerid;
    public Context ctx;

    public CommitReq(){}

    public CommitReq(Context context, int id) {
        this.managerid = id;
        this.ctx = context;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(managerid);
        serializer.writeObject(ctx, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        managerid = bufferInput.readInt();
        ctx = serializer.readObject(bufferInput);
    }
}
