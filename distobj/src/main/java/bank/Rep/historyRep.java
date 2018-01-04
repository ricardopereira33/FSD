package bank.Rep;

import DO.ObjRef;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

import java.util.List;

public class historyRep implements CatalystSerializable {
    public List<String> list;

    public historyRep() {}

    public historyRep(List<String> list) {
        this.list = list;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        serializer.writeObject(list, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        list = serializer.readObject(bufferInput);
    }
}
