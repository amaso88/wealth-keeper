package com.desolitech.tag.domian.services.systemLog;

import com.desolitech.tag.domian.constants.GlobalConstant;
import com.desolitech.tag.domian.constants.LogType;
import com.desolitech.tag.domian.entities.EntityLog;
import com.desolitech.tag.domian.interfaces.repositories.EntityLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/*import com.opinno.urbano.domain.constants.GlobalConstant;
import com.opinno.urbano.domain.constants.LogType;
import com.opinno.urbano.domain.entities.EntityLog;
import com.opinno.urbano.domain.interfaces.repositories.EntityLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;*/

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
