package com.masantello.bookstoremanager.validation.publisher;

import com.masantello.bookstoremanager.dtos.PublisherDto;
import com.masantello.bookstoremanager.exceptions.MissingMandatoryFieldsException;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = PublisherMandatoryFieldsValidator.class)
public class PublisherMandatoryFieldsValidatorTest {

    @Autowired
    private PublisherMandatoryFieldsValidator validator;

    private PublisherDto publisherDto;

    @BeforeEach
    void setup() {
        publisherDto = new PublisherDto();
        publisherDto.setId(1L);
        publisherDto.setName("Editora Exemplo");
        publisherDto.setCode("EX001");
        publisherDto.setDescription("Uma editora de exemplo");
        publisherDto.setFoundationDate(LocalDate.of(2000, 1, 1));
    }

    @Test
    @DisplayName("Quando name e code são válidos e não há next, deve retornar o mesmo PublisherDto")
    void testValidate_whenNameAndCodeAreValid_andNoNext_shouldReturnSameDto() {
        // Act
        PublisherDto result = validator.validate(publisherDto);

        // Assert
        assertSame(publisherDto, result);
        assertEquals("Editora Exemplo", result.getName());
        assertEquals("EX001", result.getCode());
    }

    @Test
    @DisplayName("Quando name e code são válidos e existe next, deve chamar next.validate e retornar seu resultado")
    void testValidate_whenNameAndCodeAreValid_andHasNext_shouldCallNextAndReturnItsResult() {
        // Arrange
        @SuppressWarnings("unchecked")
        AbstractValidator<PublisherDto> next = mock(AbstractValidator.class);
        PublisherDto nextResult = new PublisherDto();
        nextResult.setName("Modificado");
        nextResult.setCode("MOD001");
        when(next.validate(publisherDto)).thenReturn(nextResult);

        validator.next = next;

        // Act
        PublisherDto result = validator.validate(publisherDto);

        // Assert
        assertNotNull(result);
        assertEquals("Modificado", result.getName());
        assertEquals("MOD001", result.getCode());
        verify(next, times(1)).validate(publisherDto);
    }

    @Test
    @DisplayName("Quando name é null deve lançar MissingMandatoryFieldsException")
    void testValidate_whenNameIsNull_shouldThrowMissingMandatoryFieldsException() {
        // Arrange
        publisherDto.setName(null);

        // Act & Assert
        MissingMandatoryFieldsException exception = assertThrows(
                MissingMandatoryFieldsException.class,
                () -> validator.validate(publisherDto)
        );

        assertEquals(PublisherMandatoryFieldsValidator.MISSING_MANDATORY_PUBLISHER_FIELDS,
                exception.getMessage());
    }

    @Test
    @DisplayName("Quando code é null deve lançar MissingMandatoryFieldsException")
    void testValidate_whenCodeIsNull_shouldThrowMissingMandatoryFieldsException() {
        // Arrange
        publisherDto.setCode(null);

        // Act & Assert
        MissingMandatoryFieldsException exception = assertThrows(
                MissingMandatoryFieldsException.class,
                () -> validator.validate(publisherDto)
        );

        assertEquals(PublisherMandatoryFieldsValidator.MISSING_MANDATORY_PUBLISHER_FIELDS,
                exception.getMessage());
    }

    @Test
    @DisplayName("Quando name e code são null deve lançar MissingMandatoryFieldsException")
    void testValidate_whenNameAndCodeAreNull_shouldThrowMissingMandatoryFieldsException() {
        // Arrange
        publisherDto.setName(null);
        publisherDto.setCode(null);

        // Act & Assert
        MissingMandatoryFieldsException exception = assertThrows(
                MissingMandatoryFieldsException.class,
                () -> validator.validate(publisherDto)
        );

        assertEquals(PublisherMandatoryFieldsValidator.MISSING_MANDATORY_PUBLISHER_FIELDS,
                exception.getMessage());
    }

    @Test
    @DisplayName("Quando publisherDto é null deve lançar NullPointerException")
    void testValidate_whenDtoIsNull_shouldThrowNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> validator.validate(null));
    }

    @Test
    @DisplayName("Quando name é vazio e code é válido deve retornar o DTO (validação apenas de null)")
    void testValidate_whenNameIsEmptyStringAndCodeIsValid_shouldReturnDto() {
        // Arrange
        publisherDto.setName("");

        // Act
        PublisherDto result = validator.validate(publisherDto);

        // Assert
        assertSame(publisherDto, result);
        assertEquals("", result.getName());
    }

    @Test
    @DisplayName("Quando code é vazio e name é válido deve retornar o DTO (validação apenas de null)")
    void testValidate_whenCodeIsEmptyStringAndNameIsValid_shouldReturnDto() {
        // Arrange
        publisherDto.setCode("");

        // Act
        PublisherDto result = validator.validate(publisherDto);

        // Assert
        assertSame(publisherDto, result);
        assertEquals("", result.getCode());
    }

    @Test
    @DisplayName("Quando description é null mas name e code são válidos deve retornar o DTO")
    void testValidate_whenDescriptionIsNull_shouldReturnDto() {
        // Arrange
        publisherDto.setDescription(null);

        // Act
        PublisherDto result = validator.validate(publisherDto);

        // Assert
        assertSame(publisherDto, result);
        assertNull(result.getDescription());
    }

    @Test
    @DisplayName("Quando foundationDate é null mas name e code são válidos deve retornar o DTO")
    void testValidate_whenFoundationDateIsNull_shouldReturnDto() {
        // Arrange
        publisherDto.setFoundationDate(null);

        // Act
        PublisherDto result = validator.validate(publisherDto);

        // Assert
        assertSame(publisherDto, result);
        assertNull(result.getFoundationDate());
    }

    @Test
    @DisplayName("Quando name tem valor e code tem valor mas vazios devem passar na validação")
    void testValidate_whenNameAndCodeAreEmptyStrings_shouldReturnDto() {
        // Arrange
        publisherDto.setName("");
        publisherDto.setCode("");

        // Act
        PublisherDto result = validator.validate(publisherDto);

        // Assert
        assertSame(publisherDto, result);
        assertEquals("", result.getName());
        assertEquals("", result.getCode());
    }

    @Test
    @DisplayName("Quando name é válido com espaços e code é válido deve retornar o DTO")
    void testValidate_whenNameHasSpacesAndCodeIsValid_shouldReturnDto() {
        // Arrange
        publisherDto.setName("   Editora com Espaços   ");

        // Act
        PublisherDto result = validator.validate(publisherDto);

        // Assert
        assertSame(publisherDto, result);
        assertEquals("   Editora com Espaços   ", result.getName());
    }

    @Test
    @DisplayName("Quando next.validate retorna null, o resultado deve ser null")
    void testValidate_whenNextReturnsNull_shouldReturnNull() {
        // Arrange
        @SuppressWarnings("unchecked")
        AbstractValidator<PublisherDto> next = mock(AbstractValidator.class);
        when(next.validate(publisherDto)).thenReturn(null);

        validator.next = next;

        // Act
        PublisherDto result = validator.validate(publisherDto);

        // Assert
        assertNull(result);
        verify(next, times(1)).validate(publisherDto);
    }
}
