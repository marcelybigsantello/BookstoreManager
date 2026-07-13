package com.masantello.bookstoremanager.services;

import com.masantello.bookstoremanager.dtos.PublisherDto;
import com.masantello.bookstoremanager.mappers.PublisherMapper;
import com.masantello.bookstoremanager.models.Publisher;
import com.masantello.bookstoremanager.repositories.PublisherRepository;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublisherService {

    private static final Logger logger = LoggerFactory.getLogger(PublisherService.class);

    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;
    private final AbstractValidator<PublisherDto> validatorCreate;

    public PublisherService(PublisherRepository publisherRepository,
                            PublisherMapper publisherMapper,
                            @Qualifier("publisherCreateValidator")
                            AbstractValidator<PublisherDto> validatorCreate) {
        this.publisherRepository = publisherRepository;
        this.publisherMapper = publisherMapper;
        this.validatorCreate = validatorCreate;
    }

    public PublisherDto create(PublisherDto publisherDto) {
        logger.debug("Validating publisher request's information");
        validatorCreate.validate(publisherDto);

        var publisher = publisherMapper.convertToModel(publisherDto);
        publisher = publisherRepository.save(publisher);

        logger.info("Publisher ID={}, Name={} created successfully.", publisher.getId(), publisher.getName());
        return publisherMapper.convertToDto(publisher);
    }

    public List<PublisherDto> findAll() {
        var publishers = publisherRepository.findAll();

        return publishers.stream().map(publisherMapper::convertToDto).collect(Collectors.toList());
    }

    public List<PublisherDto> findByName(String publisherName) {
        var publishers = publisherRepository.findByNameContainingIgnoreCase(publisherName);

        if (publishers.isEmpty()) {
            logger.error("Publisher's name {} does not exist in database. You should try another one.", publisherName);
            return Collections.emptyList();
        }

        logger.info("{} publisher(s) containing '{}' were found in database", publishers.size(), publisherName);

        return publishers.stream()
                .map(publisherMapper::convertToDto)
                .collect(Collectors.toList());
    }

    public void delete(Long publisherId) {
        var publisher = publisherRepository.findById(publisherId);

        publisher.ifPresentOrElse(publisher1 -> {
            logger.info("Deleting publisher='{}', ID={}", publisher1.getName(), publisher1.getName());
            publisherRepository.delete(publisher1);
        }, () -> {
            var errorMessage = String.format("Publisher ID={%s} was not found in database.", publisherId);
            throw new EntityNotFoundException(errorMessage);
        });
    }

    public Publisher findById(@NotNull Long publisherId) {
        return publisherRepository.findById(publisherId)
                .map(publisher -> {
                    logger.info("Publisher ID={}, Name={} found in database", publisher.getId(), publisher.getName());
                    return publisher;
                })
                .orElseThrow(() -> {
                    var errorMessage = String.format("Publisher ID={%s} was not found in database.", publisherId);
                    logger.error(errorMessage);
                    return new EntityNotFoundException(errorMessage);
                });
    }
}
