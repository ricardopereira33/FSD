/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.Interfaces;

import bookstore.Interfaces.Book;

/**
 *
 * @author Ricardo
 */
public interface Bank {
    boolean transfer(String recv, String send, int value);
}
