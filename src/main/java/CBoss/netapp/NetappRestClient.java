package CBoss.netapp;

import CBoss.configClasses.NetappConfig;
import CBoss.jobService.JobService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;


public class NetappRestClient {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NetappRestClient.class);

    private final NetappConfig config;
    private final URI base_url;
    private final HttpClient client;


    public NetappRestClient(NetappConfig config) throws URISyntaxException {
        String protocol = "http";
        this.config = config;

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(this.config.getUser(), this.config.getPassword());
        provider.setCredentials(AuthScope.ANY, credentials);
        this.client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        URIBuilder builder = new URIBuilder().setScheme(protocol).setHost(this.config.getHost()).setPort(this.config.getPort()).setPath("/api");
        this.base_url = builder.build();

        log.info(String.format("Netapp Restful Client initialized. Connecting to: %s:%s via %s", this.config.getHost(), this.config.getPort(), this.config.getUser()));
    }

    public NetappConfig getConfig() {
        return this.config;
    }


    public APIResponse testConnection() throws IOException {
        String testUrl = this.base_url.resolve("/api/cluster").toString();

        HttpResponse response = this.client.execute(new HttpGet(testUrl));
        int statusCode = response.getStatusLine().getStatusCode();

        String json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        return new APIResponse(statusCode, json);
    }


    @Value("${netapp.file_clone.volume_uuid}")
    private String volume_uuid;

    public APIResponse file_clone(String source_path, String destination_path) throws Exception {

        String fileCloneUrl = this.base_url.resolve("api/storage/file/clone").toString();

        HttpPost httpPost = new HttpPost(fileCloneUrl);

        JsonObject volumeProperty = new JsonObject();
        volumeProperty.addProperty("uuid", volume_uuid);


        JsonObject body = new JsonObject();
        body.addProperty("source_path", source_path);
        body.add("volume", volumeProperty);
        body.addProperty("destination_path", destination_path);

        StringEntity entity = new StringEntity(body.toString());
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        HttpResponse response = client.execute(httpPost);
        int statusCode = response.getStatusLine().getStatusCode();


        String json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);


        return new APIResponse(statusCode, json);
    }


    public APIResponse get_job_status(String job_id) throws IOException {
        URI jobURI = this.base_url.resolve("/api/cluster/jobs/");
        String target_job_url = jobURI.resolve(job_id).toString();

        HttpResponse response = this.client.execute(new HttpGet(target_job_url));
        int statusCode = response.getStatusLine().getStatusCode();
        String json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        return new APIResponse(statusCode, json);
    }
}
