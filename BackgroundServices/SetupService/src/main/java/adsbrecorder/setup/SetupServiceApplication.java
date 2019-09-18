package adsbrecorder.setup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "adsbrecorder.setup",
        "adsbrecorder.user.service"})
@EntityScan(basePackages = {
        "adsbrecorder.user.entity"})
@EnableJpaRepositories(basePackages = {
        "adsbrecorder.user.repo"})
@PropertySource(value = {
        "application.properties",
        "mariadb.properties",
        "userauth.properties",
        "misc.properties"})
public class SetupServiceApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(SetupServiceApplication.class, args);
    }
}
