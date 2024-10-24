package com.desolitech.summary.infrastructure.models;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Component
@Audited
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseDbModel {
    @Id
    private UUID id;
    protected Date createAt;
    protected String createBy;
    protected Date updateAt;
    protected String updateBy;
    @Transient
    protected int auditRevision;
    @Transient
    protected String auditRevisionType;

    public BaseDbModel() {
    }

    public BaseDbModel(UUID id) {
        this.id = id;
        this.createAt = new Date();
        this.updateAt = new Date();
    }

    public BaseDbModel(UUID id, boolean active, Date createAt, Date updateAt, Date deleteAt) {
        this.id = id;
        this.createAt = createAt;
        this.updateAt = updateAt;
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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
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


