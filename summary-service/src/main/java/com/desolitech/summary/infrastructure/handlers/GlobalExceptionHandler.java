package com.desolitech.summary.infrastructure.handlers;


import com.desolitech.summary.domian.constants.LogType;
import com.desolitech.summary.domian.response.ErrorResponse;
import com.desolitech.summary.domian.services.systemLog.LogService;
import com.desolitech.summary.domian.utils.Utils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Environment environment;
    private final Utils utils;
    private final LogService logService;

    public GlobalExceptionHandler(Environment environment, Utils utils, LogService logService) {
        this.environment = environment;
        this.utils = utils;
        this.logService = logService;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    protected ResponseEntity<ErrorResponse> handleGenericError(Exception e) {
        logService.add(
                LogType.ERROR,
                String.format("Exception: %s", utils.readException(e))
        );

        var response = new ErrorResponse();

        if (e.getMessage().contains("ValidationException")){
            response.setMessage(e.getMessage().substring(38));
            response.setDetails(e.getMessage().substring(38));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        var profiles = environment.getActiveProfiles();
        if (profiles.length > 0 && profiles[0].equals("prod")) {
            response.setMessage("Some errors were found");
            response.setDetails("Some errors were found");
        }
        else {
            response.setMessage(utils.readException(e));
            response.setDetails(e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
