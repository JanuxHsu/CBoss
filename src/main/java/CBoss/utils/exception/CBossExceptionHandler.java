package CBoss.utils.exception;

import CBoss.controllers.FileServiceController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CBossExceptionHandler extends ResponseEntityExceptionHandler {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileServiceController.class);


    @ExceptionHandler({CBossException.class})
    public final ResponseEntity<CBossExceptionResponse> handleDemoException(CBossException ex) {
        log.error(ex.getErrorMessage());
        CBossExceptionResponse response = new CBossExceptionResponse(ex.getErrorCode(), ex.getErrorMessage());
        return new ResponseEntity<>(response, ex.getResStatus());
    }
}