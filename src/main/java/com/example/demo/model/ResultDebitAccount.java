package com.example.demo.model;

import lombok.Data;

@Data
public class ResultDebitAccount {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    private String message;
    private boolean result;
}
