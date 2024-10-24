package com.desolitech.summary.infrastructure.crons;

import an.awesome.pipelinr.Pipeline;

import com.desolitech.summary.domian.constants.LogType;
import com.desolitech.summary.domian.services.systemLog.LogService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationReviewCron {

    private final Pipeline mediator;
    private final LogService logService;

    public ConfigurationReviewCron(Pipeline mediator, LogService logService) {
        this.mediator = mediator;
        this.logService = logService;
    }

    @Scheduled(cron = "0/30 * * * * *")//Every 20 seconds
    public void executeCronJobs() {
        logService.add(LogType.INFO, "ExecuteCronJobs cron job started.");

        /*mediator.send(new GeneratePdfCommand("d3b8a9b0-9e4f-4a1f-bb9b-8d1273ffb1b1",
                "f47ac10b-58cc-4372-a567-0e02b2c3d479"));*/

        logService.add(LogType.INFO, "ExecuteCronJobs  cron job completed");
    }
}
