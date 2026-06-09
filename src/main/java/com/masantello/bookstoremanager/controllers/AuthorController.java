package com.masantello.bookstoremanager.controllers;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Api("Authors Management")
public interface AuthorController {

    @ApiOperation(value = "Author creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success author creation"),
            @ApiResponse(code = 400, message = "Missing required fields, wrong field range value or author already registered")
    })
    ResponseEntity<AuthorDto> create(@RequestBody AuthorDto authorDto);

    @ApiOperation(value = "List all authors operation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return all registered authors")
    })
    ResponseEntity<List<AuthorDto>> findAll();

    @ApiOperation(value = "Find Author by Name operation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success author found"),
            @ApiResponse(code = 404, message = "Author not found error code")
    })
    ResponseEntity<AuthorDto> findByName(@PathVariable String authorName);

    @ApiOperation(value = "Update author's data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success author update"),
            @ApiResponse(code = 404, message = "Author not found error code")
    })
    ResponseEntity<AuthorDto> update(@PathVariable Long authorId, @RequestBody AuthorDto authorDto);

    @ApiOperation(value = "Delete author operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success author delete"),
            @ApiResponse(code = 404, message = "Author not found error code")
    })
    ResponseEntity<Void> delete(@PathVariable Long authorId );
}
