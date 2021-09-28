package com.example.book.configuration;

import com.example.book.dto.BookDtoRepository;
import com.example.book.repository.BookRepository;
import com.example.book.repository.DatabaseRepository;
import com.example.book.repository.InMemoryRepository;
import com.example.book.service.BookService;
import io.micronaut.context.annotation.Factory;

import javax.inject.Inject;
import javax.inject.Singleton;

//definition of bean
@Factory
public class BeanFactory {

  /*  @Singleton
    public BookRepository bookRepository(){
        return new InMemoryRepository() ;
    }*/
    @Singleton
    public BookService bookService(BookRepository bookRepository){
        return new BookService(bookRepository) ;
    }
    @Singleton
    public BookRepository bookRepository(BookDtoRepository bookDtoRepository){
        return new DatabaseRepository(bookDtoRepository);
    }

 }
