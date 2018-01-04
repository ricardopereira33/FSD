package bookstore.Rep;

import DO.ObjRef;
import bookstore.Impl.Book;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class StoreSearchRep implements CatalystSerializable {
    public Book b;
    
    public StoreSearchRep() {}

    public StoreSearchRep(Book b) {
        this.b = b;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        serializer.writeObject(b, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        b = serializer.readObject(bufferInput);
    }
}
