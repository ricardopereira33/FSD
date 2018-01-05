package bank.Impl;

import DO.Obj;
import bank.Interfaces.Account;
import bank.Interfaces.Bank;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class BankImp extends Obj implements Bank, CatalystSerializable {
    public Map<String, Account> accounts;

    public BankImp() {
        super();
        accounts = new HashMap<>();
    }

    @Override
    public Account access(String id) {
        Account ac = null;
        if(!accounts.containsKey(id)) {
            ac = new AccountImp(id, 0);
            accounts.put(id, ac);
        }
        else ac = accounts.get(id);

        return ac;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        serializer.writeObject(accounts, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        accounts = serializer.readObject(bufferInput);
    }
}
