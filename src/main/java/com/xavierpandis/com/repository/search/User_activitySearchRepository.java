package com.xavierpandis.com.repository.search;

import com.xavierpandis.com.domain.User_activity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the User_activity entity.
 */
public interface User_activitySearchRepository extends ElasticsearchRepository<User_activity, Long> {
}
