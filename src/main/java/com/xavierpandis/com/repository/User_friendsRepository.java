package com.xavierpandis.com.repository;

import com.xavierpandis.com.domain.User_friends;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the User_friends entity.
 */
@SuppressWarnings("unused")
public interface User_friendsRepository extends JpaRepository<User_friends,Long> {

    @Query("select user_friends from User_friends user_friends where user_friends.friend_to.login = ?#{principal.username}")
    List<User_friends> findByFriend_toIsCurrentUser();

    @Query("select user_friends from User_friends user_friends where user_friends.friend_from.login = ?#{principal.username}")
    List<User_friends> findByFriend_fromIsCurrentUser();

    @Query("select user_friends from User_friends user_friends where user_friends.friend_to.login = ?#{principal.username} AND user_friends.friend = false")
    List<User_friends> findFriendRequests();

}
