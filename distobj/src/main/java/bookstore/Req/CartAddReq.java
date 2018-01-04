package bookstore.Req;

import bookstore.Impl.Book;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import manager.Data.Context;

public class CartAddReq implements CatalystSerializable {
    public int cartid;
    public Book b;
    public Context ctx;

    public CartAddReq(){}
    
    public CartAddReq(int cartid, Book b, Context ctx){
        this.b = b;
        this.cartid = cartid;
        this.ctx = ctx;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        serializer.writeObject(b, bufferOutput);
        bufferOutput.writeInt(cartid);
        serializer.writeObject(ctx, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        b = serializer.readObject(bufferInput);
        cartid = bufferInput.readInt();
        ctx = serializer.readObject(bufferInput);
    }
}
