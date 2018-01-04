package bank.Data;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class Invoice implements CatalystSerializable{
    private int value;
    private String reference;

    public Invoice(){}

    public Invoice(int value, String reference){
        this.value = value;
        this.reference = reference;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(value);
        bufferOutput.writeString(reference);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        value = bufferInput.readInt();
        reference = bufferInput.readString();
    }
}
