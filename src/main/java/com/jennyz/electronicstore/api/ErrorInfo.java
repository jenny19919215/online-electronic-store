package com.jennyz.electronicstore.api;

import lombok.ToString;
import lombok.Value;

@Value
@ToString
public class ErrorInfo {

    private final String reason;

}
