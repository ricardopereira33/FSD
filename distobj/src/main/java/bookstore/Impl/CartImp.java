/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore.Impl;

import bank.Data.Invoice;
import bookstore.Interfaces.Cart;
import bookstore.Interfaces.Store;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ricardo
 */
public class CartImp implements Cart, CatalystSerializable {
    private List<Book> content;
    private Store s;

    public CartImp(){}

    public CartImp(Store s){
        content = new ArrayList<>();
        this.s = (Store) s;
    }

    @Override
    public boolean add(Book b) {
        content.add(b);
        return true;
    }

    @Override
    public Invoice buy() {
        int total = content.stream().mapToInt(Book::getPrice).sum();
        s.addHistory(total, content);
        content.clear();
        return new Invoice(total, "store");
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        serializer.writeObject(content, bufferOutput);
        serializer.writeObject(s, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        content = serializer.readObject(bufferInput);
        s = serializer.readObject(bufferInput);
    }
}
