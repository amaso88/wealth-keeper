package com.desolitech.summary.infrastructure.configurations;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name="scheduling-jobs.enabled", havingValue = "true")
public class SchedulingConfiguration {
}
