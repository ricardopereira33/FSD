package bank.Impl;

import bank.Interfaces.Account;
import bank.Interfaces.Bank;
import bookstore.Impl.BookImp;
import bookstore.Interfaces.Book;
import bookstore.Interfaces.Cart;
import bookstore.Interfaces.Store;

import java.util.HashMap;
import java.util.Map;

public class BankImp implements Bank {
    
    public Map<Integer, AccountImp> accounts;

    public BankImp() {
        accounts = new HashMap<>();
        accounts.put(1, new AccountImp( "store", 0));
        accounts.put(2, new AccountImp("client", 0));
    }

    @Override
    public boolean transfer(String recv, String send, int value){
        Account aRecv = accounts.get(recv);
        Account aSend = accounts.get(send);

        aRecv.addValue(value);
        aSend.rmValue(value);

        return true;
    }
}
