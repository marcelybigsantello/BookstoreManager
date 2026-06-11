package com.masantello.bookstoremanager.controllers;

import com.masantello.bookstoremanager.dtos.PublisherDto;
import com.masantello.bookstoremanager.services.PublisherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/publishers")
public class PublisherControllerImpl implements PublisherController {

    private final PublisherService publisherService;

    public PublisherControllerImpl(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @PostMapping
    public ResponseEntity<PublisherDto> create(@RequestBody @Valid PublisherDto publisherDto) {
        var publisher = publisherService.create(publisherDto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(publisher.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping
    public ResponseEntity<List<PublisherDto>> findAll() {
        var publishers = publisherService.findAll();

        return ResponseEntity.ok().body(publishers);
    }

    @GetMapping(value = "/{publisherName}")
    public ResponseEntity<PublisherDto> findByName(String publisherName) {
        var publisherDto = publisherService.findByName(publisherName);

        return ResponseEntity.ok().body(publisherDto);
    }

    @DeleteMapping(value = "/{publisherId}")
    public ResponseEntity<Void> delete(Long publisherId) {
        publisherService.delete(publisherId);

        return ResponseEntity.noContent().build();
    }
}
