package com.masantello.bookstoremanager.validation.publisher;

import com.masantello.bookstoremanager.dtos.PublisherDto;
import com.masantello.bookstoremanager.repositories.PublisherRepository;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import jakarta.persistence.EntityExistsException;
import org.springframework.stereotype.Component;

@Component
public class PublisherAlreadyExistsValidator extends AbstractValidator<PublisherDto> {

    private final PublisherRepository publisherRepository;

    public PublisherAlreadyExistsValidator(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Override
    public PublisherDto validate(PublisherDto publisherDto) {
        var publishers = publisherRepository.findByNameContainingIgnoreCase(publisherDto.getName());

        if (!publishers.isEmpty()) {
            var errorMessage = String.format("Publisher '%s' already exists in database.", publisherDto.getName());
            throw new EntityExistsException(errorMessage);
        }

        return validateNext(publisherDto);
    }
}
