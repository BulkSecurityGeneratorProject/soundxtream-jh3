package com.xavierpandis.com.repository.search;

import com.xavierpandis.com.domain.Track;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Track entity.
 */
public interface TrackSearchRepository extends ElasticsearchRepository<Track, Long> {
}
