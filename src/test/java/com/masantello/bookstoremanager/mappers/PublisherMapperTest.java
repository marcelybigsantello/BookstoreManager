package com.masantello.bookstoremanager.mappers;

import com.masantello.bookstoremanager.dtos.PublisherDto;
import com.masantello.bookstoremanager.models.Publisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = PublisherMapper.class)
public class PublisherMapperTest {

    @Autowired
    private PublisherMapper publisherMapper;

    @Mock
    private Publisher publisher;

    @Mock
    private PublisherDto publisherDto;

    @BeforeEach
    void setup() {
        this.publisher = new Publisher();
        this.publisherDto = new PublisherDto();
    }

    @Test
    void convertToModel_shouldReturnPublisherWithAllFields_convertedFromDto() {
        //Arrange
        publisherDto.setId(1L);
        publisherDto.setName("Editora Arqueiro");
        publisherDto.setCode("001");
        publisherDto.setDescription("Lançada em 2011, a editora arqueiro é uma das mais importantes do país, responsável por publicar grandes autores nacionais e internacionais. Com um catálogo diversificado, a editora é conhecida por sua qualidade editorial e por lançar livros que conquistam leitores de todas as idades.");
        publisherDto.setFoundationDate(LocalDate.of(2011, 4, 10));

        //Act
        var publisher = publisherMapper.convertToModel(publisherDto);

        //Assert
        assertThat(publisher).isNotNull();
        assertEquals(publisher.getId(), publisherDto.getId());
        assertEquals(publisher.getName(), publisherDto.getName());
        assertEquals(publisher.getCode(), publisherDto.getCode());
        assertEquals(publisher.getDescription(), publisherDto.getDescription());
        assertEquals(publisher.getFoundationDate(), publisherDto.getFoundationDate());
    }

    @Test
    void convertToModel_shouldReturnPublisherWithNullFields_convertedFromDto() {
        //Arrange

        //Act
        var publisher = publisherMapper.convertToModel(publisherDto);

        //Assert
        assertThat(publisher).isNotNull();
        assertEquals(publisher.getId(), publisherDto.getId());
        assertEquals(publisher.getName(), publisherDto.getName());
        assertEquals(publisher.getCode(), publisherDto.getCode());
        assertEquals(publisher.getDescription(), publisherDto.getDescription());
        assertEquals(publisher.getFoundationDate(), publisherDto.getFoundationDate());
    }

    @Test
    void convertToDto_shouldReturnPublisherWithAllFields_convertedFromModel() {
        //Arrange
        publisher.setId(1L);
        publisher.setName("Companhia das Letras");
        publisher.setCode("002");
        publisher.setDescription("A editora Companhia das Letras");
        publisher.setFoundationDate(LocalDate.of(1986, 4, 14));

        //Act
        var publisherDto = publisherMapper.convertToDto(publisher);

        //Assert
        assertThat(publisherDto).isNotNull();
        assertEquals(publisherDto.getId(), publisher.getId());
        assertEquals(publisherDto.getName(), publisher.getName());
        assertEquals(publisherDto.getCode(), publisher.getCode());
        assertEquals(publisherDto.getDescription(), publisher.getDescription());
        assertEquals(publisherDto.getFoundationDate(), publisher.getFoundationDate());
    }

    @Test
    void convertToDto_shouldReturnAuthorWithNullFields_convertedFromDto() {
        //Arrange

        //Act
        var publisher = publisherMapper.convertToModel(publisherDto);

        //Assert
        assertThat(publisher).isNotNull();
        assertEquals(publisher.getId(), publisherDto.getId());
        assertEquals(publisher.getName(), publisherDto.getName());
        assertEquals(publisher.getCode(), publisherDto.getCode());
        assertEquals(publisher.getDescription(), publisherDto.getDescription());
        assertEquals(publisher.getFoundationDate(), publisherDto.getFoundationDate());
    }
}
