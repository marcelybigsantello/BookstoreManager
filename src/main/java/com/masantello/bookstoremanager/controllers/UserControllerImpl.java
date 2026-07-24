package com.masantello.bookstoremanager.controllers;

import com.masantello.bookstoremanager.dtos.JwtRequest;
import com.masantello.bookstoremanager.dtos.JwtResponse;
import com.masantello.bookstoremanager.dtos.UserDto;
import com.masantello.bookstoremanager.services.AuthenticationService;
import com.masantello.bookstoremanager.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserControllerImpl implements UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserControllerImpl(UserService userService,
                              AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @Override
    @PostMapping
    public ResponseEntity<UserDto> create(UserDto userDto) {
        var userCreated = userService.create(userDto);

        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userCreated.getId())
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
    public ResponseEntity<Void> update(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
        userDto.setId(userId);
        userService.update(userDto);

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> delete(Long userId) {
        userService.delete(userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/authenticate")
    public JwtResponse createAuthenticationToken(@RequestBody @Valid JwtRequest jwtRequest) {
        return authenticationService.createAuthenticationToken(jwtRequest);
    }
}
