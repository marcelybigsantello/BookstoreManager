package com.masantello.bookstoremanager.validation.publisher;

import com.masantello.bookstoremanager.dtos.PublisherDto;
import com.masantello.bookstoremanager.validation.AbstractValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PublisherCreateValidatorBeans {

    private final PublisherAlreadyExistsValidator publisherAlreadyExistsValidator;
    private final PublisherMandatoryFieldsValidator publisherMandatoryFieldsValidator;

    public PublisherCreateValidatorBeans(PublisherAlreadyExistsValidator publisherAlreadyExistsValidator,
                                         PublisherMandatoryFieldsValidator publisherMandatoryFieldsValidator) {
        this.publisherAlreadyExistsValidator = publisherAlreadyExistsValidator;
        this.publisherMandatoryFieldsValidator = publisherMandatoryFieldsValidator;
    }

    @Bean("publisherCreateValidator")
    public AbstractValidator<PublisherDto> abstractAuditPublisherValidator() {
        return AbstractValidator.link(
                publisherAlreadyExistsValidator,
                List.of(publisherMandatoryFieldsValidator)
        );
    }

}
