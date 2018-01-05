package bookstore.Req;

import bookstore.Impl.Book;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import manager.Data.Context;

import java.util.List;

public class addHistoryReq implements CatalystSerializable {
    public int storeid;
    public int value;
    public List<Book> list;
    public Context ctx;

    public addHistoryReq(){}

    public addHistoryReq(int storeid, int value, List<Book> list, Context ctx){
        this.storeid = storeid;
        this.value = value;
        this.list = list;
        this.ctx = ctx;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(storeid);
        bufferOutput.writeInt(value);
        serializer.writeObject(list, bufferOutput);
        serializer.writeObject(ctx, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
       storeid = bufferInput.readInt();
       value = bufferInput.readInt();
       list = serializer.readObject(bufferInput);
       ctx = serializer.readObject(bufferInput);
    }
}
