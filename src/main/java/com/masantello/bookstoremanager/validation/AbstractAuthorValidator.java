package com.masantello.bookstoremanager.validation;

import java.util.List;

public abstract class AbstractAuthorValidator<T> {

    public AbstractAuthorValidator<T> next;

    public static <T> AbstractAuthorValidator<T> link(AbstractAuthorValidator<T> first,
                                                      List<AbstractAuthorValidator<T>> chain) {
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
