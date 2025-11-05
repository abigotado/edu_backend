package com.masters.edu.backend.domain.user;

import com.masters.edu.backend.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "profiles", uniqueConstraints = {
        @UniqueConstraint(name = "uk_profile_user", columnNames = "user_id")
})
public class Profile extends BaseEntity {

    @Column(name = "bio", columnDefinition = "text")
    private String bio;

    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    @Column(name = "headline", length = 255)
    private String headline;

    @Column(name = "social_links", columnDefinition = "jsonb")
    private String socialLinks;

    @Column(name = "preferences", columnDefinition = "jsonb")
    private String preferences;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}


