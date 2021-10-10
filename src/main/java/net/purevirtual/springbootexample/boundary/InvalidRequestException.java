package net.purevirtual.springbootexample.boundary;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRequestException() {
    }

}
