package com.masantello.bookstoremanager.controllers;

import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserControllerImpl implements UserController {

    private final UserService userService;

    public UserControllerImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    @PostMapping
    public ResponseEntity<UserDto> create(UserDto userDto) {
        UserDto userCreated = userService.create(userDto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userCreated.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @Override
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();

        return ResponseEntity.ok().body(users);
    }

    @Override
    @GetMapping(value = "/{username}")
    public ResponseEntity<List<UserDto>> findByUserName(String username) {
        List<UserDto> usersByName = userService.findByUsername(username);

        return ResponseEntity.ok().body(usersByName);
    }

    @Override
    @PutMapping(value = "/{userId}")
    public ResponseEntity<Void> update(@Valid @RequestBody UserDto userDto) {
        userService.update(userDto);

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> delete(Long userId) {
        userService.delete(userId);

        return ResponseEntity.noContent().build();
    }
}
