package com.xavierpandis.com.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import com.xavierpandis.com.domain.enumeration.TypeTrack;

/**
 * A Track.
 */
@Entity
@Table(name = "track")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "track")
public class Track implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "label")
    private String label;

    @Column(name = "buy_url")
    private String buy_url;

    @Column(name = "artwork")
    private String artwork;

    @Column(name = "visual")
    private String visual;

    @Column(name = "tags")
    private String tags;

    @NotNull
    @Column(name = "date_upload", nullable = false)
    private ZonedDateTime date_upload;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "location_track", nullable = false)
    private String location_track;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeTrack type;

    @NotNull
    @Column(name = "access_url", nullable = false)
    private String accessUrl;

    @ManyToOne
    private User user;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "track_style",
               joinColumns = @JoinColumn(name="tracks_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="styles_id", referencedColumnName="ID"))
    private Set<Style> styles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getBuy_url() {
        return buy_url;
    }

    public void setBuy_url(String buy_url) {
        this.buy_url = buy_url;
    }

    public String getArtwork() {
        return artwork;
    }

    public void setArtwork(String artwork) {
        this.artwork = artwork;
    }

    public String getVisual() {
        return visual;
    }

    public void setVisual(String visual) {
        this.visual = visual;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public ZonedDateTime getDate_upload() {
        return date_upload;
    }

    public void setDate_upload(ZonedDateTime date_upload) {
        this.date_upload = date_upload;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation_track() {
        return location_track;
    }

    public void setLocation_track(String location_track) {
        this.location_track = location_track;
    }

    public TypeTrack getType() {
        return type;
    }

    public void setType(TypeTrack type) {
        this.type = type;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Style> getStyles() {
        return styles;
    }

    public void setStyles(Set<Style> styles) {
        this.styles = styles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Track track = (Track) o;
        if(track.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, track.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Track{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", label='" + label + "'" +
            ", buy_url='" + buy_url + "'" +
            ", artwork='" + artwork + "'" +
            ", visual='" + visual + "'" +
            ", tags='" + tags + "'" +
            ", date_upload='" + date_upload + "'" +
            ", description='" + description + "'" +
            ", location_track='" + location_track + "'" +
            ", type='" + type + "'" +
            ", accessUrl='" + accessUrl + "'" +
            '}';
    }
}
