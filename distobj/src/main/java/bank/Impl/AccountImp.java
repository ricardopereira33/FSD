/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.Impl;

import bank.Interfaces.Account;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ricardo
 */
public class AccountImp implements Account {
    private String id;
    private List<String> history;
    private int value;

    public AccountImp(String id, int value){
        this.id = id;
        this.value = value;
        this.history = new ArrayList<>();
    }


    @Override
    public void transfer(Account ac, int value) {
        debit(value);
        ac.credit(value);
        String id = ac.getId();
        addToHistory(id, value);
    }

    @Override
    public void credit(int value) {
        this.value += value;
    }

    @Override
    public void debit(int value) {
        this.value = value;
    }

    @Override
    public List<String> getHistory(){
        return history;
    }

    @Override
    public String getId() {
        return id;
    }

    private void addToHistory(String id, int value) {
        history.add("ID: "+ id +"\tValue: " + value);
    }

  }
