/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore.Interfaces;

import bookstore.Impl.Book;

import java.util.List;

/**
 *
 * @author Ricardo
 */
public interface Store {
    Book get(int isbn);
    Book search(String title) throws Exception;
    Cart newCart() throws Exception;
    void addHistory(int value, List<Book> content);
}
