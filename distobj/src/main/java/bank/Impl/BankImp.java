package bank.Impl;

import bank.Interfaces.Account;
import bank.Interfaces.Bank;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class BankImp implements Bank {
    public Map<String, Account> accounts;
    private ReentrantLock lock;

    public BankImp() {
        accounts = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    @Override
    public Account newAccount(String id) {
        Account ac = new AccountImp(id, 0);
        accounts.put(id, ac);
        return ac;
    }

    public void lock() {
        this.lock.lock();
    }

    public void unlock(){
        this.lock.unlock();
    }
}
