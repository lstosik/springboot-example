package net.purevirtual.springbootexample.boundary;

import net.purevirtual.springbootexample.entity.Application;
import net.purevirtual.springbootexample.entity.ApplicationStatus;

public class InvalidStatusException extends RuntimeException {

    public InvalidStatusException(String message) {
        super(message);
    }

    public InvalidStatusException(Application application, ApplicationStatus... expected) {
        super("Application " + application.getId() + " has invalid status " + application.getStatus() + ", expected one of: " + expected);
    }

}
