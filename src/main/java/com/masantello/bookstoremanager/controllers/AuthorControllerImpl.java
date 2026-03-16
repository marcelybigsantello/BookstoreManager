package com.masantello.bookstoremanager.controllers;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.services.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/authors")
public class AuthorControllerImpl implements AuthorController {

    private final AuthorService authorService;

    public AuthorControllerImpl(AuthorService authorService) { this.authorService = authorService; }

    @PostMapping
    public ResponseEntity<AuthorDto> create(@RequestBody @Valid AuthorDto authorDto) {
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

    @PutMapping(value = "/{authorId}")
    public ResponseEntity<AuthorDto> update(@PathVariable Long authorId, @RequestBody @Valid AuthorDto authorDto) {
        authorDto.setId(authorId);
        authorDto = authorService.updateById(authorDto);

        return ResponseEntity.ok().body(authorDto);
    }

    @DeleteMapping(value = "/{authorId}")
    public ResponseEntity<Void> delete(@PathVariable Long authorId) {
        authorService.delete(authorId);

        return ResponseEntity.noContent().build();
    }

}
