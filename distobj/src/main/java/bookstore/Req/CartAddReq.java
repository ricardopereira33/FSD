package bookstore.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class CartAddReq implements CatalystSerializable {
    public int cartid;
    public int isbn;
    
    public CartAddReq(){}
    
    public CartAddReq(int cartid, int isbn){
        this.isbn = isbn;
        this.cartid = cartid;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(isbn);
        bufferOutput.writeInt(cartid);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        isbn = bufferInput.readInt();
        cartid = bufferInput.readInt();
    }
}
