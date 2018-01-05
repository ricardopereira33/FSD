package DO;

import DO.DO;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Connection;
import pt.haslab.ekit.Clique;

public class Backup implements CatalystSerializable{
    private int id;
    private Obj o;

    public Backup(){}

    public Backup(int id, Obj o){
        this.id = id;
        this.o = o;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Obj getO() {
        return o;
    }

    public void setO(Obj o) {
        this.o = o;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(id);
        serializer.writeObject(o, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        id = bufferInput.readInt();
        o = serializer.readObject(bufferInput);
    }
}
