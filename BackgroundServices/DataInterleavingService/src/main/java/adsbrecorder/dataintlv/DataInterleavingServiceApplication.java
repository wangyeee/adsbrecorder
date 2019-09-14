package adsbrecorder.dataintlv;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAutoConfiguration
@EnableScheduling
@SpringBootApplication
@EnableMongoRepositories(basePackages = {
        "adsbrecorder.receiver.repo"})
@ComponentScan(basePackages = {
        "adsbrecorder.dataintlv",
        "adsbrecorder.dataintlv.task"})
@PropertySource(value = {
        "application.properties",
        "kafka.properties",
        "mongodb.properties",
        "misc.properties"})
public class DataInterleavingServiceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DataInterleavingServiceApplication.class, args);
    }

    public void run(String... args) throws Exception {
        // ignored
    }
}
