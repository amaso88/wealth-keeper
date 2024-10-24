package com.desolitech.summary.domian.services.systemLog;

import com.desolitech.summary.domian.constants.GlobalConstant;
import com.desolitech.summary.domian.constants.LogType;
import com.desolitech.summary.domian.entities.EntityLog;
import com.desolitech.summary.domian.interfaces.repositories.EntityLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);
    private final EntityLogRepository entityLogRepository;

    public LogService(EntityLogRepository entityLogRepository) {
        this.entityLogRepository = entityLogRepository;
    }

    @Async
    public void add(String logType, String description) {
        if (logType.equals(LogType.ERROR)) {
            logger.error(description);
        } else if (logType.equals(LogType.WARNING)) {
            logger.warn(description);
        }
        else {
            logger.info(description);
        }

        var entityLog = new EntityLog(
                GlobalConstant.SYSTEM,
                logType,
                description
        );
        entityLogRepository.add(entityLog);
    }
}
