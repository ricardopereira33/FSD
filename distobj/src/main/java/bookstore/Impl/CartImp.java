/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore.Impl;

import bookstore.Interfaces.Book;
import bookstore.Interfaces.Cart;
import bookstore.Interfaces.Store;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ricardo
 */
public class CartImp implements Cart {
    private List<Book> content;
    private StoreImp s;

    public CartImp(Store s){
        content = new ArrayList<>();
        this.s = (StoreImp) s;
    }

    @Override
    public boolean add(Book b) {
        content.add(b);
        return true;
    }

    @Override
    public int buy() {
        s.addHistory(content);
        int total = content.stream().mapToInt(Book::getPrice).sum();
        content.clear();
        return total;
    }
  }
