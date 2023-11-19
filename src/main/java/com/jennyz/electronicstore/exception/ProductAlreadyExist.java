package com.jennyz.electronicstore.exception;

import javax.validation.constraints.NotBlank;

public class ProductAlreadyExist extends RuntimeException {
    public ProductAlreadyExist(String message) {
        super(message);
    }
}
