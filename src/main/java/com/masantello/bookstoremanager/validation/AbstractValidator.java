package com.masantello.bookstoremanager.validation;

import java.util.List;

//Chain of Responsibilities
public abstract class AbstractValidator<T> {

    public AbstractValidator<T> next;

    public static <T> AbstractValidator<T> link(AbstractValidator<T> first,
                                                List<AbstractValidator<T>> chain) {
        var head = first;
        for (var nextPresentInChain : chain) {
            head.next = nextPresentInChain;
            head = nextPresentInChain;
        }

        return first;
    }

    public abstract T validate(T validationObject);

    public T validateNext(T validationObject) {
        if (next == null || validationObject == null) {
            return validationObject;
        }

        return next.validate(validationObject);
    }
}
