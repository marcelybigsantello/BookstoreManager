package com.masantello.bookstoremanager.services;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.exceptions.DataIntegrityViolationException;
import com.masantello.bookstoremanager.mappers.AuthorMapper;
import com.masantello.bookstoremanager.models.Author;
import com.masantello.bookstoremanager.models.enums.LiteraryGenre;
import com.masantello.bookstoremanager.repositories.AuthorRepository;
import com.masantello.bookstoremanager.validation.AbstractAuthorValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = AuthorService.class)
public class AuthorServiceTest {

    @Autowired
    private AuthorService authorService;

    @MockitoBean
    @Qualifier("authorCreateValidator")
    private AbstractAuthorValidator<AuthorDto> validatorCreate;

    @MockitoBean
    @Qualifier("authorUpdateValidator")
    private AbstractAuthorValidator<AuthorDto> validatorUpdate;

    @MockitoBean
    @Qualifier("authorDeleteValidator")
    private AbstractAuthorValidator<AuthorDto> validatorDelete;

    @MockitoBean
    private AuthorRepository authorRepository;

    @MockitoBean
    private AuthorMapper authorMapper;

    private Author author;
    private AuthorDto authorDto;

    @BeforeEach
    void setup() {
        author = new Author();
        author.setId(1L);
        author.setName("Eckhart Tolle");
        author.setEmail("eckharttolle@gmail.com");
        author.setAge(78);
        author.setBirthDate(LocalDate.of(1948, 2, 16));
        author.setLiteraryGenre(LiteraryGenre.SELF_HELP);

        authorDto = new AuthorDto();
        authorDto.setId(1L);
        authorDto.setName("Eckhart Tolle");
        authorDto.setEmail("eckharttolle@gmail.com");
        authorDto.setAge(78);
        authorDto.setBirthDate(LocalDate.of(1948, 2, 16));
        authorDto.setLiteraryGenre(LiteraryGenre.convertToDescription(LiteraryGenre.SELF_HELP));
    }

    // ==================== CREATE TESTS ====================

    @Test
    @DisplayName("Should create author successfully")
    void testCreateAuthorSuccess() {
        // Arrange
        when(authorMapper.convertToModel(authorDto)).thenReturn(author);
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.convertToDto(author)).thenReturn(authorDto);

        // Act
        AuthorDto result = authorService.create(authorDto);

        // Assert
        assertNotNull(result);
        assertEquals(authorDto.getName(), result.getName());
        assertEquals(authorDto.getEmail(), result.getEmail());
        assertEquals(authorDto.getAge(), result.getAge());

        verify(validatorCreate, times(1)).validate(authorDto);
        verify(authorMapper, times(1)).convertToModel(authorDto);
        verify(authorRepository, times(1)).save(any(Author.class));
        verify(authorMapper, times(1)).convertToDto(author);
    }

    @Test
    @DisplayName("Should throw exception when creating author with invalid data")
    void testCreateAuthorWithValidationError() {
        // Arrange
        doThrow(new IllegalArgumentException("Invalid author data"))
                .when(validatorCreate).validate(authorDto);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authorService.create(authorDto));

        verify(validatorCreate, times(1)).validate(authorDto);
        verify(authorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle DataIntegrityViolationException on create")
    void testCreateAuthorWithDataIntegrityViolation() {
        // Arrange
        when(authorMapper.convertToModel(authorDto)).thenReturn(author);
        when(authorRepository.save(any(Author.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate author"));

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> authorService.create(authorDto));

        verify(validatorCreate, times(1)).validate(authorDto);
    }

    // ==================== FIND ALL TESTS ====================

    @Test
    @DisplayName("Should find all authors successfully")
    void testFindAllAuthorsSuccess() {
        // Arrange
        Author author2 = new Author();
        author2.setId(2L);
        author2.setName("George R.R. Martin");
        author2.setEmail("george@example.com");
        author2.setAge(77);
        author2.setLiteraryGenre(LiteraryGenre.FANTASY);

        AuthorDto authorDto2 = new AuthorDto();
        authorDto2.setId(2L);
        authorDto2.setName("George R.R. Martin");
        authorDto2.setEmail("george@example.com");
        authorDto2.setAge(77);
        authorDto2.setLiteraryGenre("Fantasy");

        List<Author> authors = Arrays.asList(author, author2);
        when(authorRepository.findAll()).thenReturn(authors);
        when(authorMapper.convertToDto(author)).thenReturn(authorDto);
        when(authorMapper.convertToDto(author2)).thenReturn(authorDto2);

        // Act
        List<AuthorDto> result = authorService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Eckhart Tolle", result.get(0).getName());
        assertEquals("George R.R. Martin", result.get(1).getName());

        verify(authorRepository, times(1)).findAll();
        verify(authorMapper, times(2)).convertToDto(any(Author.class));
    }

    @Test
    @DisplayName("Should return empty list when no authors found")
    void testFindAllAuthorsEmpty() {
        // Arrange
        when(authorRepository.findAll()).thenReturn(List.of());

        // Act
        List<AuthorDto> result = authorService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(authorRepository, times(1)).findAll();
    }

    // ==================== FIND BY NAME TESTS ====================

    @Test
    @DisplayName("Should find author by name successfully")
    void testFindByNameSuccess() {
        // Arrange
        when(authorRepository.findByNameContainingIgnoreCase("Stephen"))
                .thenReturn(Optional.of(author));
        when(authorMapper.convertToDto(author)).thenReturn(authorDto);

        // Act
        AuthorDto result = authorService.findByName("Stephen");

        // Assert
        assertNotNull(result);
        assertEquals(authorDto.getName(), result.getName());
        assertEquals(authorDto.getEmail(), result.getEmail());

        verify(authorRepository, times(1)).findByNameContainingIgnoreCase("Stephen");
        verify(authorMapper, times(1)).convertToDto(author);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when author not found by name")
    void testFindByNameNotFound() {
        // Arrange
        String authorName = "Unknown Author";
        when(authorRepository.findByNameContainingIgnoreCase(authorName))
                .thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> authorService.findByName(authorName));

        assertEquals("Author " + authorName + " was not found in database.", exception.getMessage());

        verify(authorRepository, times(1)).findByNameContainingIgnoreCase(authorName);
        verify(authorMapper, never()).convertToDto(any());
    }

    @Test
    @DisplayName("Should find author with case insensitive search")
    void testFindByNameCaseInsensitive() {
        // Arrange
        when(authorRepository.findByNameContainingIgnoreCase("eckhart TOLLE"))
                .thenReturn(Optional.of(author));
        when(authorMapper.convertToDto(author)).thenReturn(authorDto);

        // Act
        AuthorDto result = authorService.findByName("eckhart TOLLE");

        // Assert
        assertNotNull(result);
        assertEquals("Eckhart Tolle", result.getName());

        verify(authorRepository, times(1)).findByNameContainingIgnoreCase("eckhart TOLLE");
    }

    // ==================== UPDATE TESTS ====================

    @Test
    @DisplayName("Should update author successfully")
    void testUpdateAuthorSuccess() {
        // Arrange
        AuthorDto updateDto = new AuthorDto();
        updateDto.setId(1L);
        updateDto.setName("Stephen King Updated");
        updateDto.setEmail("stephen.updated@example.com");
        updateDto.setAge(78);
        updateDto.setBirthDate(LocalDate.of(1947, 9, 21));
        updateDto.setLiteraryGenre("Horror");

        Author updatedAuthor = new Author();
        updatedAuthor.setId(1L);
        updatedAuthor.setName("Stephen King Updated");
        updatedAuthor.setEmail("stephen.updated@example.com");
        updatedAuthor.setAge(78);
        updatedAuthor.setLiteraryGenre(LiteraryGenre.HORROR);

        when(authorMapper.convertToModel(updateDto)).thenReturn(updatedAuthor);
        when(authorRepository.save(updatedAuthor)).thenReturn(updatedAuthor);
        when(authorMapper.convertToDto(updatedAuthor)).thenReturn(updateDto);

        // Act
        AuthorDto result = authorService.updateById(updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("Stephen King Updated", result.getName());
        assertEquals("stephen.updated@example.com", result.getEmail());
        assertEquals(78, result.getAge());

        verify(validatorUpdate, times(1)).validate(updateDto);
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    @DisplayName("Should throw exception when updating author with invalid data")
    void testUpdateAuthorWithValidationError() {
        // Arrange
        doThrow(new IllegalArgumentException("Invalid update data"))
                .when(validatorUpdate).validate(authorDto);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authorService.updateById(authorDto));

        verify(validatorUpdate, times(1)).validate(authorDto);
        verify(authorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update only specified author fields")
    void testUpdateAuthorFieldsCorrectly() {
        // Arrange
        Author newAuthorData = new Author();
        when(authorMapper.convertToModel(authorDto)).thenReturn(newAuthorData);
        when(authorRepository.save(newAuthorData)).thenReturn(newAuthorData);
        when(authorMapper.convertToDto(newAuthorData)).thenReturn(authorDto);

        // Act
        authorService.updateById(authorDto);

        // Assert - Verify that fields were set correctly
        verify(authorRepository, times(1)).save(newAuthorData);
        assertEquals("Eckhart Tolle", newAuthorData.getName());
        assertEquals("eckharttolle@gmail.com", newAuthorData.getEmail());
        assertEquals(78, newAuthorData.getAge());
    }

    // ==================== DELETE TESTS ====================

    @Test
    @DisplayName("Should delete author successfully")
    void testDeleteAuthorSuccess() {
        // Arrange
        Long authorId = 1L;
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(authorMapper.convertToDto(author)).thenReturn(authorDto);

        // Act
        authorService.delete(authorId);

        // Assert
        verify(authorRepository, times(1)).findById(authorId);
        verify(validatorDelete, times(1)).validate(authorDto);
        verify(authorRepository, times(1)).delete(author);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent author")
    void testDeleteAuthorNotFound() {
        // Arrange
        Long authorId = 999L;
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> authorService.delete(authorId));

        assertEquals("Author Id={999} not found.", exception.getMessage());

        verify(authorRepository, times(1)).findById(authorId);
        verify(validatorDelete, never()).validate(any());
        verify(authorRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw exception when deletion validation fails")
    void testDeleteAuthorValidationError() {
        // Arrange
        Long authorId = 1L;
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(authorMapper.convertToDto(author)).thenReturn(authorDto);
        doThrow(new IllegalArgumentException("Cannot delete author"))
                .when(validatorDelete).validate(authorDto);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authorService.delete(authorId));

        verify(authorRepository, times(1)).findById(authorId);
        verify(validatorDelete, times(1)).validate(authorDto);
        verify(authorRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should not perform delete operation if validation fails")
    void testDeleteAuthorValidationFailsNoDelete() {
        // Arrange
        Long authorId = 1L;
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(authorMapper.convertToDto(author)).thenReturn(authorDto);
        doThrow(new DataIntegrityViolationException("Author has associated books"))
                .when(validatorDelete).validate(authorDto);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> authorService.delete(authorId));

        verify(authorRepository, never()).delete(any());
    }

    // ==================== EDGE CASES TESTS ====================

    @Test
    @DisplayName("Should handle null author data in create")
    void testCreateWithNullDto() {
        // Act & Assert
        assertThrows(Exception.class, () -> authorService.create(null));
    }

    @Test
    @DisplayName("Should handle null values in update")
    void testUpdateWithNullFields() {
        // Arrange
        AuthorDto nullDto = new AuthorDto();
        nullDto.setId(1L);
        nullDto.setName(null);

        Author newAuthorData = new Author();
        when(authorMapper.convertToModel(nullDto)).thenReturn(newAuthorData);
        when(authorRepository.save(newAuthorData)).thenReturn(newAuthorData);
        when(authorMapper.convertToDto(newAuthorData)).thenReturn(nullDto);

        // Act
        authorService.updateById(nullDto);

        // Assert
        verify(authorRepository, times(1)).save(newAuthorData);
    }

    @Test
    @DisplayName("Should handle empty string in findByName")
    void testFindByNameEmptyString() {
        // Arrange
        when(authorRepository.findByNameContainingIgnoreCase(""))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> authorService.findByName(""));

        verify(authorRepository, times(1)).findByNameContainingIgnoreCase("");
    }
}
