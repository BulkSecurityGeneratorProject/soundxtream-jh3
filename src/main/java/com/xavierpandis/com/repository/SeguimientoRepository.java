package com.xavierpandis.com.repository;

import com.xavierpandis.com.domain.Seguimiento;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Seguimiento entity.
 */
@SuppressWarnings("unused")
public interface SeguimientoRepository extends JpaRepository<Seguimiento,Long> {

    @Query("select seguimiento from Seguimiento seguimiento where seguimiento.follower.login = ?#{principal.username}")
    List<Seguimiento> findByFollowerIsCurrentUser();

    @Query("select seguimiento from Seguimiento seguimiento where seguimiento.followed.login = ?#{principal.username}")
    List<Seguimiento> findByFollowedIsCurrentUser();

    @Query("select seguimiento from Seguimiento seguimiento where seguimiento.follower.login = :seguidor AND seguimiento.followed.login = :seguido")
    Seguimiento findExistSeguimiento(@Param("seguidor") String seguidor, @Param("seguido") String seguido);

}
