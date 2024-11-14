package com.autohome.app.cars.common;

import lombok.Data;

@Data
public class BaseModel<T> {
    private int returncode;
    private String message;
    private T result;

    public BaseModel() {
    }

    public BaseModel(T result) {
        this.returncode = 0;
        this.message = "success";
        this.result = result;
    }

    public BaseModel(int returncode, String message) {
        this.returncode = returncode;
        this.message = message;
    }
}
