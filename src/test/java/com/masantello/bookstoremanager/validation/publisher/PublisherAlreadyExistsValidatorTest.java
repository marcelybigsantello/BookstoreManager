package com.masantello.bookstoremanager.validation.publisher;

import com.masantello.bookstoremanager.dtos.PublisherDto;
import com.masantello.bookstoremanager.models.Publisher;
import com.masantello.bookstoremanager.repositories.PublisherRepository;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = PublisherAlreadyExistsValidator.class)
public class PublisherAlreadyExistsValidatorTest {

    @Autowired
    private PublisherAlreadyExistsValidator validator;

    @MockitoBean
    private PublisherRepository publisherRepository;

    private PublisherDto publisherDto;
    private Publisher publisher;

    @BeforeEach
    void setup() {
        publisher = new Publisher();
        publisher.setId(1L);
        publisher.setName("Editora Moderna");
        publisher.setCode("001");
        publisher.setFoundationDate(LocalDate.of(1990, 1, 1));

        publisherDto = new PublisherDto();
        publisherDto.setId(1L);
        publisherDto.setName("Editora Moderna");
        publisherDto.setCode("001");
        publisherDto.setFoundationDate(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("Quando já existe publisher com mesmo nome deve lançar EntityExistsException e não chamar next")
    void testValidate_whenPublisherAlreadyExists_shouldThrowEntityExistsException() {
        // Arrange
        when(publisherRepository.findByNameContainingIgnoreCase(publisherDto.getName()))
                .thenReturn(List.of(publisher));

        // Act & Assert
        EntityExistsException ex = assertThrows(EntityExistsException.class,
                () -> validator.validate(publisherDto));
        assertEquals(String.format("Publisher '%s' already exists in database.", publisherDto.getName()), ex.getMessage());

        // next não deve ser chamado (não há next configurado por padrão; se houver, não será invocado)
        // Garantir que o repositório foi consultado
        verify(publisherRepository, times(1)).findByNameContainingIgnoreCase(publisherDto.getName());
    }

    @Test
    @DisplayName("Quando não existe publisher e next é null deve retornar o próprio PublisherDto")
    void testValidate_whenNoPublisherExists_andNoNext_shouldReturnSameDto() {
        // Arrange
        when(publisherRepository.findByNameContainingIgnoreCase(publisherDto.getName()))
                .thenReturn(Collections.emptyList());

        // Act
        PublisherDto result = validator.validate(publisherDto);

        // Assert
        assertSame(publisherDto, result);
        verify(publisherRepository, times(1)).findByNameContainingIgnoreCase(publisherDto.getName());
    }

    @Test
    @DisplayName("Quando não existe publisher e existe next deve chamar next.validate e retornar seu resultado")
    void testValidate_whenNoPublisherExists_andHasNext_shouldCallNextAndReturnItsResult() {
        // Arrange
        when(publisherRepository.findByNameContainingIgnoreCase(publisherDto.getName()))
                .thenReturn(Collections.emptyList());

        @SuppressWarnings("unchecked")
        AbstractValidator<PublisherDto> next = mock(AbstractValidator.class);
        PublisherDto nextResult = new PublisherDto();
        nextResult.setName("after-next");
        when(next.validate(publisherDto)).thenReturn(nextResult);

        validator.next = next;

        // Act
        PublisherDto result = validator.validate(publisherDto);

        // Assert
        assertNotNull(result);
        assertEquals("after-next", result.getName());
        verify(publisherRepository, times(1)).findByNameContainingIgnoreCase(publisherDto.getName());
        verify(next, times(1)).validate(publisherDto);
    }

    @Test
    @DisplayName("Quando publisherDto é null deve lançar NullPointerException antes de chamar repositório")
    void testValidate_whenDtoIsNull_shouldThrowNpeAndNotCallRepository() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> validator.validate(null));

        // repository não deve ser chamado porque publisherDto.getName() lança NPE primeiro
        verify(publisherRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("Quando o repositório retorna null deve lançar NullPointerException")
    void testValidate_whenRepositoryReturnsNull_shouldThrowNpe() {
        // Arrange
        when(publisherRepository.findByNameContainingIgnoreCase(publisherDto.getName()))
                .thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> validator.validate(publisherDto));

        verify(publisherRepository, times(1)).findByNameContainingIgnoreCase(publisherDto.getName());
    }

    @Test
    @DisplayName("Quando next.validate retorna null o resultado final deve ser null")
    void testValidate_whenNextReturnsNull_shouldReturnNull() {
        // Arrange
        when(publisherRepository.findByNameContainingIgnoreCase(publisherDto.getName()))
                .thenReturn(Collections.emptyList());

        @SuppressWarnings("unchecked")
        AbstractValidator<PublisherDto> next = mock(AbstractValidator.class);
        when(next.validate(publisherDto)).thenReturn(null);

        validator.next = next;

        // Act
        PublisherDto result = validator.validate(publisherDto);

        // Assert
        assertNull(result);
        verify(next, times(1)).validate(publisherDto);
        verify(publisherRepository, times(1)).findByNameContainingIgnoreCase(publisherDto.getName());
    }
}
