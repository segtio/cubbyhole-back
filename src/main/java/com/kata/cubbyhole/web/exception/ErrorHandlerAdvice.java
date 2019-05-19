package com.kata.cubbyhole.web.exception;

import com.kata.cubbyhole.web.payload.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandlerAdvice {

    @ExceptionHandler(InternalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleInternalExpection(Exception e) {
        return buildResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleBadRequestException(Exception e) {
        return buildResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleResourceNotFoundException(Exception e) {
        return buildResponse(e, HttpStatus.NOT_FOUND);
    }

    private ErrorResponse buildResponse(Exception e, HttpStatus status) {
        String message = e.getMessage();
        if (e instanceof MethodArgumentNotValidException) {
            message = buildMethodArgumentNotValidExceptionMessage((MethodArgumentNotValidException) e);
        }
        return new ErrorResponse(message, status.getReasonPhrase());
    }

    private String buildMethodArgumentNotValidExceptionMessage(MethodArgumentNotValidException e) {

        StringBuilder sb = new StringBuilder();

        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            sb.append(error.getDefaultMessage()).append(" ");
        }

        return sb.toString();
    }
}
