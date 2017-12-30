package bookstore.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import manager.Data.Context;

public class CartAddReq implements CatalystSerializable {
    public int cartid;
    public int isbn;
    public int txid;
    public Address address;

    public CartAddReq(){}
    
    public CartAddReq(int cartid, int isbn, Context ctx){
        this.isbn = isbn;
        this.cartid = cartid;
        this.txid = ctx.getTxid();
        this.address = ctx.getAddress();
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(isbn);
        bufferOutput.writeInt(cartid);
        bufferOutput.writeInt(txid);
        serializer.writeObject(address, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        isbn = bufferInput.readInt();
        cartid = bufferInput.readInt();
        txid = bufferInput.readInt();
        address = serializer.readObject(bufferInput);
    }
}
