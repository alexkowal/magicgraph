package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("controller")
@ComponentScan("utils")
@ComponentScan("starter")
@ComponentScan("domain")
public class Main {
    public static void main(String[] args) throws Exception {
            SpringApplication.run(Main.class, args);
    }
}
