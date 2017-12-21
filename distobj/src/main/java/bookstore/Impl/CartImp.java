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
    private Store s;

    public CartImp(Store s){
        content = new ArrayList<>();
        this.s = s;
    }

    @Override
    public boolean add(Book b) {
        content.add(b);
        return true;
    }

    @Override
    public boolean buy() {
        content.clear();
        return true;
    }
  }
