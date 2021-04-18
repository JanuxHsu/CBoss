package CBoss.utils.exception;

import org.springframework.http.HttpStatus;

public class CBossException extends RuntimeException {

    private final Integer errorCode;
    private final String errorMessage;
    private final HttpStatus resStatus;

    public CBossException(int errorCode, String errorMessage, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.resStatus = httpStatus;
    }

    public HttpStatus getResStatus() {
        return this.resStatus;
    }

    public Integer getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    // getters and setters
}