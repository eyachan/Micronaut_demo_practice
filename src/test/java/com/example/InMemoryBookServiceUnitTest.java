package com.example;


import com.example.book.model.Book;
import com.example.book.repository.InMemoryRepository;
import com.example.book.service.BookService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;


public class InMemoryBookServiceUnitTest {

    private final BookService bookService ;

    public InMemoryBookServiceUnitTest() {
        final var bookrepository = new InMemoryRepository() ;
        this.bookService = new BookService(bookrepository);
    }

    @BeforeEach
    public void init() {
        bookService.deleteAll();
    }

//    Book save(Book book);
    @Test
    public void save_valid_book_as_input_expect_same_book(){
        final var book = new Book("12", "micronaut");
        final var savedBook = bookService.addBook(book).get();
        Assertions.assertAll(
                () -> Assertions.assertEquals(book, savedBook),
                () -> Assertions.assertEquals(1, bookService.findAll().size())
        );
    }
    @Test
    public void save_invalid_book_as_input_expect_booknotfound_exception(){
        final Book book = null;
        final var savedBook = bookService.addBook(book).getLeft();
        MatcherAssert.assertThat(savedBook, IsInstanceOf.instanceOf(BookService.BookException.BookNullException.class));
        //Assertions.assertEquals("book null exception ", savedBook.getMessage());

    }
    @Test
    public void save_invalid_book_ID_as_input_expect_Bookid_not_valid_exception(){
        final Book book = new Book("","micronaut");
        final var savedBook = bookService.addBook(book).getLeft();
        MatcherAssert.assertThat(savedBook, IsInstanceOf.instanceOf(BookService.BookException.BookIdNotValidException.class));

    }
    @Test
    public void save_invalid_book_name_as_input_expect_Bookid_not_valid_exception(){
        final var book = new Book("12", null);
        final var savedBook = bookService.addBook(book).getLeft();
        MatcherAssert.assertThat(savedBook, IsInstanceOf.instanceOf(BookService.BookException.BookTitleNotValidException.class));
    }
    @Test
    public void save_valid_book_input_with_same_book_expect_book_found_exception(){
        final var book = new Book("12", "micronaut");
        final var savedBook = bookService.addBook(book).get();
        final var book2 = new Book("12", "micronaut");
        final var savedSameBook = bookService.addBook(book2).getLeft();
        MatcherAssert.assertThat(savedSameBook, IsInstanceOf.instanceOf(BookService.BookException.BookAlreadyExistException.class));
    }
//    boolean bookExist(String bookId) ;
//    Collection<Book> saveAll (Collection<Book> books) ;
@Test
public void save_collection_valid_books_input_and_expect_list_of_books() {
    final var books = List.of(
            new Book("1", "micronaur"),
            new Book("2", "springBoot"),
            new Book("3", "Spring")
    );
    final var savedBooks = bookService.saveAll(books).getSavedBooks();
    Assertions.assertAll(
            () -> Assertions.assertEquals(books, savedBooks),
            () -> Assertions.assertEquals(3, bookService.findAll().size())
    );

}
    @Test
    public void save_collection_valid_books_and_invalid_book_input_and_expect_list_of_books() {
        final var books = List.of(
                new Book("", "micronaur"),
                new Book("2", ""),
                new Book("3", "Spring"),
                new Book(null,null)
        );
        final var savedBooks = bookService.saveAll(books);
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, savedBooks.getSavedBooks().size()),
                ()-> Assertions.assertEquals(3,savedBooks.getUnsavedBooks().size() ),
                ()-> MatcherAssert.assertThat(savedBooks.getUnsavedBooks().stream().findFirst().get().getReason(), IsInstanceOf.instanceOf(BookService.BookException.BookIdNotValidException.class)),
                ()-> Assertions.assertEquals(new Book("", "micronaur"),savedBooks.getUnsavedBooks().stream().findFirst().get().getBook())

        );

    }
//    Book update(Book book);
    //error can't update book
    @Test
    public void update_book_with_valid_id_expect_book_updated(){
        final var book = new Book("12", "micronaut");
        final var savedBook = bookService.addBook(book);
        final var bookToBeUpdated = new Book("12", "springboot");
        final var updateBook = bookService.updateBook(bookToBeUpdated);
        Assertions.assertAll(
                () -> Assertions.assertEquals(bookToBeUpdated, updateBook.get()),
                () -> Assertions.assertNotEquals(savedBook.get(), updateBook.get()),
                () -> Assertions.assertEquals(1, bookService.findAll().size())
        );
    }
    @Test
    public void update_book_with_invalid_id_expect_book_id_exception(){
        final var book = new Book("12", "micronaut");
        final var savedBook = bookService.addBook(book);
        final var bookToBeUpdated = new Book("", "springboot");
        final var updateBook = bookService.updateBook(bookToBeUpdated);
        MatcherAssert.assertThat(updateBook.getLeft(), IsInstanceOf.instanceOf(BookService.BookException.BookIdNotValidException.class));
    }
    @Test
    public void update_book_with_invalid_name_expect_book_name_exception(){
        final var book = new Book("12", "micronaut");
        final var savedBook = bookService.addBook(book);
        final var bookToBeUpdated = new Book("12", null);
        final var updateBook = bookService.updateBook(bookToBeUpdated);
        MatcherAssert.assertThat(updateBook.getLeft(), IsInstanceOf.instanceOf(BookService.BookException.BookTitleNotValidException.class));
    }
    //error
    @Test
    public void update_book_with_non_exist_name_expect_book_dont_exist__exception(){
        final var book = new Book("12", "micronaut");
        final var savedBook = bookService.addBook(book);
        final var bookToBeUpdated = new Book("13", "springboot");
        final var updateBook = bookService.updateBook(bookToBeUpdated);
        MatcherAssert.assertThat(updateBook.getLeft(), IsInstanceOf.instanceOf(BookService.BookException.BookNotFoundException.class));
    }
    @Test
    public void update_book_with_null_book_expect_book_dont_exist__exception(){
        final var book = new Book("12", "micronaut");
        final var savedBook = bookService.addBook(book);
        final Book bookToBeUpdated = null;
        final var updateBook = bookService.updateBook(bookToBeUpdated);
        MatcherAssert.assertThat(updateBook.getLeft(), IsInstanceOf.instanceOf(BookService.BookException.BookNullException.class));
    }

//    void deleteById(String id) ;
    @Test
    public void delete_book_by_id_expect_deleted_book(){
        final var book = new Book("12", "micronaut");
        final var savedBook = bookService.addBook(book);
        bookService.deleteById(savedBook.get().getIdBook());
        Assertions.assertAll(
                () -> Assertions.assertFalse(bookService.bookExit(book.getIdBook())),
                () -> Assertions.assertTrue(bookService.findAll().isEmpty())
        );
    }
    @Test
    public void delete_book_by_invalid_id_expect_book_id_invalid(){
        final var book = new Book("12", "micronaut");
        final var savedBook = bookService.addBook(book);
        final var bookDeleted = bookService.deleteById("");
        MatcherAssert.assertThat(bookDeleted.get(), IsInstanceOf.instanceOf(BookService.BookException.BookIdNotValidException.class));

    }
//    void deleteAll() ;
@Test
public void delete_all_book_collection_expect_empty_list() {
    final var books = List.of(
            new Book("1", "micronaur"),
            new Book("2", "springBoot"),
            new Book("3", "Spring")
    );
    final var savedBooks = bookService.saveAll(books);
    bookService.deleteAll();
    Assertions.assertTrue(bookService.findAll().isEmpty());
}

//    Optional<Book> findById(String id);
@Test
public void find_book_by_id_expect_book(){
    final var book = new Book("12", "micronaut");
    final var savedBook = bookService.addBook(book);
    final var bookFound = bookService.findById(savedBook.get().getIdBook());
    Assertions.assertEquals(book, bookFound.get());
}
    @Test
    public void find_book_by_invalid_id_expect_book_id_exception(){
        final var book = new Book("12", "micronaut");
        final var savedBook = bookService.addBook(book);
        final var bookFound = bookService.findById("");
        MatcherAssert.assertThat(bookFound.getLeft(), IsInstanceOf.instanceOf(BookService.BookException.BookIdNotValidException.class));
    }
    @Test
    public void find_book_by_valid_id_but_non_exist_book_expect_book_non_exist(){
        final var book = new Book("12", "micronaut");
        final var savedBook = bookService.addBook(book);
        final var bookFound = bookService.findById("13");
        MatcherAssert.assertThat(bookFound.getLeft(), IsInstanceOf.instanceOf(BookService.BookException.BookNotFoundException.class));
    }
//    Collection<Book> finaAll();
@Test
public void find_all_book_expected_collection_book(){
    final var books = List.of(
            new Book("1", "micronaur"),
            new Book("2", "springBoot"),
            new Book("3", "Spring")
    );
    final var savedBooks = bookService.saveAll(books);
    final var allBooks = bookService.findAll();
    Assertions.assertEquals(books.size(), allBooks.size());

}




}
