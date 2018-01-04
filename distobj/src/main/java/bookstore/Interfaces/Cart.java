/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore.Interfaces;

import bookstore.Impl.Book;

/**
 *
 * @author Ricardo
 */
public interface Cart {
    boolean add(Book b);
    int buy();
}
