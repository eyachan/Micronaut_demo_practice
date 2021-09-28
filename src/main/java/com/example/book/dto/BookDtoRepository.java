package com.example.book.dto;


import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface BookDtoRepository  extends CrudRepository<BookDto,String> {


}
