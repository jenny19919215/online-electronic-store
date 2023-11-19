package com.jennyz.electronicstore.exception;

import net.bytebuddy.implementation.bind.annotation.Super;

public class BasketItemNotFoundException extends RuntimeException {
    public BasketItemNotFoundException(String basketItemNotFound) {
        super(basketItemNotFound);
    }
}
