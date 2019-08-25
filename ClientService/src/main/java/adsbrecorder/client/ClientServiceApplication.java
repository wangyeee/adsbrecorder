package adsbrecorder.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@EnableJpaRepositories(basePackages = {
        "adsbrecorder.client.repo",
        "adsbrecorder.user.repo"})
@ComponentScan(basePackages = {
        "adsbrecorder.client",
        "adsbrecorder.client.service",
        "adsbrecorder.common.aop",
        "adsbrecorder.user.service",
        "adsbrecorder.client.controller",
        "adsbrecorder.client.auth"})
@EntityScan(basePackages = {
        "adsbrecorder.client.entity",
        "adsbrecorder.user.entity"})
@PropertySource(value = {
        "application.properties",
        "mariadb.properties",
        "userauth.properties",
        "clientauth.properties",
        "misc.properties"})
public class ClientServiceApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ClientServiceApplication.class, args);
    }
}
