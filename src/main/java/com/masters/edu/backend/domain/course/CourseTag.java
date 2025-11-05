package com.masters.edu.backend.domain.course;

import com.masters.edu.backend.domain.common.BaseEntity;
import com.masters.edu.backend.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "course_tags", indexes = {
        @Index(name = "idx_course_tag_course", columnList = "course_id"),
        @Index(name = "idx_course_tag_tag", columnList = "tag_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_course_tag_pair", columnNames = {"course_id", "tag_id"})
})
public class CourseTag extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "weight")
    private Integer weight;
}


