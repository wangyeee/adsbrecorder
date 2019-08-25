package adsbrecorder.receiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan(basePackages = {
        "adsbrecorder.receiver",
        "adsbrecorder.receiver.controller",
        "adsbrecorder.receiver.service",
        "adsbrecorder.client.service",
        "adsbrecorder.user.service",
        "adsbrecorder.receiver.security"})
@EntityScan(basePackages = {
        "adsbrecorder.receiver.entity"})
@EnableMongoRepositories(basePackages = {
        "adsbrecorder.receiver.repo"})
@EnableJpaRepositories(basePackages = {
        "adsbrecorder.client.repo",
        "adsbrecorder.user.repo"})
@PropertySource(value = {
        "application.properties",
        "kafka.properties",
        "mariadb.properties",
        "mongodb.properties",
        "userauth.properties",
        "clientauth.properties",
        "misc.properties"})
public class ReceiverServiceApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ReceiverServiceApplication.class, args);
    }
}
