package bookstore.Impl;

import bookstore.Interfaces.Book;
import bookstore.Interfaces.Cart;
import bookstore.Interfaces.Store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreImp implements Store {
    
    public Map<Integer, BookImp> books;
    public Map<Integer, BookImp> history;

    public StoreImp() {
        books = new HashMap<>();
        history = new HashMap<>();
        books.put(1, new BookImp(1, "one", "someone", 10));
        books.put(2, new BookImp(2, "other", "someother", 20));
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

    public void addHistory(List<Book> content) {
        for(Book b: content){
            history.put(b.getIsbn(), (BookImp) b);
        }
    }
}
