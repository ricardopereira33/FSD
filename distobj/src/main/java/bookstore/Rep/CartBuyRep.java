/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore.Rep;

import bank.Data.Invoice;
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
    public Invoice i;

    public CartBuyRep() {}

    public CartBuyRep(boolean result, Invoice i) {
        this.i = i;
        this.ok = result;
    }
    
    @Override
    public void writeObject(BufferOutput<?> bo, Serializer srlzr) {
        srlzr.writeObject(i, bo);
        bo.writeBoolean(ok);
    }

    @Override
    public void readObject(BufferInput<?> bi, Serializer srlzr) {
        i = srlzr.readObject(bi);
        ok = bi.readBoolean();
    }
}
