package com.masantello.bookstoremanager.validation.publisher;

import com.masantello.bookstoremanager.dtos.PublisherDto;
import com.masantello.bookstoremanager.exceptions.MissingMandatoryFieldsException;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PublisherMandatoryFieldsValidator extends AbstractValidator<PublisherDto> {

    public static final String MISSING_MANDATORY_PUBLISHER_FIELDS = "Campos obrigatórios de editora ausentes";
    private static final Logger logger = LoggerFactory.getLogger(PublisherMandatoryFieldsValidator.class);

    @Override
    public PublisherDto validate(PublisherDto publisherDto) {
        logger.trace("Validating mandatory fields in publisher's creation request");

        if (publisherDto.getName() == null || publisherDto.getCode() == null) {
            logger.error("There are missing mandatory fields in publisher creation request. "
                    + "Publisher={}", publisherDto);
            throw new MissingMandatoryFieldsException(MISSING_MANDATORY_PUBLISHER_FIELDS);
        }

        return validateNext(publisherDto);
    }
}
