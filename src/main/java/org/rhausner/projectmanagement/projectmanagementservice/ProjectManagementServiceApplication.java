package org.rhausner.projectmanagement.projectmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application entry point for the Project Management Service.
 */
@SpringBootApplication
public class ProjectManagementServiceApplication {

    /**
     * Application entry point used by the JVM to start the Spring Boot application.
     *
     * @param args standard command-line arguments forwarded to SpringApplication
     */
    public static void main(String[] args) {
        SpringApplication.run(ProjectManagementServiceApplication.class, args);
    }

}
