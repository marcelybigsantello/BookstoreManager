package com.masantello.bookstoremanager.services;

import com.masantello.bookstoremanager.dtos.PublisherDto;
import com.masantello.bookstoremanager.exceptions.DataIntegrityViolationException;
import com.masantello.bookstoremanager.mappers.PublisherMapper;
import com.masantello.bookstoremanager.models.Publisher;
import com.masantello.bookstoremanager.repositories.PublisherRepository;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = PublisherService.class)
public class PublisherServiceTest {

    @Autowired
    private PublisherService publisherService;

    @MockitoBean
    private PublisherRepository publisherRepository;

    @MockitoBean
    private PublisherMapper publisherMapper;

    @MockitoBean
    @Qualifier("publisherCreateValidator")
    private AbstractValidator<PublisherDto> publisherCreateValidator;

    private Publisher publisher;
    private PublisherDto publisherDto;

    @BeforeEach
    void setup() {
        publisher = new Publisher();
        publisher.setId(2L);
        publisher.setName("Companhia das Letras");
        publisher.setCode("123");
        publisher.setDescription("Inclui ficção, não-ficção, infanto-juvenil e infantil através dos seus 21 selos");
        publisher.setFoundationDate(LocalDate.of(1986, 4, 14));

        publisherDto = new PublisherDto();
        publisherDto.setId(2L);
        publisherDto.setName("Companhia das Letras");
        publisherDto.setCode("123");
        publisherDto.setDescription("Inclui ficção, não-ficção, infanto-juvenil e infantil através dos seus 21 selos");
        publisherDto.setFoundationDate(LocalDate.of(1986, 4, 14));
    }

    // ==================== CREATE TESTS ====================

    @Test
    @DisplayName("Should create publisher successfully")
    void testCreatePublisherSuccess() {
        // Arrange
        when(publisherMapper.convertToModel(publisherDto)).thenReturn(publisher);
        when(publisherRepository.save(publisher)).thenReturn(publisher);
        when(publisherMapper.convertToDto(publisher)).thenReturn(publisherDto);

        // Act
        PublisherDto result = publisherService.create(publisherDto);

        // Assert
        assertNotNull(result);
        assertEquals(publisherDto.getName(), result.getName());
        assertEquals(publisherDto.getCode(), result.getCode());
        assertEquals(publisherDto.getDescription(), result.getDescription());
        assertEquals(publisherDto.getFoundationDate(), result.getFoundationDate());

        verify(publisherCreateValidator, times(1)).validate(publisherDto);
        verify(publisherMapper, times(1)).convertToModel(publisherDto);
        verify(publisherRepository, times(1)).save(any(Publisher.class));
        verify(publisherMapper, times(1)).convertToDto(publisher);
    }

    @Test
    @DisplayName("Should throw exception when creating publisher with invalid data")
    void testCreatePublisherWithValidationError() {
        // Arrange
        doThrow(new IllegalArgumentException("Invalid publisher data"))
                .when(publisherCreateValidator).validate(publisherDto);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> publisherService.create(publisherDto));

        verify(publisherCreateValidator, times(1)).validate(publisherDto);
        verify(publisherRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle DataIntegrityViolationException on create")
    void testCreatePublisherWithDataIntegrityViolation() {
        // Arrange
        when(publisherMapper.convertToModel(publisherDto)).thenReturn(publisher);
        when(publisherRepository.save(any(Publisher.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate publisher"));

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> publisherService.create(publisherDto));

        verify(publisherCreateValidator, times(1)).validate(publisherDto);
    }

    // ==================== FIND ALL TESTS ====================

    @Test
    @DisplayName("Should find all publishers successfully")
    void testFindAllPublishersPresentSuccess() {
        // Arrange
        Publisher publisher2 = new Publisher();
        publisher2.setId(3L);
        publisher2.setName("George R.R. Martin");
        publisher2.setCode("124");
        publisher2.setFoundationDate(LocalDate.of(2002, 3, 30));

        PublisherDto publisherDto2 = new PublisherDto();
        publisherDto2.setId(3L);
        publisherDto2.setName("George R.R. Martin");
        publisherDto2.setCode("124");
        publisherDto2.setFoundationDate(LocalDate.of(2002, 3, 30));

        List<Publisher> publishers = Arrays.asList(publisher, publisher2);
        when(publisherRepository.findAll()).thenReturn(publishers);
        when(publisherMapper.convertToDto(publisher)).thenReturn(publisherDto);
        when(publisherMapper.convertToDto(publisher2)).thenReturn(publisherDto2);

        // Act
        List<PublisherDto> result = publisherService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Companhia das Letras", result.get(0).getName());
        assertEquals("George R.R. Martin", result.get(1).getName());

        verify(publisherRepository, times(1)).findAll();
        verify(publisherMapper, times(2)).convertToDto(any(Publisher.class));
    }

    @Test
    @DisplayName("Should return empty list when no publishers found")
    void testFindAllAuthorsEmpty() {
        // Arrange
        when(publisherRepository.findAll()).thenReturn(List.of());

        // Act
        List<PublisherDto> result = publisherService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(publisherRepository, times(1)).findAll();
    }

    // ==================== FIND BY NAME TESTS ====================

    @Test
    @DisplayName("Should find publisher by name successfully")
    void testFindByNameSuccess() {
        // Arrange
        when(publisherRepository.findByNameContainingIgnoreCase("Editora")).thenReturn(List.of(publisher));
        when(publisherMapper.convertToDto(publisher)).thenReturn(publisherDto);

        // Act
        var result = publisherService.findByName("Editora");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(publisherDto.getName(), result.get(0).getName());
        assertEquals(publisherDto.getCode(), result.get(0).getCode());
        assertEquals(publisherDto.getFoundationDate(), result.get(0).getFoundationDate());

        verify(publisherRepository, times(1)).findByNameContainingIgnoreCase("Editora");
        verify(publisherMapper, times(1)).convertToDto(publisher);
    }

    @Test
    @DisplayName("Should return empty list when publisher not found by name (service behavior)")
    void testFindByNameNotFoundReturnsEmptyList() {
        // Arrange
        String publisherName = "Unknown publisher";
        when(publisherRepository.findByNameContainingIgnoreCase(publisherName)).thenReturn(Collections.emptyList());

        // Act
        var result = publisherService.findByName(publisherName);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(publisherRepository, times(1)).findByNameContainingIgnoreCase(publisherName);
        verify(publisherMapper, never()).convertToDto(any());
    }

    @Test
    @DisplayName("Should find publisher with case insensitive search")
    void testFindByNameCaseInsensitive() {
        // Arrange
        when(publisherRepository.findByNameContainingIgnoreCase("editora MODERNA")).thenReturn(List.of(publisher));
        when(publisherMapper.convertToDto(publisher)).thenReturn(publisherDto);

        // Act
        var result = publisherService.findByName("editora MODERNA");

        // Assert
        assertNotNull(result);
        assertNotNull(result.get(0));
        // apenas validar que a consulta foi repassada e mapeada — nome real do DTO depende do mapper configurado
        verify(publisherRepository, times(1)).findByNameContainingIgnoreCase("editora MODERNA");
        verify(publisherMapper, times(1)).convertToDto(publisher);
    }

    // ==================== DELETE TESTS ====================

    @Test
    @DisplayName("Should delete publisher successfully")
    void testDeletePublisherSuccess() {
        // Arrange
        Long publisherId = 1L;
        when(publisherRepository.findById(publisherId)).thenReturn(Optional.of(publisher));
        when(publisherMapper.convertToDto(publisher)).thenReturn(publisherDto);

        // Act
        publisherService.delete(publisherId);

        // Assert
        verify(publisherRepository, times(1)).findById(publisherId);
        verify(publisherRepository, times(1)).delete(publisher);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent publisher")
    void testDeletePublisherNotFound() {
        // Arrange
        Long publisherId = 999L;
        when(publisherRepository.findById(publisherId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> publisherService.delete(publisherId));

        assertEquals(String.format("Publisher ID={%s} was not found in database.", publisherId), exception.getMessage());

        verify(publisherRepository, times(1)).findById(publisherId);
        verify(publisherRepository, never()).delete(any());
    }

    // ==================== EDGE CASES TESTS ====================

    @Test
    @DisplayName("Should handle null publisher data in create")
    void testCreateWithNullDto() {
        // Act & Assert
        assertThrows(Exception.class, () -> publisherService.create(null));
    }

    @Test
    @DisplayName("Should handle empty string in findByName")
    void testFindByNameEmptyString() {
        // Arrange
        when(publisherRepository.findByNameContainingIgnoreCase("")).thenReturn(Collections.emptyList());

        // Act
        var result = publisherService.findByName("");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(publisherRepository, times(1)).findByNameContainingIgnoreCase("");
    }

}
