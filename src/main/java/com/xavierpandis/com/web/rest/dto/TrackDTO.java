package com.xavierpandis.com.web.rest.dto;

import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Track entity.
 */
public class TrackDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String type;

    private String label;

    private String buy_url;

    private byte[] artwork;

    private byte[] visual;

    private String tags;

    @NotNull
    private ZonedDateTime date_upload;

    private String description;

    @NotNull
    private String location_track;


    private Long userId;
    

    private String userLogin;

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
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
    public byte[] getArtwork() {
        return artwork;
    }

    public void setArtwork(byte[] artwork) {
        this.artwork = artwork;
    }
    public byte[] getVisual() {
        return visual;
    }

    public void setVisual(byte[] visual) {
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TrackDTO trackDTO = (TrackDTO) o;

        if ( ! Objects.equals(id, trackDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TrackDTO{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", type='" + type + "'" +
            ", label='" + label + "'" +
            ", buy_url='" + buy_url + "'" +
            ", artwork='" + artwork + "'" +
            ", visual='" + visual + "'" +
            ", tags='" + tags + "'" +
            ", date_upload='" + date_upload + "'" +
            ", description='" + description + "'" +
            ", location_track='" + location_track + "'" +
            '}';
    }
}
