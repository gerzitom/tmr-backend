package cz.cvut.fel.tmr.rest;

import cz.cvut.fel.tmr.exception.*;
import cz.cvut.fel.tmr.response.ErrorInfo;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorInfo> notFoundExeption(HttpServletRequest request, EarException e){
        ErrorInfo error = new ErrorInfo(e.getMessage());
        return new ResponseEntity<ErrorInfo>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorInfo> alreadyExistsExeption(HttpServletRequest request, EarException e){
        ErrorInfo error = new ErrorInfo(e.getMessage());
        return new ResponseEntity<ErrorInfo>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorInfo> validationExeption(HttpServletRequest request, EarException e){
        ErrorInfo error = new ErrorInfo(e.getMessage());
        return new ResponseEntity<ErrorInfo>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorInfo> authenticationException(HttpServletRequest request, EarException e){
        ErrorInfo error = new ErrorInfo(e.getMessage());
        return new ResponseEntity<ErrorInfo>(error, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(EarException.class)
    public ResponseEntity<ErrorInfo> earExeption(HttpServletRequest request, EarException e){
        ErrorInfo error = new ErrorInfo(e.getMessage());
        return new ResponseEntity<ErrorInfo>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorInfo> expiredJwt(HttpServletRequest request, Exception e){
        ErrorInfo error = new ErrorInfo(e.getMessage());
        return new ResponseEntity<ErrorInfo>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorInfo> accessDenied(HttpServletRequest request, Exception e){
        ErrorInfo error = new ErrorInfo(e.getMessage());
        return new ResponseEntity<ErrorInfo>(error, HttpStatus.FORBIDDEN);
    }
}
