package fsd.logtest.message;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class Ok implements CatalystSerializable {
    private String s;

    public Ok() {}

    public Ok(String s) {
        this.s = s;
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
