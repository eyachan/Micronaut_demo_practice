package com.example.book.service;


import com.example.book.model.Book;
import com.example.book.repository.BookRepository;
import io.vavr.control.Either;
import lombok.Data;


import java.util.*;
import java.util.stream.Collectors;

public class BookService {


    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public final Either<? extends BookException, Book> addBook(final Book book) {
        final var bookValidationStatus = validateBookToBeAdded(book);
        if (!bookValidationStatus.isValid()) {
            return Either.left(bookValidationStatus.getReason().orElse(new BookException("reason unknown")));
        }
        return Either.right(bookRepository.save(book));

    }


    private ValidationBookRecord validateBookToBeAdded(final Book book) {

        if (Objects.isNull(book)) {
            return ValidationBookRecord.bookInvalid(book, new BookException.BookNullException("book null exception "));
        }

        final var bookIdStatus = validateBookId(book);
        if (bookIdStatus.isPresent()) {

            return ValidationBookRecord.bookInvalid(book, bookIdStatus.get());
        }
        final var bookNameStatus = validateBookName(book);
        if (bookNameStatus.isPresent()) {
            return ValidationBookRecord.bookInvalid(book, bookNameStatus.get());
        }
        return
                bookExit(book.getIdBook())
                        ? ValidationBookRecord.bookInvalid(book, new BookException.BookAlreadyExistException("book already exsit"))
                        : ValidationBookRecord.bookValid(book);
    }

    public boolean bookExit(String idBook) {
        return bookRepository.bookExist(idBook);
    }

    private Optional<? extends BookException> validateBookName(Book book) {
        return Objects.isNull(book.getBookTitle()) || book.getBookTitle().isEmpty()
                ? Optional.ofNullable(new BookException.BookTitleNotValidException("book title is not valid !"))
                : Optional.empty();
    }

    private Optional<? extends BookException> validateBookId(Book book) {
        return Objects.isNull(book.getIdBook()) || book.getIdBook().isEmpty()
                ? Optional.ofNullable(new BookException.BookIdNotValidException("book id is not valid !"))
                : Optional.empty();
    }

    public SaveBooksRecord saveAll(Collection<Book> books) {

        //this stream should never be parallel because IO
        final var checkBook = books.stream()
                .map(this::validateBookToBeAdded)
                .collect(Collectors.partitioningBy(ValidationBookRecord::isValid));

        final var validBooks =
                checkBook
                        .get(true)
                        .stream()
                        .map(ValidationBookRecord::getBook)
                        .collect(Collectors.toUnmodifiableList());

        final var invalidBooks =
                checkBook
                        .get(false)
                        .stream()
                        .map(this::validationBookRecord2UnvalidatedBookRecord)
                        .collect(Collectors.toUnmodifiableList());

        final var saveBooks = bookRepository.saveAll(validBooks);

        return
                new SaveBooksRecord(saveBooks, invalidBooks);

    }

    private SaveBooksRecord.UnsavedBookRecord validationBookRecord2UnvalidatedBookRecord(ValidationBookRecord validationBookRecord) {
        return
                new SaveBooksRecord.UnsavedBookRecord(
                        validationBookRecord.getBook(),
                        validationBookRecord
                                .getReason()
                                .orElse(new BookException("unknown reason"))
                );

    }


    public Either<? extends BookException, Book> updateBook(Book book) {
        final var bookValidationStatus = validateBookToBeUpdated(book);
        if (!bookValidationStatus.isValid()) {
            return Either.left(bookValidationStatus.getReason().orElse(new BookException("reason unknown")));
        }
        return Either.right(bookRepository.update(book));

    }

    private ValidationBookRecord validateBookToBeUpdated(final Book book) {

        if (Objects.isNull(book)) {
            return ValidationBookRecord.bookInvalid(book, new BookException.BookNullException("book null exception "));
        }

        final var bookIdStatus = validateBookId(book);
        if (bookIdStatus.isPresent()) {

            return ValidationBookRecord.bookInvalid(book, bookIdStatus.get());
        }
        final var bookNameStatus = validateBookName(book);
        if (bookNameStatus.isPresent()) {
            return ValidationBookRecord.bookInvalid(book, bookNameStatus.get());
        }
        return
                bookExit(book.getIdBook())
                        ? ValidationBookRecord.bookValid(book)
                        : ValidationBookRecord.bookInvalid(book, new BookException.BookNotFoundException("book doesn't exist"));

    }

    public Optional<? extends BookException> deleteById(String id) {
        final var bookValidationStatus = validateExistentBookByID(id);
        if (bookValidationStatus.isPresent()) {
            return bookValidationStatus;
        }
        bookRepository.deleteById(id);
        return Optional.empty();
    }

    private Optional<? extends BookException> validateExistentBookByID(final String id) {
        final var bookIdStatus = validateBookId(id);
        if (bookIdStatus.isPresent()) {

            return bookIdStatus;
        }
        return
                bookExit(id)
                        ? Optional.empty()
                        : Optional.ofNullable(new BookException.BookNotFoundException("book doesn't exist"));

    }

    private Optional<? extends BookException> validateBookId(String id) {
        return Objects.isNull(id) || id.isEmpty()
                ? Optional.ofNullable(new BookException.BookIdNotValidException("book id is not valid !"))
                : Optional.empty();
    }

    public void deleteAll() {
        bookRepository.deleteAll();
    }

    public Either<? extends BookException, Book> findById(String id) {
        final var bookValidationStatus = validateExistentBookByID(id);
        if (bookValidationStatus.isPresent()) {
            return Either.left(bookValidationStatus.get());
        }
        return
                bookRepository
                        .findById(id)
                        .<Either<? extends BookException, Book>>map(Either::right)
                        .orElse(Either.left(new BookException.BookNotFoundException("book not found ")));
    }

    public Collection<Book> findAll() {
        return bookRepository.finaAll();
    }


    @Data
    public static final class SaveBooksRecord {
        private final Collection<Book> savedBooks;
        private final Collection<UnsavedBookRecord> unsavedBooks;

        @Data
        public static final class UnsavedBookRecord {
            private final Book book;
            private final BookException reason;
        }
    }

    public static final class ValidationBookRecord {
        private final Book book;
        private final BookException reason;

        private ValidationBookRecord(Book book, BookException reason) {
            this.book = book;
            this.reason = reason;
        }

        private ValidationBookRecord(Book book) {
            this.book = book;
            this.reason = null;
        }


        public Book getBook() {
            return book;
        }

        public Optional<BookException> getReason() {

            return Objects.nonNull(this.reason)
                    ? Optional.ofNullable(this.reason)
                    : Optional.empty();
        }

        public boolean isValid() {
            return Objects.isNull(this.reason);
        }

        public static ValidationBookRecord bookValid(Book book) {
            return new ValidationBookRecord(book);
        }

        public static ValidationBookRecord bookInvalid(Book book, BookException reason) {
            return new ValidationBookRecord(book, reason);
        }


    }

    public static class BookException extends RuntimeException {
        public BookException(String message) {
            super(message);
        }

        public static final class BookIdNotValidException extends BookException {

            public BookIdNotValidException(String message) {
                super(message);
            }
        }

        public static final class BookTitleNotValidException extends BookException {

            public BookTitleNotValidException(String message) {
                super(message);
            }
        }

        public static final class BookNullException extends BookException {

            public BookNullException(String message) {
                super(message);
            }
        }

        public static final class BookAlreadyExistException extends BookException {

            public BookAlreadyExistException(String message) {
                super(message);
            }
        }

        public static final class BookNotFoundException extends BookException {

            public BookNotFoundException(String message) {
                super(message);
            }
        }
    }


}
