package bank.Impl;

import bank.Interfaces.Account;
import bank.Interfaces.Bank;
import bookstore.Impl.BookImp;
import bookstore.Interfaces.Book;
import bookstore.Interfaces.Cart;
import bookstore.Interfaces.Store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankImp implements Bank {
    
    public Map<Integer, AccountImp> accounts;
    public Map<String, List<Transfer>> history;

    public BankImp() {
        history = new HashMap<>();
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

        addToHistory(recv, send, value);

        return true;
    }

    private void addToHistory(String recv, String send, int value) {
        Transfer trans = new Transfer(send, value);
        if(history.containsKey(recv)){
            List<Transfer> list = history.get(recv);
            list.add(trans);
        }
        else{
            List<Transfer> list = new ArrayList<>();
            list.add(trans);
            history.put(recv, list);
        }
    }
}
