package DO;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class Snapshot implements CatalystSerializable{
    private DO d;

    public Snapshot(){}

    public Snapshot(DO d){
        this.d = d.clone();
    }

    public DO getD() {
        return d;
    }

    public void setD(DO d) {
        this.d = d;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {

    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {

    }
}
