package com.desolitech.summary.infrastructure.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "entity_log")
public class EntityLogDbModel extends BaseDbModel {
    private String createdBy;
    private String logType;
    private String actionType;
    @Column(columnDefinition = "TEXT")
    private String description;

    public EntityLogDbModel() {
    }

    public EntityLogDbModel(String createdBy, String logType, String actionType, String description) {
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

    public void setActionType(String action) {
        this.actionType = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
