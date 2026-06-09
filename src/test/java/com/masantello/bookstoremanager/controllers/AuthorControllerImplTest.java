package com.masantello.bookstoremanager.controllers;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.models.Author;
import com.masantello.bookstoremanager.models.enums.LiteraryGenre;
import com.masantello.bookstoremanager.services.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = AuthorControllerImpl.class)
public class AuthorControllerImplTest {

    @Autowired
    private AuthorControllerImpl authorController;

    @MockitoBean
    private AuthorService authorService;

    private AuthorDto authorDto;

    @BeforeEach
    void setup() {
        authorDto = new AuthorDto();
        authorDto.setId(1L);
        authorDto.setName("George Orwell");
        authorDto.setEmail("georgeorwell@gmail.com");
        authorDto.setAge(123);
        authorDto.setBirthDate(LocalDate.of(1903, 6, 25));
        authorDto.setLiteraryGenre("Utopia");
    }

    // =============================== CREATE TESTS =======================================
    @Test
    @DisplayName("Should create a new author successfully and return 201 CREATED")
    void testCreateAuthorSuccess(){
        //Arrange
        AuthorDto newAuthorDto = new AuthorDto();
        newAuthorDto.setId(1L);
        newAuthorDto.setName("George Orwell");
        newAuthorDto.setEmail("georgeorwell@gmail.com");
        newAuthorDto.setAge(123);
        newAuthorDto.setBirthDate(LocalDate.of(1903, 6, 25));
        newAuthorDto.setLiteraryGenre("Utopia");
        when(authorService.create(any(AuthorDto.class))).thenReturn(authorDto);

        //Act
        var response = authorController.create(newAuthorDto);

        //Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        verify(authorService, times(1)).create(any(AuthorDto.class));
    }

    @Test
    @DisplayName("Should invoke AuthorService.create exactly once during author creation")
    void testeCreateAuthorCallsServiceOnce() {
        //Arrange
        when(authorService.create(any(AuthorDto.class))).thenReturn(authorDto);

        //Act
        authorController.create(authorDto);

        //Assert
        verify(authorService, times(1)).create(any(AuthorDto.class));
    }

    // =============================== FIND ALL TESTS =======================================
    @Test
    @DisplayName("Should return all authors with 200 OK Status Code")
    void testFindAllAuthorsSuccess() {
        //Arrange
        List<AuthorDto> authorDtoList = Arrays.asList(authorDto,
                new AuthorDto(2L, "J.K. Rowling", "jkrowling@gmail.com",
                        60, LocalDate.of(1965, 7, 31), "Fantasy", null));
        when(authorService.findAll()).thenReturn(authorDtoList);

        //Act
        var response = authorController.findAll();

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(authorService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when there are no authors")
    void testFindAllAuthorsEmpty() {
        //Arrange
        when(authorService.findAll()).thenReturn(Collections.emptyList());

        //Act
        var response = authorController.findAll();

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(authorService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return correct author data in findAll response")
    void testFindAllAuthorsReturnsCorrectData() {
        //Arrange
        List<AuthorDto> authorDtoList = Collections.singletonList(authorDto);
        when(authorService.findAll()).thenReturn(authorDtoList);

        //Act
        var response = authorController.findAll();

        //Assert
        assert response.getBody() != null;
        var returnedAuthorDto = response.getBody().get(0);
        assertEquals(1L, returnedAuthorDto.getId());
        assertEquals("George Orwell", returnedAuthorDto.getName());
        assertEquals("georgeorwell@gmail.com", returnedAuthorDto.getEmail());
        assertThat(returnedAuthorDto.getBirthDate()).isEqualTo(LocalDate.of(1903, 6, 25));
        assertEquals(LiteraryGenre.convertToDescription(LiteraryGenre.UTOPIA), returnedAuthorDto.getLiteraryGenre());
    }

    // =============================== FIND BY NAME TESTS =======================================
    @Test
    @DisplayName("Should find an author by name and return 200 OK")
    void testFindAuthorByNameSuccess() {
        //Arrange
        String authorName = "George Orwell";
        when(authorService.findByName(authorName)).thenReturn(authorDto);

        //Act
        var response = authorController.findByName(authorName);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(authorName, response.getBody().getName());
        verify(authorService, times(1)).findByName(authorName);
    }

    @Test
    @DisplayName("Should pass part of Author's name to service and return correct author data")
    void testFindAuthorByNamePassesPartOfTheAuthorName() {
        //Arrange
        String authorName = "George";
        when(authorService.findByName(authorName)).thenReturn(authorDto);

        //Act
        var response = authorController.findByName(authorName);

        //Assert
        var returnedAuthor = response.getBody();
        assertEquals(1L, returnedAuthor.getId());
        assertEquals("georgeorwell@gmail.com", returnedAuthor.getEmail());
        assertEquals(123, returnedAuthor.getAge());
        assertEquals(LocalDate.of(1903, 6, 25), returnedAuthor.getBirthDate());
        assertEquals(LiteraryGenre.convertToDescription(LiteraryGenre.UTOPIA), returnedAuthor.getLiteraryGenre());
        verify(authorService, times(1)).findByName(authorName);
    }

    @Test
    @DisplayName("Should handle special characters in author's name")
    void testFindAuthorByNameWithSpecialCharacters() {
        //Arrange
        String authorName = "André Conceição";
        when(authorService.findByName(authorName)).thenReturn(authorDto);

        //Act
        var response = authorController.findByName(authorName);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authorService, times(1)).findByName(authorName);
    }

    // =============================== UPDATE TESTS =======================================
    @Test
    @DisplayName("Should update author's data and return 200 OK")
    void testUpdateAuthorSuccess() {
        //Arrange
        AuthorDto updatedAuthor = new AuthorDto();
        updatedAuthor.setName("Arthur Eric Blair");
        updatedAuthor.setEmail("arthurericblair@gmail.com");
        updatedAuthor.setBirthDate(LocalDate.of(1903, 6, 23));

        AuthorDto updatedAuthorDto = new AuthorDto(1L, "Arthur Eric Blair",
                "arthurericblaircompany@gmail.com",
                123, LocalDate.of(1903, 6, 23),
                LiteraryGenre.convertToDescription(LiteraryGenre.UTOPIA),
                null);

        when(authorService.updateById(any(AuthorDto.class))).thenReturn(updatedAuthorDto);

        //Act
        var response = authorController.update(1L, updatedAuthor);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Arthur Eric Blair", response.getBody().getName());
        verify(authorService, times(1)).updateById(any(AuthorDto.class));
    }

    @Test
    @DisplayName("Should set Author ID from path variable before updating")
    void testUpdateAuthorSetsIdFromPathVariable() {
        //Arrange
        Long authorId = 10L;
        AuthorDto updateAuthor = new AuthorDto();
        updateAuthor.setName("George Orwell (Eric Arthur Blair)");

        AuthorDto expectedDto = new AuthorDto();
        expectedDto.setId(authorId);
        expectedDto.setName("George Orwell (Eric Arthur Blair)");

        when(authorService.updateById(any(AuthorDto.class))).thenReturn(expectedDto);

        //Act
        var response = authorController.update(authorId, updateAuthor);

        //Assert
        verify(authorService, times(1)).updateById(argThat(dto -> dto.getId().equals(authorId)));
    }

    // =============================== DELETE TESTS =======================================
    @Test
    @DisplayName("Should delete author and return 204 NO_CONTENT")
    void testDeleteAuthorSuccess() {
        //Arrange
        Long authorId = 1L;
        doNothing().when(authorService).delete(authorId);

        //Act
        ResponseEntity<Void> response = authorController.delete(authorId);

        //Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(authorService, times(1)).delete(authorId);
    }

    @Test
    @DisplayName("Should delete different authors by ID")
    void testDeleteDifferentAuthors() {
        //Arrange
        doNothing().when(authorService).delete(anyLong());

        //Act
        authorController.delete(1L);
        authorController.delete(2L);
        authorController.delete(10L);

        //Assert
        verify(authorService).delete(1L);
        verify(authorService).delete(2L);
        verify(authorService).delete(10L);
        verify(authorService, times(3)).delete(anyLong());
    }




}
