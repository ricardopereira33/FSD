package log;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import manager.Data.Context;

public class Rollback implements CatalystSerializable{
    public String s;

    public Rollback(){}

    public Rollback(String rollback) {
        this.s = rollback;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeString(s);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        s = bufferInput.readString();
    }
}
