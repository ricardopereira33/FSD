/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

/**
 *
 * @author Ricardo
 */
public class CartBuyReq implements CatalystSerializable{
    public int cartid;
    
    public CartBuyReq(){ }
    
    public CartBuyReq(int id) {
        this.cartid = id;
    }

    @Override
    public void writeObject(BufferOutput<?> bo, Serializer srlzr) {
        bo.writeInt(cartid);
    }

    @Override
    public void readObject(BufferInput<?> bi, Serializer srlzr) {
        cartid = bi.readInt();
    }
    
}
