package pl.edu.agh.travelagencyapp.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidUserException extends Exception{
    private static final long serialVersionUID = 5;

    public InvalidUserException(String message) {
        super(message);
    }
}
