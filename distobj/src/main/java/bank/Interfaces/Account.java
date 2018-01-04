/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.Interfaces;

import java.util.List;

/**
 *
 * @author Ricardo
 */
public interface Account {
    void transfer(Account ac, int value);
    void credit(int value);
    void debit(int value);
    List<String> getHistory();
    String getId();
}
