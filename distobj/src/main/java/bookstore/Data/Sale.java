package bookstore.Data;

import bookstore.Impl.Book;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

import java.util.List;

public class Sale implements CatalystSerializable {
    private int total;
    private List<Book> list;

    public Sale(){}

    public Sale(int total, List<Book> list){
        this.total = total;
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Book> getList() {
        return list;
    }

    public void setList(List<Book> list) {
        this.list = list;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(total);
        serializer.writeObject(list, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        total = bufferInput.readInt();
        list = serializer.readObject(bufferInput);
    }
}
