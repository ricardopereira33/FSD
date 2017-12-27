/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

/**
 *
 * @author Ricardo
 */
public class Prepare implements CatalystSerializable {
    public String s;
    public int txid;

    public Prepare() {}

    public Prepare(String s, int txid){
        this.s = s;
        this.txid = txid;
    }

    @Override
    public void writeObject(BufferOutput<?> bo, Serializer srlzr) {
        bo.writeString(s);
        bo.writeInt(txid);
    }

    @Override
    public void readObject(BufferInput<?> bi, Serializer srlzr) {
        s = bi.readString();
        txid = bi.readInt();
    }
}
