package net.purevirtual.springbootexample.boundary;

import java.util.Arrays;
import net.purevirtual.springbootexample.entity.Application;
import net.purevirtual.springbootexample.entity.ApplicationStatus;

public class InvalidStatusException extends RuntimeException {

    public InvalidStatusException(String message) {
        super(message);
    }

    public InvalidStatusException(Application application, ApplicationStatus... expected) {

        super(String.format("Application %d has invalid status %s, expected one of: %s",
                application.getId(), application.getStatus(), Arrays.deepToString(expected)));
    }

}
