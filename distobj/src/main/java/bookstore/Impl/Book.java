package bookstore.Impl;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class Book implements CatalystSerializable{
    private int isbn;
    private String title, author;
    private int price;

    public Book(){ }

    public Book(int isbn, String title, String author, int price) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
    }

    public int getIsbn() { return isbn; }

    public String getTitle() { return title; }

    public String getAuthor() { return author; }

    public int getPrice() { return price; }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(isbn);
        bufferOutput.writeInt(price);
        bufferOutput.writeString(title);
        bufferOutput.writeString(author);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        isbn = bufferInput.readInt();
        price = bufferInput.readInt();
        title = bufferInput.readString();
        author = bufferInput.readString();
    }
}
