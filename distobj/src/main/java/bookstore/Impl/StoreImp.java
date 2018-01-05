package bookstore.Impl;

import bookstore.Data.Sale;
import bookstore.Interfaces.Cart;
import bookstore.Interfaces.Store;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class StoreImp implements Store, CatalystSerializable {
    public Map<Integer, Book> books;
    public List<Sale> history;
    public ReentrantLock lock;

    public StoreImp() {
        books = new HashMap<>();
        history = new ArrayList<>();
        books.put(1, new Book(1, "one", "someone", 10));
        books.put(2, new Book(2, "other", "someother", 20));
        lock = new ReentrantLock();
    }

    @Override
    public Book get(int isbn) {
        return books.get(isbn);
    }

    @Override
    public Book search(String title) {
        for(Book b: books.values())
            if (b.getTitle().equals(title))
                return b;
        return null;
    }
    
    @Override
    public Cart newCart(){
        return new CartImp(this);
    }

    @Override
    public void addHistory(int value, List<Book> content) {
        history.add(new Sale(value, content));
    }

    public void lock() {
        this.lock.lock();
    }

    public void unlock(){
        this.lock.unlock();
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        serializer.writeObject(history, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        history = serializer.readObject(bufferInput);
    }
}
