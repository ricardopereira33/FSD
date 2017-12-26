package manager.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import manager.Data.Context;

public class CommitReq implements CatalystSerializable {
    public Context c;

    public CommitReq(Context context) {
        c = context;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        serializer.writeObject(c);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        c = serializer.readObject(bufferInput);
    }
}
