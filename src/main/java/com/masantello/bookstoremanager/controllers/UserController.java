package com.masantello.bookstoremanager.controllers;

import com.masantello.bookstoremanager.dtos.PublisherDto;
import com.masantello.bookstoremanager.dtos.UserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Api("User Management")
public interface UserController {

    @ApiOperation(value = "User creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success user creation"),
            @ApiResponse(code = 400, message = "Missing required fields, wrong field range value or user already registered")
    })
    ResponseEntity<UserDto> create(@RequestBody UserDto userDto);

    @ApiOperation(value = "List all users operation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return all registered users")
    })
    ResponseEntity<List<UserDto>> findAll();

    @ApiOperation(value = "Find User by Name operation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success user found"),
            @ApiResponse(code = 404, message = "User not found error code")
    })
    ResponseEntity<List<UserDto>> findByUserName(@PathVariable String username);

    @ApiOperation(value = "Update user's data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success user update"),
            @ApiResponse(code = 404, message = "User not found error code")
    })
    ResponseEntity<Void> update(@RequestBody UserDto userDto);

    @ApiOperation(value = "Delete user operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success user delete"),
            @ApiResponse(code = 404, message = "User not found error code")
    })
    ResponseEntity<Void> delete(@PathVariable Long userId);
}
