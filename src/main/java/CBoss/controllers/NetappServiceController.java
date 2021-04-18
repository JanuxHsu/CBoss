package CBoss.controllers;

import CBoss.netapp.NetappRestService;
import CBoss.utils.exception.CBossException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
@RequestMapping("api/netapp_service")
public class NetappServiceController {

    @Autowired
    NetappRestService netappRestService;

    @GetMapping(value = "test", produces = MediaType.APPLICATION_JSON_VALUE)
    public String testConnection() {
        try {
            return netappRestService.test_connection();
        } catch (Exception e) {
            throw new CBossException(500, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
