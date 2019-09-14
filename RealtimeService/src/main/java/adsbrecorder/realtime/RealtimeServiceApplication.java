package adsbrecorder.realtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan(basePackages = {
        "adsbrecorder.realtime",
        "adsbrecorder.realtime.controller",
        "adsbrecorder.realtime.security"})
@PropertySource(value = {
        "application.properties",
        "kafka.properties",
        "misc.properties"})
public class RealtimeServiceApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(RealtimeServiceApplication.class, args);
    }
}
