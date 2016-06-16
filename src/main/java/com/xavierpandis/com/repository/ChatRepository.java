package com.xavierpandis.com.repository;

import com.xavierpandis.com.domain.Chat;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Chat entity.
 */
@SuppressWarnings("unused")
public interface ChatRepository extends JpaRepository<Chat,Long> {

    @Query("select distinct chat from Chat chat left join fetch chat.participants")
    List<Chat> findAllWithEagerRelationships();

    @Query("select chat from Chat chat left join fetch chat.participants where chat.id =:id")
    Chat findOneWithEagerRelationships(@Param("id") Long id);

}
