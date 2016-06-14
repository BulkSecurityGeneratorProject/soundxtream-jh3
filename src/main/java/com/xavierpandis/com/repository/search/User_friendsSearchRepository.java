package com.xavierpandis.com.repository.search;

import com.xavierpandis.com.domain.User_friends;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the User_friends entity.
 */
public interface User_friendsSearchRepository extends ElasticsearchRepository<User_friends, Long> {
}
