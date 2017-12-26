package manager.Rep;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import log.Commit;

public class CommitRep implements CatalystSerializable {
    public boolean ok;
    public CommitRep(){ }

    public CommitRep(boolean valid){
        ok = valid;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeBoolean(ok);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        ok = bufferInput.readBoolean();
    }
}
