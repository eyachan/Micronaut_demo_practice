package com.example.book.controller;


import com.example.book.model.Book;
import com.example.book.service.BookService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.stream.Collectors;

@Controller("/book")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }


    @Get("/{id}")
    public HttpResponse<BookSwap> show(String id) {

        return HttpResponse
                .ok(
                        new BookSwap(
                                bookService
                                        .findById(id)
                                        .get()
                        )
                );
    }

    @Put
    public HttpResponse<BookSwap> update(@Body BookSwap book) {
        final var bookUpdate = bookService.updateBook(book.toBook());

        return HttpResponse
                .ok(
                        new BookSwap(bookUpdate.get())
                );
    }

    @Get
    public HttpResponse<Collection<BookSwap>> showAll() {

        return HttpResponse
                .ok(
                        bookService
                                .findAll()
                                .stream()
                                .map(BookSwap::new)
                                .collect(Collectors.toUnmodifiableList())
                );
    }

    @Post
    public HttpResponse<BookSwap> save(@Body BookSwap book) {
        Book bookAdded = bookService.addBook(book.toBook()).get();
        return HttpResponse
                .ok(new BookSwap(bookAdded));
    }

    @Post("/list")
    public HttpResponse<Collection<BookSwap>> saveAll(@Body Collection<BookSwap> books) {

        final var savedBooks = bookService
                .saveAll(
                        books
                                .stream()
                                .map(BookSwap::toBook)
                                .collect(Collectors.toUnmodifiableList())
                )
                .getSavedBooks();
        return HttpResponse
                .ok(
                        savedBooks
                                .stream()
                                .map(BookSwap::new)
                                .collect(Collectors.toUnmodifiableList())
                );
    }

    @Delete("/{id}")
    public HttpResponse<String> delete(String id) {
        bookService.deleteById(id);
        return HttpResponse.ok("element deleted");
    }

    @Delete("/delete")
    public HttpResponse<String> deleteAll() {
        bookService.deleteAll();
        return HttpResponse.ok("list deleted");
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookSwap {
        private String idBook;
        private String bookTitle;

        public BookSwap(Book book) {
            this.idBook = book.getIdBook();
            this.bookTitle = book.getBookTitle();
        }

        public Book toBook() {
            return new Book(this.idBook, this.bookTitle);
        }
    }

}

