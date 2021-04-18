package CBoss.controllers;

import CBoss.utils.exception.CBossException;
import CBoss.workerQueue.WorkerThreadPoolTaskExecutor;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadPoolExecutor;

@RestController
@ControllerAdvice
@RequestMapping("api/workers")
public class WorkerServiceController {
    @Autowired
    private WorkerThreadPoolTaskExecutor workerThreadPoolTaskExecutor;


    @GetMapping(value = "status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getWorkerPoolStatus() {
        try {
            ThreadPoolExecutor executor = workerThreadPoolTaskExecutor.getThreadPoolExecutor();

            JsonObject response = new JsonObject();
            response.addProperty("task_count", executor.getTaskCount());
            response.addProperty("completed_tasks", executor.getCompletedTaskCount());
            response.addProperty("active_tasks", executor.getActiveCount());
            response.addProperty("queue_size", executor.getQueue().size());
            response.addProperty("pool_size", executor.getMaximumPoolSize());
            return new ResponseEntity<>(response.toString(), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            throw new CBossException(500, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
