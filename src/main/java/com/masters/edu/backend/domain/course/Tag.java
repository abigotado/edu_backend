package com.masters.edu.backend.domain.course;

import java.util.HashSet;
import java.util.Set;

import com.masters.edu.backend.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tags", uniqueConstraints = {
        @UniqueConstraint(name = "uk_tag_name", columnNames = "name"),
        @UniqueConstraint(name = "uk_tag_slug", columnNames = "slug")
})
public class Tag extends BaseEntity {

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "slug", nullable = false, length = 120)
    private String slug;

    @Column(name = "description", length = 500)
    private String description;

    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    private Set<CourseTag> courses = new HashSet<>();
}


