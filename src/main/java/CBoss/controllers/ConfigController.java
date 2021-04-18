package CBoss.controllers;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
@RequestMapping("api")
public class ConfigController {


    @Value("${netapp.host}")
    private String netapp_host;
    @Value("${netapp.port}")
    private String netapp_port;
    @Value("${netapp.user}")
    private String netapp_user;
    @Value("${netapp.file_clone.volume_uuid}")
    private String attached_netapp_volume_uuid;

    @Value("${service.worker.max_thread}")
    private String max_thread;
    @Value("${service.worker.max_queue_size}")
    private String max_queue_size;
    @Value("${service.job.life_ms}")
    private String jobLifeMs;


    @GetMapping(value = "config", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getConfig() {

        JsonObject configJson = new JsonObject();
        JsonObject netappSettings = new JsonObject();
        netappSettings.addProperty("host", netapp_host);
        netappSettings.addProperty("port", netapp_port);
        netappSettings.addProperty("user", netapp_user);
        netappSettings.addProperty("working_volume_uuid", attached_netapp_volume_uuid);

        JsonObject threadpoolSettings = new JsonObject();
        threadpoolSettings.addProperty("max_threads", max_thread);
        threadpoolSettings.addProperty("max_queue_size", max_queue_size);
        threadpoolSettings.addProperty("job_life_ms", jobLifeMs);


        configJson.add("worker_setting", threadpoolSettings);
        configJson.add("netapp_setting", netappSettings);


        return new ResponseEntity<>(configJson.toString(), HttpStatus.OK);
    }

}
