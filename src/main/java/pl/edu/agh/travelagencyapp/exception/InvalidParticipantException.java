package pl.edu.agh.travelagencyapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidParticipantException extends Exception{

    private static final long serialVersionUID = 3;

    public InvalidParticipantException(String message) {
        super(message);
    }
}