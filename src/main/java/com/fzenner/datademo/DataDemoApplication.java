package com.fzenner.datademo;

import com.kewebsi.lab.CustomRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;



@SpringBootApplication
// @RestController
@ServletComponentScan(basePackages = {"com.fzenner.datademo.web.servlets"})  // Required for detecting servlets.
@ComponentScan(basePackages = {"com.kewebsi", "com.fzenner.datademo.controller", "com.fzenner.datademo.service", "com.kewebsi.service", "com.fzenner.datademo"})
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)
public class DataDemoApplication {


	
	static final Logger LOG = LoggerFactory.getLogger(DataDemoApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(DataDemoApplication.class, args);
	}

}
