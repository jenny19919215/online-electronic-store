package com.jennyz.electronicstore.exception;

public class NotEnoughStockException extends RuntimeException {
    public NotEnoughStockException(String notEnoughtStockExist) {
        super(notEnoughtStockExist);
    }
}
