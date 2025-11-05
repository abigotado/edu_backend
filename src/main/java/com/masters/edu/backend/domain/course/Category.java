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
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(name = "uk_category_name", columnNames = "name"),
        @UniqueConstraint(name = "uk_category_slug", columnNames = "slug")
})
public class Category extends BaseEntity {

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "slug", nullable = false, length = 120)
    private String slug;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "icon", length = 120)
    private String icon;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<Course> courses = new HashSet<>();
}


