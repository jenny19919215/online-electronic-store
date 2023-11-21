package com.jennyz.electronicstore.exception;

import com.jennyz.electronicstore.api.ErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomGlobalExceptionHandler.class);


    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorInfo> handleUnexpectedException(Exception exception) {
        LOGGER.error("Unexpected exception occurred", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorInfo(exception.getMessage()));
    }


    @ExceptionHandler({ProductAlreadyExist.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorInfo> springHandleNotFound(RuntimeException exception) {
        LOGGER.error("product already exist", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorInfo(exception.getMessage()));
    }


    @ExceptionHandler({ProductNotFoundException.class, BasketItemNotFoundException.class})
    public ResponseEntity<ErrorInfo> springHandleNotFound(ProductNotFoundException exception) {
        LOGGER.error("Item not found", exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorInfo(exception.getMessage()));
    }

    @ExceptionHandler(NotEnoughStockException.class)
    public ResponseEntity<ErrorInfo> springHandleNotFound(NotEnoughStockException exception) {
        LOGGER.error("Not enough Stock", exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorInfo(exception.getMessage()));
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorInfo> springHandleNotFound(ResponseStatusException exception) {
        LOGGER.error("response status exception ", exception);
        return ResponseEntity.status(exception.getStatus()).body(new ErrorInfo(exception.getMessage()));
    }



}