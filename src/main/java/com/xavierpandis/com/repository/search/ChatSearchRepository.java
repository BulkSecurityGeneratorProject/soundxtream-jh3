package com.xavierpandis.com.repository.search;

import com.xavierpandis.com.domain.Chat;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Chat entity.
 */
public interface ChatSearchRepository extends ElasticsearchRepository<Chat, Long> {
}
