package CBoss.netapp;

import CBoss.jobService.JobService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class NetappRestService {

    Gson gson = new Gson();

    @Autowired
    NetappRestClient netappRestClient;


    @Autowired
    JobService jobService;


    public String test_connection() throws IOException {
        return netappRestClient.testConnection().to_json();
    }


    public APIResponse do_file_clone(String source_path, String destination_path) throws Exception {
        return netappRestClient.file_clone(source_path, destination_path);
    }


    public APIResponse wait_till_ONTAP_job_end(String cboss_job_id, String ontap_job_id) throws Exception {

        boolean run = true;
        while (run) {
            Thread.sleep(2000);
            JsonObject ontap_job_json = (JsonObject) netappRestClient.get_job_status(ontap_job_id).getContent_json();

            String end_time = ontap_job_json.get("end_time").getAsString();
            if (end_time != null && !end_time.equals("")) {
                return new APIResponse(200, ontap_job_json.toString());
            }

            jobService.update(cboss_job_id, ontap_job_json);


            run = false;
        }
        return new APIResponse(500, "[556] Unexpected Error.");

    }

}
