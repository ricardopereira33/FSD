package log;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import manager.Data.Context;

public class askManagerRep implements CatalystSerializable{
    public String status;

    public askManagerRep(){}

    public askManagerRep(String status){
        this.status = status;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeString(status);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        status = bufferInput.readString();
    }
}
