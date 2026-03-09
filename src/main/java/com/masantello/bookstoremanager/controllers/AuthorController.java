package com.masantello.bookstoremanager.controllers;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AuthorController {

    ResponseEntity<AuthorDto> create(@RequestBody AuthorDto authorDto);

    ResponseEntity<List<AuthorDto>> findAll();

    ResponseEntity<AuthorDto> findByName(@PathVariable String authorName);

    ResponseEntity<AuthorDto> update(@PathVariable String authorName, @RequestBody AuthorDto authorDto);

    ResponseEntity<Void> delete(@PathVariable String authorName);
}
