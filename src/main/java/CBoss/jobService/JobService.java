package CBoss.jobService;

import CBoss.configClasses.CBossJobConfig;
import CBoss.utils.exception.CBossException;
import CBoss.workerQueue.WorkerQueueService;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JobService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JobService.class);


    @Autowired
    WorkerQueueService workerQueueService;

    private final ConcurrentHashMap<String, CBossJob> JobRecordMap = new ConcurrentHashMap<>();

    public Map<String, CBossJob> getJobRecords() {
        return this.JobRecordMap;
    }


    public Boolean updateJobStatus(String job_id) {
        return null;
    }

    public void update(String job_id, JsonObject progress) {
        CBossJob job = this.JobRecordMap.get(job_id);
        job.updateProgress(progress);

    }

    public String addNewJob(CBossJob job) throws Exception {
        String job_id = job.getJob_id();

        switch (job.getJobType()) {
            case TEST:
                job.setJobFuture(workerQueueService.test_job(job));
                break;
            case CLONE:
                job.setJobFuture(workerQueueService.clone_file_job(job));
                break;
            case DELETE:
                job.setJobFuture(workerQueueService.delete_all_files(job));
                break;
            default:
                throw new CBossException(400, "Unknown Job Type, Please check.", HttpStatus.BAD_REQUEST);
        }
        this.JobRecordMap.put(job_id, job);

        log.info(String.format("[%s] Job submitted, id: %s", job.getJobType().toString(), job_id));
        return job_id;
    }


}


