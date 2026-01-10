package org.rhausner.projectmanagement.projectmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ProjectManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectManagementServiceApplication.class, args);
    }

}
