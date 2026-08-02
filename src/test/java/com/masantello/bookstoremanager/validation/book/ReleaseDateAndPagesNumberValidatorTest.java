package com.masantello.bookstoremanager.validation.book;

import com.masantello.bookstoremanager.dtos.BookDto;
import com.masantello.bookstoremanager.exceptions.DataIntegrityViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ReleaseDateAndPagesNumberValidator.class)
public class ReleaseDateAndPagesNumberValidatorTest {

    @Autowired
    private ReleaseDateAndPagesNumberValidator releaseDateAndPagesNumberValidator;

    @Test
    @DisplayName("Deve validar quando releaseDate está no passado recente e pages > 0 (caminho feliz)")
    void validate_shouldReturnSameDto_whenReleaseDateAndPagesAreValid() {
        //Arrange
        var bookDto = new BookDto();
        bookDto.setTitle("Valid Book");
        bookDto.setReleaseDate(LocalDate.now().minusYears(10));
        bookDto.setPages(200);

        //Act
        var result = releaseDateAndPagesNumberValidator.validate(bookDto);

        //Assert
        assertNotNull(result);
        assertEquals(result.getReleaseDate(), bookDto.getReleaseDate());
        assertSame(bookDto, result, "O validator deve retornar o mesmo BookDto quando válido");
    }

    @Test
    @DisplayName("Deve lançar DataIntegrityViolationException quando releaseDate está no futuro")
    void validate_shouldThrow_whenReleaseDateIsFuture() {
        //Arrange
        var bookDto = new BookDto();
        bookDto.setTitle("Future Book");
        bookDto.setReleaseDate(LocalDate.now().plusDays(10));
        bookDto.setPages(100);

        //Act && Assert
        assertThrows(DataIntegrityViolationException.class, () -> releaseDateAndPagesNumberValidator.validate(bookDto),
                "Deve lançar exceção se a data de lançamento estiver no futuro");
    }

    @Test
    @DisplayName("Deve lançar DataIntegrityViolationException quando releaseDate está antes do ano 1100")
    void validate_shouldThrow_whenReleaseDateBeforeYear1100() {
        var bookDto = new BookDto();
        bookDto.setTitle("Ancient book");
        bookDto.setReleaseDate(LocalDate.of(1000, 1, 1));
        bookDto.setPages(100);

        //Act && Assert
        assertThrows(DataIntegrityViolationException.class,
                () -> releaseDateAndPagesNumberValidator.validate(bookDto),
                "Deve lançar exceção quando a data de lançamento for anterior ao ano 1100");
    }

    @Test
    @DisplayName("Deve lançar DataIntegrityViolationException se número de páginas for menor ou igual a 0")
    void validate_shouldThrow_whenPagesIsNonPositive() {
        var bookDto = new BookDto();
        bookDto.setTitle("Book with no pages");
        bookDto.setReleaseDate(LocalDate.now().minusYears(6));
        bookDto.setPages(-1);

        assertThrows(DataIntegrityViolationException.class,
                () -> releaseDateAndPagesNumberValidator.validate(bookDto),
                "Deve lançar exceção se o número de páginas for menor ou igual a zero");
    }


}
