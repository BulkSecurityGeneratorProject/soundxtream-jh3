package com.xavierpandis.com.repository;

import com.xavierpandis.com.domain.Track;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Track entity.
 */
@SuppressWarnings("unused")
public interface TrackRepository extends JpaRepository<Track,Long> {

    @Query("select track from Track track where track.user.login = ?#{principal.username}")
    List<Track> findByUserIsCurrentUser();

    @Query("select distinct track from Track track left join fetch track.styles")
    List<Track> findAllWithEagerRelationships();

    @Query("select track from Track track left join fetch track.styles where track.id =:id")
    Track findOneWithEagerRelationships(@Param("id") Long id);

}
