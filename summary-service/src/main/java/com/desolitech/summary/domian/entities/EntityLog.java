package com.desolitech.summary.domian.entities;

import com.desolitech.summary.domian.constants.LogAction;

public class EntityLog extends BaseEntity {
    private String createdBy;
    private String logType;
    private String actionType;
    private String description;

    public EntityLog() {
    }

    public EntityLog(String createdBy, String logType, String description) {
        this.createdBy = createdBy;
        this.logType = logType;
        this.actionType = LogAction.TRACER;
        this.description = description;
    }

    public EntityLog(String createdBy, String logType, String actionType, String description) {
        this.createdBy = createdBy;
        this.logType = logType;
        this.actionType = actionType;
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
