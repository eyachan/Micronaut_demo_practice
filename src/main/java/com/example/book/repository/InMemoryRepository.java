package com.example.book.repository;

import com.example.book.model.Book;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InMemoryRepository implements BookRepository {

    private Collection<Book> books;

    public InMemoryRepository() {
        this.books = new ArrayList<Book>();
    }

    @Override
    public Book save(Book book) {
        this.books.add(book);
        return book;
    }

    @Override
    public boolean bookExist(String bookId) {
        return
                this.books
                        .stream()
                        .anyMatch(it -> it.getIdBook().equals(bookId));
    }

    @Override
    public Collection<Book> saveAll(Collection<Book> books) {
        this.books.addAll(books);
        return books;
    }

    @Override
    public Book update(Book book) {
        deleteById(book.getIdBook());
        this.books.add(book) ;
        return book ;
    }

    @Override
    public void deleteById(String id) {
        this.books =
                this.books
                        .stream()
                        .filter(Predicate.not(it -> it.getIdBook().equals(id)))
                        .collect(Collectors.toList());
    }

    @Override
    public void deleteAll() {
        this.books = new ArrayList<Book>() ;
    }

    @Override
    public Optional<Book> findById(String id) {
        return this.books
                .stream()
                .filter(it -> it.getIdBook().equals(id))
                .findFirst() ;
    }

    @Override
    public Collection<Book> finaAll() {
        return this.books ;
    }
}
