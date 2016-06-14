package com.xavierpandis.com.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Seguimiento.
 */
@Entity
@Table(name = "seguimiento")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "seguimiento")
public class Seguimiento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "following_date")
    private ZonedDateTime followingDate;

    @Column(name = "following")
    private Boolean following;

    @ManyToOne
    private User follower;

    @ManyToOne
    private User followed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getFollowingDate() {
        return followingDate;
    }

    public void setFollowingDate(ZonedDateTime followingDate) {
        this.followingDate = followingDate;
    }

    public Boolean isFollowing() {
        return following;
    }

    public void setFollowing(Boolean following) {
        this.following = following;
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User user) {
        this.follower = user;
    }

    public User getFollowed() {
        return followed;
    }

    public void setFollowed(User user) {
        this.followed = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Seguimiento seguimiento = (Seguimiento) o;
        if(seguimiento.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, seguimiento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Seguimiento{" +
            "id=" + id +
            ", followingDate='" + followingDate + "'" +
            ", following='" + following + "'" +
            '}';
    }
}
