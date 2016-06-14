package com.xavierpandis.com.repository;

import com.xavierpandis.com.domain.User_activity;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the User_activity entity.
 */
@SuppressWarnings("unused")
public interface User_activityRepository extends JpaRepository<User_activity,Long> {

    @Query("select user_activity from User_activity user_activity where user_activity.user.login = ?#{principal.username}")
    List<User_activity> findByUserIsCurrentUser();

}
