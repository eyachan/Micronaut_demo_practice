package com.example;


import com.example.book.dto.BookDtoRepository;
import com.example.book.model.Book;
import com.example.book.repository.BookRepository;
import com.example.book.repository.DatabaseRepository;
import com.example.book.repository.InMemoryRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

@MicronautTest
public class DatabaseRepositoryUnitTest {

    private final BookRepository bookRepository;

    public DatabaseRepositoryUnitTest(BookDtoRepository bookDtoRepository) {
        this.bookRepository = new DatabaseRepository(bookDtoRepository);
    }

    //initialize bookreporsitory before any test
    @BeforeEach
    public void init() {
        bookRepository.deleteAll();
    }


    @Test
    public void Save_Book_with_Valid_Book_input_expected_same_book() {
        final var book = new Book("12", "micronaut");
        final var savedBook = bookRepository.save(book);
        Assertions.assertAll(
                () -> Assertions.assertEquals(book, savedBook),
                () -> Assertions.assertEquals(1, bookRepository.finaAll().size()),
                () -> Assertions.assertTrue(bookRepository.bookExist(book.getIdBook()))
        );
    }


    @Test
    public void save_book_with_valid_book_input_and_see_if_exit() {
        final var book = new Book("12", "micronaut");
        final var savedBook = bookRepository.save(book);
        Assertions.assertTrue(bookRepository.bookExist(book.getIdBook()));

    }

    @Test
    public void save_collection_valid_books_input_and_expect_list_of_books() {
        final var books = List.of(
                new Book("1", "micronaur"),
                new Book("2", "springBoot"),
                new Book("3", "Spring")
        );
        final var savedBooks = bookRepository.saveAll(books);
        Assertions.assertAll(
                () -> Assertions.assertEquals(books, savedBooks),
                () -> Assertions.assertEquals(3, bookRepository.finaAll().size()),
                () -> Assertions.assertTrue(books.stream().map(Book::getIdBook).allMatch(bookRepository::bookExist))
        );

    }

    @Test
    public void update_book_with_valid_book_input_expected_updated_book() {
        final var book = new Book("12", "micronaut");
        final var savedBook = bookRepository.save(book);
        final var bookToBeUpdated = new Book("12", "springboot");
        final var updateBook = bookRepository.update(bookToBeUpdated);
        Assertions.assertAll(
                () -> Assertions.assertEquals(bookToBeUpdated, updateBook),
                () -> Assertions.assertNotEquals(savedBook, updateBook),
                () -> Assertions.assertEquals(1, bookRepository.finaAll().size()),
                () -> Assertions.assertTrue(bookRepository.bookExist(bookToBeUpdated.getIdBook()))
        );
    }

    @Test
    public void Deleted_book_by_id_expected_book_not_exist() {
        final var book = new Book("12", "micronaut");
        final var savedBook = bookRepository.save(book);
        bookRepository.deleteById(savedBook.getIdBook());
        Assertions.assertAll(
                () -> Assertions.assertFalse(bookRepository.bookExist(book.getIdBook())),
                () -> Assertions.assertTrue(bookRepository.finaAll().isEmpty())
        );
    }

    @Test
    public void delete_all_book_collection_expect_empty_list() {
        final var books = List.of(
                new Book("1", "micronaur"),
                new Book("2", "springBoot"),
                new Book("3", "Spring")
        );
        final var savedBooks = bookRepository.saveAll(books);
        bookRepository.deleteAll();
        Assertions.assertTrue(bookRepository.finaAll().isEmpty());
    }

    @Test
    public void find_book_by_id_expect_book() {
        final var book = new Book("12", "micronaut");
        final var savedBook = bookRepository.save(book);
        final var bookFound = bookRepository.findById(savedBook.getIdBook());
        Assertions.assertAll(
                () -> Assertions.assertTrue(bookFound.isPresent()),
                () -> Assertions.assertEquals(book, bookFound.get())
        );
    }

    @Test
    public void find_all_book_expected_collection_book() {
        final var books = List.of(
                new Book("1", "micronaur"),
                new Book("2", "springBoot"),
                new Book("3", "Spring")
        );
        final var savedBooks = bookRepository.saveAll(books);
        final var allBooks = bookRepository.finaAll();
        Assertions.assertEquals(books.size(), allBooks.size());

    }

}
