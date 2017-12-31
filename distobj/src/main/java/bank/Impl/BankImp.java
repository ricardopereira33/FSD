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
import java.util.concurrent.locks.ReentrantLock;

public class BankImp implements Bank {
    public Map<String, AccountImp> accounts;
    public Map<String, List<Transfer>> history;
    private ReentrantLock lock;

    public BankImp() {
        history = new HashMap<>();
        accounts = new HashMap<>();
        accounts.put("store", new AccountImp( "store", 0));
        accounts.put("client", new AccountImp("client", 0));
        this.lock = new ReentrantLock();
    }

    @Override
    public boolean transfer(String recv, String send, int value){
        AccountImp aRecv = accounts.get(recv);
        AccountImp aSend = accounts.get(send);

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

    public void lock() {
        this.lock.lock();
    }

    public void unlock(){
        this.lock.unlock();
    }
}
