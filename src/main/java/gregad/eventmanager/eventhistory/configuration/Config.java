package gregad.eventmanager.eventhistory.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * @author Greg Adler
 */
@Configuration
public class Config {
    @Value("${security.service.url}")
    private String securityServiceUrl;
    @Value("${rest.template.timeout}")
    int timeout;


    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(timeout))
                .setReadTimeout(Duration.ofMillis(timeout))
                .build();
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    //    @Bean
//    public FilterRegistrationBean<JwtTokenValidatorFilter> filterRegistrationBean() {
//        FilterRegistrationBean < JwtTokenValidatorFilter > registrationBean = new FilterRegistrationBean();
//        JwtTokenValidatorFilter filter=new JwtTokenValidatorFilter(securityServiceUrl,timeout,restTemplate(restTemplateBuilder()));
//
//        registrationBean.setFilter(filter);
//        return registrationBean;
//    }
}
