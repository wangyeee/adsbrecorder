package adsbrecorder.user;

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
@EnableJpaRepositories(basePackages = "adsbrecorder.user.repo")
@ComponentScan(basePackages = {
        "adsbrecorder.user",
        "adsbrecorder.user.controller",
        "adsbrecorder.user.service",
        "adsbrecorder.user.security"})
@EntityScan(basePackages = {
        "adsbrecorder.client.entity",
        "adsbrecorder.user.entity"})
@PropertySource(value = {
        "application.properties",
        "mariadb.properties",
        "userauth.properties",
        "misc.properties"})
public class UserServiceApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
