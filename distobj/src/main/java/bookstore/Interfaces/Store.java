/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore.Interfaces;

import bookstore.Impl.BookImp;

/**
 *
 * @author Ricardo
 */
public interface Store {
    
    public Book get(int isbn);
    public Book search(String title) throws Exception;
    public Cart newCart() throws Exception;
}
