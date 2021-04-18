package CBoss.workerQueue;

import CBoss.configClasses.CBossDeleteJobConfig;
import CBoss.configClasses.CBossJobConfig;
import CBoss.fileService.v1.FileServiceUtils;
import CBoss.jobService.CBossJob;
import CBoss.jobService.CBossJobResult;
import CBoss.jobService.JobService;
import CBoss.netapp.APIResponse;
import CBoss.netapp.NetappRestService;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class WorkerQueueService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JobService.class);

    @Autowired
    JobService jobService;

    @Autowired
    NetappRestService netappRestService;

    @Autowired
    FileServiceUtils fileServiceUtils;

    @Async
    public CompletableFuture<CBossJobResult> test_job(CBossJob job) throws InterruptedException {
        String thread_name = Thread.currentThread().getName();

        String job_id = job.getJob_id();

        int counter = 0;
        while (counter < 30) {
            counter++;

            JsonObject progress = new JsonObject();
            progress.addProperty("counter", counter);

            jobService.update(job_id, progress);
            log.info(thread_name + " - counter: " + counter);
            Thread.sleep(1000);
        }

        CBossJobResult result = new CBossJobResult();
        result.setMessage("Done");

        return CompletableFuture.completedFuture(result);
    }

    @Async
    public CompletableFuture<CBossJobResult> clone_file_job(CBossJob job) throws Exception {
        String thread_name = Thread.currentThread().getName();

        CBossJobConfig config = job.getJobConfig();
        String job_id = job.getJob_id();

        APIResponse clone_api_response = netappRestService.do_file_clone(config.getSource_path(), config.getDestination_path());

        jobService.update(job_id, clone_api_response.getContent_json().getAsJsonObject());


        JsonObject jobJson = clone_api_response.getContent_json().getAsJsonObject();
        String clone_job_id = jobJson.get("job").getAsJsonObject().get("uuid").getAsString();
        APIResponse res = netappRestService.wait_till_ONTAP_job_end(job_id, clone_job_id);


        CBossJobResult result = new CBossJobResult();
        result.setJsonMessage(res.getContent_json());

        return CompletableFuture.completedFuture(result);
    }

    @Async
    public CompletableFuture<CBossJobResult> delete_all_files(CBossJob job) {
        try {
            CBossDeleteJobConfig config = (CBossDeleteJobConfig) job.getJobConfig();

            int max_delete_level = config.getMax_delete_level();

            List<Path> toDeleteFiles = fileServiceUtils.getFileFlatTree(config.getStartPath(), 0, max_delete_level);

            JsonObject updateJson = new JsonObject();
            updateJson.addProperty("message", String.format("To delete total : %s files and directories.", toDeleteFiles.size()));
            jobService.update(job.getJob_id(), updateJson);

            FileUtils.deleteDirectory(new File(config.getStartPath()));

        } catch (Exception e) {
            log.error(e.getMessage());
            CBossJobResult result = new CBossJobResult();
            result.setMessage(e.getMessage());

            return CompletableFuture.completedFuture(result);
        }


        CBossJobResult result = new CBossJobResult();
        result.setMessage("done");
        return CompletableFuture.completedFuture(result);
    }
}
