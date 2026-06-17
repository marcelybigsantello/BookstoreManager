package com.masantello.bookstoremanager.controllers;

import com.masantello.bookstoremanager.dtos.PublisherDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Api("Publisher Management")
public interface PublisherController {

    @ApiOperation(value = "Publisher creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success publisher creation"),
            @ApiResponse(code = 400, message = "Missing required fields, wrong field range value or publisher already registered")
    })
    ResponseEntity<PublisherDto> create(@RequestBody PublisherDto publisherDto);

    @ApiOperation(value = "List all publishers operation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return all registered publishers")
    })
    ResponseEntity<List<PublisherDto>> findAll();

    @ApiOperation(value = "Find Publisher by Name operation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success publisher found"),
            @ApiResponse(code = 404, message = "Publisher not found error code")
    })
    ResponseEntity<List<PublisherDto>> findByName(@PathVariable String publisherName);

    @ApiOperation(value = "Delete publisher operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success publisher delete"),
            @ApiResponse(code = 404, message = "Publisher not found error code")
    })
    ResponseEntity<Void> delete(@PathVariable Long publisherId);
}
