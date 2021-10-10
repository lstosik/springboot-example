package net.purevirtual.springbootexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("net.purevirtual.springbootexample.repo") 
@EntityScan("net.purevirtual.springbootexample.entity")
@SpringBootApplication
public class BootApp {

    public static void main(String[] args) {
        SpringApplication.run(BootApp.class, args);
    }
}
