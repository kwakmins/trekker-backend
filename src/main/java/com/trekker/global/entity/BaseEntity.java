package com.trekker.global.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@MappedSuperclass
public abstract class BaseEntity extends AuditBaseEntity {

    private boolean isDelete;
}
