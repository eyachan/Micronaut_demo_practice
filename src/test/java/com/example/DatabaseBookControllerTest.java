package com.example;

import com.example.book.controller.BookController;
import com.example.book.dto.BookDtoRepository;
import com.example.book.model.Book;
import com.example.book.repository.DatabaseRepository;
import com.example.book.repository.InMemoryRepository;
import com.example.book.service.BookService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

@MicronautTest
public class DatabaseBookControllerTest {


    private final BookController bookController;

    public DatabaseBookControllerTest(BookDtoRepository bookDtoRepository) {
        this.bookController = new BookController(new BookService(new DatabaseRepository(bookDtoRepository)));
    }

    @BeforeEach
    public void init() {
        bookController.deleteAll();
    }

    @Test
    public void controller_save_book_expect_returned_book() {
        Book book = new Book("test", "micronaut");
        final var bookSave = bookController.save(new BookController.BookSwap(book));
        Assertions.assertEquals(book, bookSave.getBody().get().toBook());
        Assertions.assertEquals(HttpStatus.OK, bookSave.getStatus());
    }

  /*  @Test
    public void controller_save_book_expect_return_status_ok() {
        Book book = new Book("test", "micronaut");
        var request = HttpRequest.POST("/book", book);
        HttpResponse<Book> rsp = client.toBlocking().exchange(request);
        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());
    }*/

    @Test
    public void save_collection_valid_books_input_and_expect_list_of_books() {
        final var books = List.of(
                new Book("1", "micronaur"),
                new Book("2", "springBoot"),
                new Book("3", "Spring")
        );
        final var savedBooks = bookController.saveAll(
                books
                .stream()
                .map(BookController.BookSwap::new)
                .collect(Collectors.toUnmodifiableList())
        );
        Assertions.assertAll(
                () -> Assertions.assertEquals(books, savedBooks.getBody().get()
                        .stream().map(BookController.BookSwap::toBook)
                        .collect(Collectors.toUnmodifiableList())
                ),
                () -> Assertions.assertEquals(3, bookController.showAll().getBody().get().size())
        );

    }

  /*  @Test
    public void save_collection_valid_books_input_and_expect_response_ok() {
        final var books = List.of(
                new Book("1", "micronaut"),
                new Book("2", "springBoot"),
                new Book("3", "Spring")
        );
        HttpRequest<Object> request = HttpRequest.POST("/book/list", books);
        HttpResponse<Book> rsp = client.toBlocking().exchange(request);
        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());

    }*/

    @Test
    public void update_book_with_valid_id_expect_book_updated() {
        final var book = new Book("12", "micronaut");
        final var savedBook = bookController.save(new BookController.BookSwap(book));
        final var bookToBeUpdated = new Book("12", "springboot");
        final var updateBook = bookController.update(new BookController.BookSwap(bookToBeUpdated));
        Assertions.assertAll(
                () -> Assertions.assertEquals(bookToBeUpdated, updateBook.getBody().get().toBook()),
                () -> Assertions.assertNotEquals(savedBook.getBody().get(), updateBook.getBody().get()),
                () -> Assertions.assertEquals(1, bookController.showAll().getBody().get().size())
        );
    }



    @Test
    public void delete_book_by_id_expect_deleted_book() {
        final var book = new Book("12", "micronaut");
        final var savedBook = bookController.save(new BookController.BookSwap(book));
        final HttpResponse<String> delete = bookController.delete(savedBook.getBody().get().getIdBook());
        Assertions.assertAll(
                () -> Assertions.assertEquals("element deleted", delete.getBody().get())
        );
    }


    @Test
    public void delete_all_book_collection_expect_empty_list() {
        final var books = List.of(
                new Book("1", "micronaur"),
                new Book("2", "springBoot"),
                new Book("3", "Spring")
        );
        final var savedBooks = bookController.saveAll(
                books
                        .stream()
                        .map(BookController.BookSwap::new)
                .collect(Collectors.toUnmodifiableList())
        );
        final var stringHttpResponse = bookController.deleteAll();
        Assertions.assertEquals("list deleted", stringHttpResponse.getBody().get());
    }

 /*   @Test
    public void delete_all_book_collection_expect_status_ok() {
        final var books = List.of(
                new Book("1", "micronaur"),
                new Book("2", "springBoot"),
                new Book("3", "Spring")
        );
        HttpRequest<Object> request = HttpRequest.POST("/book/list", books);
        HttpResponse<Book> rsp = client.toBlocking().exchange(request);
        HttpRequest<Object> request2 = HttpRequest.DELETE("/book/delete");
        HttpResponse<Book> rspDeletedAll = client.toBlocking().exchange(request2);
        Assertions.assertEquals(HttpStatus.OK, rspDeletedAll.getStatus());
    }*/
 @Test
 public void find_all_book_collection_expect_all_book() {
     final var books = List.of(
             new Book("1", "micronaur"),
             new Book("2", "springBoot"),
             new Book("3", "Spring")
     );
     final var savedBooks = bookController.saveAll(
             books
                     .stream()
                     .map(BookController.BookSwap::new)
                     .collect(Collectors.toUnmodifiableList())
     );
     final var stringHttpResponse = bookController.showAll();
     Assertions.assertEquals(3,stringHttpResponse.getBody().get().size());
 }
    @Test
    public void find_book_by_id_expect_all_book() {
        final var book = new Book("12", "micronaut");
        final var savedBook = bookController.save(new BookController.BookSwap(book));
        final var stringHttpResponse = bookController.show(savedBook.getBody().get().getIdBook());
        Assertions.assertEquals(book, stringHttpResponse.getBody().get().toBook());
    }
}
