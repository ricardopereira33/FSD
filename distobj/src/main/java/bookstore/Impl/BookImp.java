package bookstore.Impl;

import bookstore.Interfaces.Book;

public class BookImp implements Book {
    private int isbn;
    private String title, author;

    public BookImp(int isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    @Override
    public int getIsbn() { return isbn; }

    @Override
    public String getTitle() { return title; }

    @Override
    public String getAuthor() { return author; }

}