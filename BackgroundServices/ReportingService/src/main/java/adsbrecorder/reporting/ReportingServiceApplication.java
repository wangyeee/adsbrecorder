package adsbrecorder.reporting;

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
        "adsbrecorder.reporting",
        "adsbrecorder.common.aop",
        "adsbrecorder.reporting.controller",
        "adsbrecorder.reporting.service",
        "adsbrecorder.receiver.service",
        "adsbrecorder.receiver.repo",
        "adsbrecorder.user.service",
        "adsbrecorder.reporting.security"})
@EntityScan(basePackages = {
        "adsbrecorder.receiver.entity"})
@EnableMongoRepositories(basePackages = {
        "adsbrecorder.reporting.repo",
        "adsbrecorder.receiver.repo"})
@EnableJpaRepositories(basePackages = {
        "adsbrecorder.client.repo",
        "adsbrecorder.user.repo"})
@PropertySource(value = {
        "application.properties",
        "mariadb.properties",
        "mongodb.properties",
        "userauth.properties",
        "misc.properties"})
public class ReportingServiceApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ReportingServiceApplication.class, args);
    }
}
