package CBoss.scheduler;

import CBoss.jobService.CBossJob;
import CBoss.jobService.CBossJobState;
import CBoss.jobService.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JobCleanupWorker {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JobCleanupWorker.class);


    @Autowired
    JobService jobService;

    private final long job_life_ms;

    public JobCleanupWorker(@Value("${service.job.life_ms}") long job_life_ms) {
        log.info(String.format("Job Life(ms) set to: %s", job_life_ms));
        this.job_life_ms = job_life_ms;
    }


    @Async("taskScheduler")
    @Scheduled(fixedRate = 5000)
    public void jobStateUpdater() {
        Map<String, CBossJob> jobs = jobService.getJobRecords();

        int counter = 0;
        for (String job_id : jobs.keySet()) {
            CBossJob job = jobs.get(job_id);
            if (job.getJob_future().isDone() && job.getState() != CBossJobState.COMPLETED && job.getState() != CBossJobState.WAIT_FOR_DELETE) {
                job.updateJobState(CBossJobState.COMPLETED);
                counter++;
            } else if (job.getJob_future().isDone() && job.getState() == CBossJobState.COMPLETED) {
                job.updateJobState(CBossJobState.WAIT_FOR_DELETE);
            }


        }

        log.debug(String.format("State update done. [%s] Jobs scanned, [%s] jobs updated.", jobs.size(), counter));

    }

    @Async("taskScheduler")
    @Scheduled(fixedRate = 10000)
    public void staleJobCleaner() {
        Map<String, CBossJob> jobs = jobService.getJobRecords();


        int scanned = jobs.size();

        int counter = 0;
        for (String job_id : jobs.keySet()) {
            CBossJob job = jobs.get(job_id);
            if (job.getState() == CBossJobState.WAIT_FOR_DELETE) {

                long last = job.getLast_updated_time();
                long delta = System.currentTimeMillis() - last;
                job.setLife_remains(this.job_life_ms - delta);

                if (delta >= this.job_life_ms) {
                    jobs.remove(job_id);
                    log.info(String.format("[%s] Job: %s got clear from job map.", job.getJobType().toString(), job_id));
                    counter++;
                }

            }
        }

        log.info(String.format("House keeping done. [%s] Jobs scanned, [%s] jobs cleared.", scanned, counter));

    }

}


