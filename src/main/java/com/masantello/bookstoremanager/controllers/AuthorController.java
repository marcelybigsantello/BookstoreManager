package com.masantello.bookstoremanager.controllers;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Api("Authors Management")
public interface AuthorController {

    @ApiOperation(value = "Author creation operation")
    ResponseEntity<AuthorDto> create(@RequestBody AuthorDto authorDto);

    @ApiOperation(value = "List all authors operation")
    ResponseEntity<List<AuthorDto>> findAll();

    @ApiOperation(value = "Find Author by Name operation")
    ResponseEntity<AuthorDto> findByName(@PathVariable String authorName);

    @ApiOperation(value = "Update author's data")
    ResponseEntity<AuthorDto> update(@PathVariable String authorName, @RequestBody AuthorDto authorDto);

    @ApiOperation(value = "Delete author operation")
    ResponseEntity<Void> delete(@PathVariable String authorName);
}
