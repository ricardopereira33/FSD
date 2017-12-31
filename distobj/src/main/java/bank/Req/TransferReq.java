package bank.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import manager.Data.Context;

public class TransferReq implements CatalystSerializable {
    public String recv, send;
    public int value, bankid;
    public int txid;
    public Address address;
    
    public TransferReq() {}

    public TransferReq(String recv, String send, int value, int bankid, Context tx) {
        this.recv = recv;
        this.send = send;
        this.value = value;
        this.bankid = bankid;
        this.txid = tx.getTxid();
        this.address = tx.getAddress();
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeString(recv);
        bufferOutput.writeString(send);
        bufferOutput.writeInt(value);
        bufferOutput.writeInt(bankid);
        bufferOutput.writeInt(txid);
        serializer.writeObject(address, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        recv = bufferInput.readString();
        send = bufferInput.readString();
        value = bufferInput.readInt();
        bankid = bufferInput.readInt();
        txid = bufferInput.readInt();
        address = serializer.readObject(bufferInput);
    }
}
