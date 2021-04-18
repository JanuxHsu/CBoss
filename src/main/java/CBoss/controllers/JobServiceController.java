package CBoss.controllers;

import CBoss.jobService.CBossJob;
import CBoss.jobService.CBossJobType;
import CBoss.jobService.JobService;
import CBoss.utils.exception.CBossException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@ControllerAdvice
@RequestMapping("api/job")
public class JobServiceController {

    @Autowired
    JobService jobService;


    @GetMapping(value = {"list/{job_id}", "list"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getWorkerPoolStatus(@PathVariable(required = false) String job_id) {
        try {
            JsonArray response = new JsonArray();
            Map<String, CBossJob> jobMap = jobService.getJobRecords();

            if (job_id == null) {
                for (String key : jobMap.keySet()) {
                    CBossJob job = jobMap.get(key);
                    response.add(job.getJobJson());
                }
                return new ResponseEntity<>(response.toString(), HttpStatus.OK);
            } else {
                for (String key : jobMap.keySet()) {
                    CBossJob job = jobMap.get(key);
                    if (job.getJob_id().equals(job_id)) {
                        response.add(job.getJobJson());
                        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
                    }
                }
                throw new CBossException(400, String.format("Job_id : %s not found.", job_id), HttpStatus.BAD_REQUEST);

            }

        } catch (CBossException e) {
            throw e;
        } catch (Exception e) {
            throw new CBossException(500, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping(value = "test", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> asyncTest() {

        try {
            CBossJob testJob = new CBossJob(CBossJobType.TEST, null);
            JsonObject response = new JsonObject();
            String job_id = jobService.addNewJob(testJob);
            response.addProperty("job_id", job_id);
            return new ResponseEntity<>(response.toString(), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            throw new CBossException(500, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }


}
