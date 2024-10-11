package com.desolitech.tag;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.TimeZone;

@EnableAsync
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TagServiceApplication {

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		Logger logger = LoggerFactory.getLogger(TagServiceApplication.class);

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("AppAsyncThread-");
		executor.setRejectedExecutionHandler((r, executor1) -> logger.warn("Task rejected, thread pool is full and queue is also full"));
		executor.initialize();
		return executor;
	}

	public static void main(String[] args) {
		SpringApplication.run(TagServiceApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// Setting Spring Boot SetTimeZone
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

}
