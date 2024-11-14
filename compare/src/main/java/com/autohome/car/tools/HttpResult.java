package com.autohome.car.tools;

import lombok.Data;

@Data
public class HttpResult<T> {
    int statusCode;
    String message;
    T result;
}
