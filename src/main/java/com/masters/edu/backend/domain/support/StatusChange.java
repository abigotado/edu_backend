package com.masters.edu.backend.domain.support;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class StatusChange {

    @Column(name = "status_changed_at", nullable = false)
    private OffsetDateTime changedAt;

    @Column(name = "status_changed_by")
    private Long changedById;

    public static StatusChange now(Long userId) {
        return new StatusChange(OffsetDateTime.now(), userId);
    }
}


