package com.example.book.repository;


import com.example.book.dto.BookDto;
import com.example.book.dto.BookDtoRepository;
import com.example.book.model.Book;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DatabaseRepository implements BookRepository {

    private final BookDtoRepository bookDtoRepository;

    public DatabaseRepository(BookDtoRepository bookDtoRepository) {
        this.bookDtoRepository = bookDtoRepository;
    }

    @Override
    public Book save(Book book) {
        return bookDtoRepository.save( BookDto.fromBook(book)).book();
    }

    @Override
    public boolean bookExist(String bookId) {
        return bookDtoRepository.existsById(bookId);
    }

    @Override
    public Collection<Book> saveAll(Collection<Book> books) {
        final var bookDtos = bookDtoRepository.saveAll(
                books
                        .stream()
                        .map(BookDto::fromBook)
                        .collect(Collectors.toUnmodifiableList())
        );
        return StreamSupport
                .stream(bookDtos.spliterator(), false)
                .map(BookDto::book)
                .collect(Collectors.toUnmodifiableList());

    }

    @Override
    public Book update(Book book) {
        return bookDtoRepository
                .update( BookDto.fromBook(book)).book();
    }

    @Override
    public void deleteById(String id) {
        bookDtoRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        bookDtoRepository.deleteAll();
    }

    @Override
    public Optional<Book> findById(String id) {
        return bookDtoRepository
                .findById(id).map(BookDto::book);
    }

    @Override
    public Collection<Book> finaAll() {
        return StreamSupport
                .stream(bookDtoRepository.findAll().spliterator(), false)
                .map(BookDto::book)
                .collect(Collectors.toUnmodifiableList());
    }
}
