package com.desolitech.summary.domian.entities;

import java.util.Date;
import java.util.UUID;

public class BaseEntity {
    protected UUID id;
    protected Date createAt;
    protected int auditRevision;
    protected String auditRevisionType;

    public BaseEntity() {
    }

    public BaseEntity(UUID id) {
        this.id = id;
        this.createAt = new Date();
    }

    public BaseEntity(UUID id, boolean active, Date createAt, Date updateAt, Date deleteAt) {
        this.id = id;
        this.createAt = createAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public int getAuditRevision() {
        return auditRevision;
    }

    public void setAuditRevision(int auditRevision) {
        this.auditRevision = auditRevision;
    }

    public String getAuditRevisionType() {
        return auditRevisionType;
    }

    public void setAuditRevisionType(String auditRevisionType) {
        this.auditRevisionType = auditRevisionType;
    }
}
