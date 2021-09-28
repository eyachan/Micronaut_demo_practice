package com.example.book.repository;


import com.example.book.model.Book;
import com.example.book.service.BookService;

import java.util.Collection;
import java.util.Optional;


public interface BookRepository {

    Book save(Book book);
    boolean bookExist(String bookId) ;
    Collection<Book> saveAll (Collection<Book> books) ;
    Book update(Book book);
    void deleteById(String id) ;
    void deleteAll() ;
    Optional<Book> findById(String id);
    Collection<Book> finaAll();
}
