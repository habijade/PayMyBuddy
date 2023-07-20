package com.example.demo.exeption;

public class ConnectionServiceException extends RuntimeException{
    public ConnectionServiceException(String message){
        super(message);
    }
}
