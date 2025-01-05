package com.github.pavlidise.acmebooking.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleBookingNotFoundException(BookingNotFoundException bookingNotFoundException) {
        log.warn("Handling BookingNotFoundException");
        return bookingNotFoundException.getMessage();
    }


    @ExceptionHandler(OverlappingBookingException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleOverlappingBookingException(OverlappingBookingException overlappingBookingException) {
        log.warn("Handling OverlappingBookingException");
        return overlappingBookingException.getMessage();
    }

    @ExceptionHandler(RoomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleRoomNotFoundException(RoomNotFoundException roomNotFoundException) {
        log.warn("Handling OverlappingBookingException");
        return roomNotFoundException.getMessage();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFoundException(UserNotFoundException userNotFoundException) {
        log.warn("Handling UserNotFoundException");
        return userNotFoundException.getMessage();
    }

    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDateTimeParseException(DateTimeParseException dateTimeParseException) {
        log.error("Handling DateTimeParseException with: {}", dateTimeParseException.getMessage());
        return dateTimeParseException.getMessage();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
        log.error("Handling MethodArgumentTypeMismatchException with: {}", methodArgumentTypeMismatchException.getMessage());
        return methodArgumentTypeMismatchException.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        log.warn("Handling MethodArgumentNotValidException");
        Map<String, String> errors = new HashMap<>();
        methodArgumentNotValidException.getBindingResult().getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });
        return errors;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String catchAll(Exception exception) {
        log.error("Handling Exception: ", exception);
        return "An unexpected error occurred";
    }
}
