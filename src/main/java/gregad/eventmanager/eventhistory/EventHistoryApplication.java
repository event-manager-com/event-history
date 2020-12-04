package gregad.eventmanager.eventhistory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EventHistoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventHistoryApplication.class, args);
    }

}
