package com.example.book.dto;


import com.example.book.model.Book;
import io.micronaut.core.annotation.Introspected;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;



@Entity
@Introspected
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {

    @Id
    private String idBook;
    private String bookTitle;

   public static BookDto fromBook(Book book){
       return new BookDto(book.getIdBook() , book.getBookTitle()) ;
    }

    public Book book(){
       return new Book(this.idBook, this.bookTitle);
    }




}
