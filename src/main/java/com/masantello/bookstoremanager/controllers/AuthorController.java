package com.masantello.bookstoremanager.controllers;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.services.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/authors")
public class AuthorController implements AuthorControllerDocs {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) { this.authorService = authorService; }

    @PostMapping
    public ResponseEntity<AuthorDto> create(@RequestBody AuthorDto authorDto) {
        var author = authorService.create(authorDto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(author.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping
    public ResponseEntity<List<AuthorDto>> findAll() {
        var authorDto = authorService.findAll();

        return ResponseEntity.ok().body(authorDto);
    }

    @GetMapping(value = "/{authorName}")
    public ResponseEntity<AuthorDto> findByName(@PathVariable String authorName) {
        var authorDto = authorService.findByName(authorName);

        return ResponseEntity.ok().body(authorDto);
    }

    @PutMapping(value = "/{authorName}")
    public ResponseEntity<AuthorDto> update(@PathVariable String authorName, @RequestBody AuthorDto authorDto) {
        authorService.updateByName(authorDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{authorName}")
    public ResponseEntity<Void> delete(@PathVariable String authorName) {
        authorService.delete(authorName);

        return ResponseEntity.noContent().build();
    }

}
