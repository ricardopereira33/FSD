package manager.Rep;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import manager.Req.NewResourceReq;

public class NewResourceRep implements CatalystSerializable {
    public boolean ok;
    public int idRes;

    public NewResourceRep(){}

    public NewResourceRep(boolean ok, int idRes){
        this.ok = ok;
        this.idRes = idRes;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeBoolean(ok);
        bufferOutput.writeInt(idRes);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        ok = bufferInput.readBoolean();
        idRes = bufferInput.readInt();
    }
}
