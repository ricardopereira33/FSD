/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.Impl;

import DO.Obj;
import bank.Interfaces.Account;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ricardo
 */
public class AccountImp extends Obj implements Account, CatalystSerializable {
    private String id;
    private List<String> history;
    private int value;

    public AccountImp(String id, int value){
        super();
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

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeString(id);
        serializer.writeObject(history, bufferOutput);
        bufferOutput.writeInt(value);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        id = bufferInput.readString();
        history = serializer.readObject(bufferInput);
        value = bufferInput.readInt();
    }
}
