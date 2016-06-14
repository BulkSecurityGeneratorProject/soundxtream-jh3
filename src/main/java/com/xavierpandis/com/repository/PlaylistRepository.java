package com.xavierpandis.com.repository;

import com.xavierpandis.com.domain.Playlist;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Playlist entity.
 */
@SuppressWarnings("unused")
public interface PlaylistRepository extends JpaRepository<Playlist,Long> {

    @Query("select playlist from Playlist playlist where playlist.user.login = ?#{principal.username}")
    List<Playlist> findByUserIsCurrentUser();

    @Query("select distinct playlist from Playlist playlist left join fetch playlist.tracks")
    List<Playlist> findAllWithEagerRelationships();

    @Query("select playlist from Playlist playlist left join fetch playlist.tracks where playlist.id =:id")
    Playlist findOneWithEagerRelationships(@Param("id") Long id);

}
