package net.purevirtual.springbootexample.repo;

public class NotFoundException extends RuntimeException {

    public NotFoundException(long id) {
        super("Application " + id + "doesn't exist");
    }

}
