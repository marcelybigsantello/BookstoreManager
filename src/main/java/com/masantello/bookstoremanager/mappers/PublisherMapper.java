package com.masantello.bookstoremanager.mappers;

import com.masantello.bookstoremanager.dtos.PublisherDto;
import com.masantello.bookstoremanager.models.Publisher;
import org.springframework.stereotype.Component;

@Component
public class PublisherMapper {

    public Publisher convertToModel(PublisherDto publisherDto) {
        Publisher publisher = new Publisher();
        publisher.setId(publisherDto.getId());
        publisher.setName(publisherDto.getName());
        publisher.setCode(publisherDto.getCode());
        publisher.setDescription(publisherDto.getDescription());
        publisher.setFoundationDate(publisherDto.getFoundationDate());

        return publisher;
    }

    public PublisherDto convertToDto(Publisher publisher) {
        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(publisher.getId());
        publisherDto.setName(publisher.getName());
        publisherDto.setCode(publisher.getCode());
        publisherDto.setDescription(publisher.getDescription());
        publisherDto.setFoundationDate(publisher.getFoundationDate());

        return publisherDto;
    }
}
