package manager.Rep;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import manager.Req.NewResourceReq;

public class NewResourceRep implements CatalystSerializable {

    public NewResourceRep(){}

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {

    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {

    }
}
