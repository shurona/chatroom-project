package com.shurona.chat.mytalk.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    
    @Column(name = "created_at", updatable = false)
    @CreatedDate
    protected LocalDateTime createdAt;

    @CreatedBy
    @Column(updatable = false)
    protected Long createdBy;

    @Column(name = "updated_at")
    @LastModifiedDate
    protected LocalDateTime updatedAt;

    @LastModifiedBy
    protected Long updatedBy;

    @Column(name = "is_deleted")
    protected Boolean isDeleted = false;

}

