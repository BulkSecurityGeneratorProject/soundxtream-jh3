package com.xavierpandis.com.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import com.xavierpandis.com.domain.enumeration.TypeActivity;

/**
 * A User_activity.
 */
@Entity
@Table(name = "user_activity")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "user_activity")
public class User_activity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeActivity type;

    @Column(name = "liked")
    private Boolean liked;

    @Column(name = "shared")
    private Boolean shared;

    @Column(name = "date_liked")
    private ZonedDateTime dateLiked;

    @Column(name = "shared_date")
    private ZonedDateTime sharedDate;

    @Column(name = "upload_date")
    private ZonedDateTime uploadDate;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @ManyToOne
    private User user;

    @ManyToOne
    private Track track;

    @ManyToOne
    private Playlist playlist;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeActivity getType() {
        return type;
    }

    public void setType(TypeActivity type) {
        this.type = type;
    }

    public Boolean isLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Boolean isShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public ZonedDateTime getDateLiked() {
        return dateLiked;
    }

    public void setDateLiked(ZonedDateTime dateLiked) {
        this.dateLiked = dateLiked;
    }

    public ZonedDateTime getSharedDate() {
        return sharedDate;
    }

    public void setSharedDate(ZonedDateTime sharedDate) {
        this.sharedDate = sharedDate;
    }

    public ZonedDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(ZonedDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User_activity user_activity = (User_activity) o;
        if(user_activity.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, user_activity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User_activity{" +
            "id=" + id +
            ", type='" + type + "'" +
            ", liked='" + liked + "'" +
            ", shared='" + shared + "'" +
            ", dateLiked='" + dateLiked + "'" +
            ", sharedDate='" + sharedDate + "'" +
            ", uploadDate='" + uploadDate + "'" +
            ", createdDate='" + createdDate + "'" +
            '}';
    }
}
