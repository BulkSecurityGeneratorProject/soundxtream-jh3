package com.xavierpandis.com.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A User_friends.
 */
@Entity
@Table(name = "user_friends")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "user_friends")
public class User_friends implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "friend")
    private Boolean friend;

    @Column(name = "friendship_date")
    private ZonedDateTime friendshipDate;

    @ManyToOne
    private User friend_to;

    @ManyToOne
    private User friend_from;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isFriend() {
        return friend;
    }

    public void setFriend(Boolean friend) {
        this.friend = friend;
    }

    public ZonedDateTime getFriendshipDate() {
        return friendshipDate;
    }

    public void setFriendshipDate(ZonedDateTime friendshipDate) {
        this.friendshipDate = friendshipDate;
    }

    public User getFriend_to() {
        return friend_to;
    }

    public void setFriend_to(User user) {
        this.friend_to = user;
    }

    public User getFriend_from() {
        return friend_from;
    }

    public void setFriend_from(User user) {
        this.friend_from = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User_friends user_friends = (User_friends) o;
        if(user_friends.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, user_friends.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User_friends{" +
            "id=" + id +
            ", friend='" + friend + "'" +
            ", friendshipDate='" + friendshipDate + "'" +
            '}';
    }
}
