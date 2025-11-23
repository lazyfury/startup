package io.sf.admin;

import io.swagger.v3.oas.annotations.headers.Header;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"io.sf.admin", "io.sf.modules", "io.sf.config", "io.sf.utils", "io.sf.third"})
@MapperScan("io.sf.modules.*.mapper")
@EnableJpaRepositories(basePackages = {"io.sf.modules"})
@EntityScan(basePackages = {"io.sf.modules"})
public class RunAdminApplication {

    public static void main(String[] args) {
        var applicationContext = SpringApplication.run(RunAdminApplication.class, args);
        Environment env = applicationContext.getEnvironment();
        var serverPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        log.info("StartupApplication started on http://localhost:{}", serverPort);
    }


}
