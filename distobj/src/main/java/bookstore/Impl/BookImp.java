package bookstore.Impl;

import bookstore.Interfaces.Book;

public class BookImp implements Book {
    private int isbn;
    private String title, author;
    private int price; 
    

    public BookImp(int isbn, String title, String author, int price) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
    }

    @Override
    public int getIsbn() { return isbn; }

    @Override
    public String getTitle() { return title; }

    @Override
    public String getAuthor() { return author; }
    
    @Override 
    public int getPrice() { return price; } 

}
