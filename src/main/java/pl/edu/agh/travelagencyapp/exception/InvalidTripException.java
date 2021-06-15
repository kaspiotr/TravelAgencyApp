package pl.edu.agh.travelagencyapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidTripException extends Exception {

    private static final long serialVersionUID = 4;

    public InvalidTripException(String message) {
        super(message);
    }
}

