/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.Impl;

import bank.Interfaces.Account;
import bookstore.Interfaces.Book;
import bookstore.Interfaces.Cart;
import bookstore.Interfaces.Store;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ricardo
 */
public class AccountImp implements Account {
    private String id;
    private int value;

    public AccountImp(String id, int value){
        this.id = id;
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void addValue(int value){
        this.value += value;
    }

    @Override
    public void rmValue(int value){
        this.value -= value;
    }

  }
