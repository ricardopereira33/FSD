/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore.Rep;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

/**
 *
 * @author Ricardo
 */
public class CartBuyRep implements CatalystSerializable {
    public boolean ok;

    public CartBuyRep() {}

    public CartBuyRep(boolean result) {
        this.ok = result;
    }
    
    @Override
    public void writeObject(BufferOutput<?> bo, Serializer srlzr) {
        bo.writeBoolean(ok);
    }

    @Override
    public void readObject(BufferInput<?> bi, Serializer srlzr) {
        ok = bi.readBoolean();
    }
    
}
