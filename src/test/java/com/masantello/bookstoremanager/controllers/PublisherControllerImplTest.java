package com.masantello.bookstoremanager.controllers;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.dtos.PublisherDto;
import com.masantello.bookstoremanager.exceptions.MissingMandatoryFieldsException;
import com.masantello.bookstoremanager.mappers.AuthorMapper;
import com.masantello.bookstoremanager.mappers.PublisherMapper;
import com.masantello.bookstoremanager.models.enums.LiteraryGenre;
import com.masantello.bookstoremanager.services.PublisherService;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

@SpringBootTest(classes = PublisherControllerImpl.class)
public class PublisherControllerImplTest {

    @Autowired
    private PublisherControllerImpl publisherController;

    @MockitoBean
    private PublisherService publisherService;

    @Mock
    private PublisherMapper publisherMapper;

    private PublisherDto publisherDto;

    @BeforeEach
    void setup() {
        publisherDto = new PublisherDto();
        publisherDto.setId(1L);
        publisherDto.setName("Editora Sextante");
        publisherDto.setCode("001");
        publisherDto.setDescription("A Editora Sextante foi fundada em 1998 e é conhecida por seu foco em desenvolvimento "
                + "pessoal, abrangendo autoajuda, empreendedorismo e espiritualidade. "
                + "A editora também publica obras de ficção através da Editora Arqueiro.");
        publisherDto.setFoundationDate(LocalDate.of(1998, 1,1));
    }

    // =============================== CREATE TESTS =======================================
    @Test
    @DisplayName("Should create a new publisher successfully and return 201 CREATED")
    void testCreateAuthorSuccess(){
        //Arrange
        PublisherDto newPublisherDto = getPublisherDto();

        when(publisherService.create(any(PublisherDto.class))).thenReturn(publisherDto);

        //Act
        var response = publisherController.create(newPublisherDto);

        //Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        verify(publisherService, times(1)).create(any(PublisherDto.class));
    }

    @Test
    @DisplayName("Should invoke PublisherService.create exactly once during publisher creation")
    void testCreatePublisherCallsServiceOnce() {
        //Arrange
        when(publisherService.create(any(PublisherDto.class))).thenReturn(publisherDto);

        //Act
        publisherController.create(publisherDto);

        //Assert
        verify(publisherService, times(1)).create(any(PublisherDto.class));
    }

    @Test
    @DisplayName("Should not create an author with null literary genre")
    void testCreatePublisherWithoutItsName() {
        //Arrange
        publisherDto.setName(null);
        when(publisherService.create(any(PublisherDto.class)))
                .thenThrow(new MissingMandatoryFieldsException("Campos obrigatorios ausentes"));

        //Act
        assertThrows(MissingMandatoryFieldsException.class, () -> publisherController.create(publisherDto));

        //Assert
        verify(publisherService, times(1)).create(any(PublisherDto.class));
    }

    // =============================== FIND ALL TESTS =======================================
    @Test
    @DisplayName("Should return all publishers with 200 OK Status Code")
    void testFindAllPublishersSuccess() {
        //Arrange
        List<PublisherDto> publisherDtoList = Arrays.asList(publisherDto,
                new PublisherDto(2L, "Editora", "003", "", LocalDate.of(1965, 7, 31)));
        when(publisherService.findAll()).thenReturn(publisherDtoList);

        //Act
        var response = publisherController.findAll();

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(publisherService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when there are no authors")
    void testFindAllPublishersEmpty() {
        //Arrange
        when(publisherService.findAll()).thenReturn(Collections.emptyList());

        //Act
        var response = publisherController.findAll();

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(publisherService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return correct author data in findAll response")
    void testFindAllAuthorsReturnsCorrectData() {
        //Arrange
        List<PublisherDto> publisherDtoList = Collections.singletonList(publisherDto);
        when(publisherService.findAll()).thenReturn(publisherDtoList);

        //Act
        var response = publisherController.findAll();

        //Assert
        assert response.getBody() != null;
        var returnedPublisherDto = response.getBody().get(0);
        assertEquals(1L, returnedPublisherDto.getId());
        assertEquals("Editora Sextante", returnedPublisherDto.getName());
        assertThat(returnedPublisherDto.getCode()).isEqualTo("001");
        assertEquals("A Editora Sextante foi fundada em 1998 e é conhecida por seu foco em desenvolvimento "
                + "pessoal, abrangendo autoajuda, empreendedorismo e espiritualidade. "
                + "A editora também publica obras de ficção através da Editora Arqueiro.", returnedPublisherDto.getDescription());
        assertThat(returnedPublisherDto.getFoundationDate()).isEqualTo(LocalDate.of(1998, 1, 1));
    }

    // =============================== FIND BY NAME TESTS =======================================
    @Test
    @DisplayName("Should find a publisher by name and return 200 OK")
    void testFindPublisherByNameSuccess() {
        //Arrange
        String publisherName = "editora";
        PublisherDto publisherDto2 = new PublisherDto();
        publisherDto2.setName("Editora Arqueiro");
        publisherDto2.setCode("002");
        publisherDto2.setDescription("A editora arqueiro é uma das principais editoras do Brasil");
        publisherDto2.setFoundationDate(LocalDate.of(2011, 4, 10));
        when(publisherService.findByName(publisherName)).thenReturn(Arrays.asList(publisherDto, publisherDto2));

        //Act
        var response = publisherController.findByName(publisherName);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertThat(response.getBody().get(0).getName()).containsIgnoringCase(publisherName);
        assertThat(response.getBody().get(1).getName()).containsIgnoringCase(publisherName);
        assertNotEquals(publisherName, response.getBody().get(0).getName());
        verify(publisherService, times(1)).findByName(publisherName);
    }

    @Test
    @DisplayName("Should pass part of Publisher's name to service and return correct author data")
    void testFindPublisherByNamePassesPartOfTheAuthorName() {
        //Arrange
        String publisherPartName = "Editora";
        when(publisherService.findByName(publisherPartName)).thenReturn(Collections.singletonList(publisherDto));

        //Act
        var response = publisherController.findByName(publisherPartName);

        //Assert
        assert response.getBody() != null;
        var returnedPublisher = response.getBody().get(0);
        assert returnedPublisher != null;
        assertEquals(1L, returnedPublisher.getId());
        assertEquals("Editora Sextante", returnedPublisher.getName());
        assertEquals("001", returnedPublisher.getCode());
        assertEquals(LocalDate.of(1998, 1,1), returnedPublisher.getFoundationDate());
        verify(publisherService, times(1)).findByName(publisherPartName);
    }

    @Test
    @DisplayName("Should handle special characters in publisher's name")
    void testFindPublisherByNameWithSpecialCharacters() {
        //Arrange
        String publisherTitle = "Editora Conceição";
        when(publisherService.findByName(publisherTitle)).thenReturn(Collections.singletonList(publisherDto));

        //Act
        var response = publisherController.findByName(publisherTitle);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(publisherService, times(1)).findByName(publisherTitle);
    }


    // =============================== DELETE TESTS =======================================
    @Test
    @DisplayName("Should delete publisher and return 204 NO_CONTENT")
    void testDeletePublisherSuccess() {
        //Arrange
        Long publisherId = 1L;
        doNothing().when(publisherService).delete(publisherId);

        //Act
        ResponseEntity<Void> response = publisherController.delete(publisherId);

        //Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(publisherService, times(1)).delete(publisherId);
    }

    @Test
    @DisplayName("Should delete different publishers by ID")
    void testDeleteDifferentPublishers() {
        //Arrange
        doNothing().when(publisherService).delete(anyLong());

        //Act
        publisherController.delete(1L);
        publisherController.delete(2L);
        publisherController.delete(10L);

        //Assert
        verify(publisherService).delete(1L);
        verify(publisherService).delete(2L);
        verify(publisherService).delete(10L);
        verify(publisherService, times(3)).delete(anyLong());
    }

    private static @NonNull PublisherDto getPublisherDto() {
        PublisherDto newPublisherDto = new PublisherDto();
        newPublisherDto.setId(2L);
        newPublisherDto.setName("Editora Arqueiro");
        newPublisherDto.setCode("002");
        newPublisherDto.setDescription("Lançada em 2011, a editora arqueiro é uma das mais importantes do país, "
                + "responsável por publicar grandes autores nacionais e internacionais. Com um catálogo diversificado, "
                + "a editora é conhecida por sua qualidade editorial e por lançar livros que conquistam leitores de todas as idades.");
        newPublisherDto.setFoundationDate(LocalDate.of(2011, 4, 10));
        return newPublisherDto;
    }

}
