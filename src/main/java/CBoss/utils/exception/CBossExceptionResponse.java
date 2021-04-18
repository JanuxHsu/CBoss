package CBoss.utils.exception;

public class CBossExceptionResponse {

    private final Integer code;
    private final String message;

    public CBossExceptionResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }


}