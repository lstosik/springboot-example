package net.purevirtual.springbootexample.repo;

public class NotFoundException extends RuntimeException {

    public NotFoundException(long id) {
        super("Application with id=" + id + " doesn't exist");
    }

}
