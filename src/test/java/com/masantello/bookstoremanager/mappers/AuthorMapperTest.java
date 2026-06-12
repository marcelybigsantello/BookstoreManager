package com.masantello.bookstoremanager.mappers;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.models.Author;
import com.masantello.bookstoremanager.models.enums.LiteraryGenre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = AuthorMapper.class)
public class AuthorMapperTest {

    @Autowired
    private AuthorMapper authorMapper;

    @Mock
    private Author author;

    @Mock
    private AuthorDto authorDto;

    @BeforeEach
    void setup() {
        author = new Author();
        authorDto = new AuthorDto();
    }

    @Test
    void convertToModel_shouldReturnAuthorWithAllFields_convertedFromDto() {
        //Arrange
        authorDto.setId(1L);
        authorDto.setName("Kiera Cass");
        authorDto.setEmail("kieracassofficial@gmail.com");
        authorDto.setAge(30);
        authorDto.setBirthDate(LocalDate.of(1982, 6, 9));
        authorDto.setLiteraryGenre("Romance");

        //Act
        var author = authorMapper.convertToModel(authorDto);

        //Assert
        assertThat(author).isNotNull();
        assertEquals(author.getId(), authorDto.getId());
        assertEquals(author.getName(), authorDto.getName());
        assertEquals(author.getEmail(), authorDto.getEmail());
        assertEquals(author.getBirthDate(), authorDto.getBirthDate());
        assertEquals(author.getAge(), authorDto.getAge());
        assertEquals(author.getLiteraryGenre(), LiteraryGenre.findByDescription(authorDto.getLiteraryGenre()));
    }

    @Test
    void convertToModel_shouldReturnAuthorWithNullFields_convertedFromDto() {
        //Arrange

        //Act
        var author = authorMapper.convertToModel(authorDto);

        //Assert
        assertThat(author).isNotNull();
        assertEquals(author.getId(), authorDto.getId());
        assertEquals(author.getName(), authorDto.getName());
        assertEquals(author.getEmail(), authorDto.getEmail());
        assertEquals(author.getBirthDate(), authorDto.getBirthDate());
        assertEquals(author.getAge(), authorDto.getAge());
        assertEquals(author.getLiteraryGenre(), LiteraryGenre.findByDescription(authorDto.getLiteraryGenre()));
    }

    @Test
    void convertToDto_shouldReturnAuthorWithAllFields_convertedFromModel() {
        //Arrange
        author.setId(1L);
        author.setName("Kiera Cass");
        author.setEmail("kieracassofficial@gmail.com");
        author.setAge(30);
        author.setBirthDate(LocalDate.of(1982, 6, 9));
        author.setLiteraryGenre(LiteraryGenre.findByDescription("Romance"));

        //Act
        var authorDto = authorMapper.convertToDto(author);

        //Assert
        assertThat(authorDto).isNotNull();
        assertEquals(authorDto.getId(), author.getId());
        assertEquals(authorDto.getName(), author.getName());
        assertEquals(authorDto.getEmail(), author.getEmail());
        assertEquals(authorDto.getBirthDate(), author.getBirthDate());
        assertEquals(authorDto.getAge(), author.getAge());
        assertEquals(authorDto.getLiteraryGenre(), LiteraryGenre.convertToDescription(LiteraryGenre.ROMANCE));
    }

    @Test
    void convertToDto_shouldReturnAuthorWithNullFields_convertedFromDto() {
        //Arrange

        //Act
        var author = authorMapper.convertToModel(authorDto);

        //Assert
        assertThat(author).isNotNull();
        assertEquals(author.getId(), authorDto.getId());
        assertEquals(author.getName(), authorDto.getName());
        assertEquals(author.getEmail(), authorDto.getEmail());
        assertEquals(author.getBirthDate(), authorDto.getBirthDate());
        assertEquals(author.getAge(), authorDto.getAge());
        assertEquals(author.getLiteraryGenre(), LiteraryGenre.findByDescription(authorDto.getLiteraryGenre()));
    }

}
