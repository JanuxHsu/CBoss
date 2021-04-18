package CBoss.applicationConfig;

import CBoss.configClasses.NetappConfig;
import CBoss.netapp.NetappRestClient;
import CBoss.workerQueue.WorkerThreadPoolTaskExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ApplicationConfigs {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ApplicationConfigs.class);


    @Bean
    public WorkerThreadPoolTaskExecutor taskExecutor(
            @Value("${service.worker.max_thread}") int pool_size,
            @Value("${service.worker.max_queue_size}") int max_queue_size) {
        log.info(String.format("Init ThreadPool, Thread pool size set to : %s", pool_size));
        WorkerThreadPoolTaskExecutor executor = new WorkerThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(pool_size);
        executor.setQueueCapacity(max_queue_size);
        executor.setKeepAliveSeconds(180);
        executor.setThreadNamePrefix("service-worker");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();

        return executor;
    }


    @Bean
    public NetappRestClient netappRestClient(@Value("${netapp.host}") String host,
                                             @Value("${netapp.port:80}") int port,
                                             @Value("${netapp.user}") String user,
                                             @Value("${netapp.env_password}") String env_password) throws URISyntaxException {
        NetappConfig config = new NetappConfig(host, port, user, env_password);
        return new NetappRestClient(config);
    }


}



