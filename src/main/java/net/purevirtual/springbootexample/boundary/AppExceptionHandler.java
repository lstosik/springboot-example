package net.purevirtual.springbootexample.boundary;

import net.purevirtual.springbootexample.repo.NotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(AppExceptionHandler.class);

    @ExceptionHandler({InvalidRequestException.class, InvalidStatusException.class,
        ConstraintViolationException.class,
        DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleBadRequest(Exception ex, WebRequest request) {
        logger.warn("Received invalid request", ex);
        return handleExceptionInternal(ex, ex.getMessage(),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
    
    @ExceptionHandler({NotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(),
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
    
}
