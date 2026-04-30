package com.masantello.bookstoremanager.validation;

import com.masantello.bookstoremanager.dtos.AuthorDto;
import com.masantello.bookstoremanager.exceptions.MissingMandatoryFieldsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NullMandatoryFieldsValidator extends AbstractAuthorValidator<AuthorDto> {

    private static final Logger logger = LoggerFactory.getLogger(NullMandatoryFieldsValidator.class);
    private static final String MISSING_MANDATORY_FIELDS = "Campos obrigatorios ausentes";

    @Override
    public AuthorDto validate(AuthorDto authorDto) {
        logger.trace("Validating mandatory fields in author's creating request");
        if (authorDto.getName() == null || authorDto.getLiteraryGenre() == null) {
            throw new MissingMandatoryFieldsException(MISSING_MANDATORY_FIELDS);
        }

        return validateNext(authorDto);
    }
}
