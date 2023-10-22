package com.example.sentisumassignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
public class SentisumAssignmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(SentisumAssignmentApplication.class, args);
	}

}
