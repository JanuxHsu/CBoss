package CBoss.jobService;

import CBoss.configClasses.CBossJobConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class CBossJob {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CBossJob.class);

    @Autowired
    JobService jobService;

    final private CBossJobType jobType;
    final private CBossJobConfig config;
    final private String job_id;
    private JsonElement progress;

    private long last_updated_time = System.currentTimeMillis();
    private long life_remains = -1;

    private CBossJobState state = CBossJobState.INIT;
    private CompletableFuture<CBossJobResult> job_future;

    public CBossJob(CBossJobType jobType, CBossJobConfig config) {
        this.jobType = jobType;
        this.config = config;
        this.job_id = UUID.randomUUID().toString();
    }

    public void setJobFuture(CompletableFuture<CBossJobResult> job_future) {
        this.job_future = job_future;
    }

    public String getJob_id() {
        return this.job_id;
    }


    public CompletableFuture<CBossJobResult> getJob_future() {
        return this.job_future;
    }

    public CBossJobConfig getJobConfig() {
        return this.config;
    }


    public void updateProgress(JsonObject progress) {
        this.updateJobState(CBossJobState.RUNNING);
        this.last_updated_time = System.currentTimeMillis();
        this.progress = progress;
    }

    public JsonElement getProgress() {
        return this.progress;
    }

    public void updateJobState(CBossJobState state) {
        this.last_updated_time = System.currentTimeMillis();
        this.state = state;
    }

    public void setLife_remains(long val) {
        this.life_remains = val;
    }

    public long getLife_remains() {
        return this.life_remains;
    }

    public long getLast_updated_time() {
        return this.last_updated_time;
    }

    public CBossJobState getState() {
        return this.state;
    }

    public CBossJobType getJobType() {
        return this.jobType;
    }


    public JsonObject getJobJson() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JsonObject jobJson = new JsonObject();
        jobJson.addProperty("job_id", this.job_id);
        jobJson.addProperty("job_type", this.jobType.toString());


        jobJson.addProperty("running", !this.job_future.isDone());

        if (this.job_future.isDone()) {
            if (this.job_future.get().getJsonMessage() != null) {
                jobJson.add("return", this.job_future.get().getJsonMessage());

            } else {
                jobJson.addProperty("return", this.job_future.get().getMessage());
            }
        }
        jobJson.add("progress", this.progress);
        jobJson.addProperty("state", this.state.toString());
        jobJson.addProperty("last_updated", simpleDateFormat.format(new Date(this.last_updated_time)));
        jobJson.addProperty("life_remains_ms", this.life_remains);

        return jobJson;

    }

}
