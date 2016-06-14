package com.xavierpandis.com.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.xavierpandis.com.domain.Track;
import com.xavierpandis.com.domain.User;
import com.xavierpandis.com.repository.TrackRepository;
import com.xavierpandis.com.repository.UserRepository;
import com.xavierpandis.com.repository.search.TrackSearchRepository;
import com.xavierpandis.com.security.SecurityUtils;
import com.xavierpandis.com.web.rest.util.HeaderUtil;
import com.xavierpandis.com.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Track.
 */
@RestController
@RequestMapping("/api")
public class TrackResource {

    private final Logger log = LoggerFactory.getLogger(TrackResource.class);

    @Inject
    private TrackRepository trackRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private TrackSearchRepository trackSearchRepository;

    /**
     * POST  /tracks : Create a new track.
     *
     * @param track the track to create
     * @return the ResponseEntity with status 201 (Created) and with body the new track, or with status 400 (Bad Request) if the track has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/tracks",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Track> createTrack(@Valid @RequestBody Track track) throws URISyntaxException {
        log.debug("REST request to save Track : {}", track);
        if (track.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("track", "idexists", "A new track cannot already have an ID")).body(null);
        }
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();

        ZonedDateTime today = ZonedDateTime.now();

        track.setDate_upload(today);
        track.setUser(user);

        Track result = trackRepository.save(track);
        trackSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/tracks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("track", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tracks : Updates an existing track.
     *
     * @param track the track to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated track,
     * or with status 400 (Bad Request) if the track is not valid,
     * or with status 500 (Internal Server Error) if the track couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/tracks",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Track> updateTrack(@Valid @RequestBody Track track) throws URISyntaxException {
        log.debug("REST request to update Track : {}", track);
        if (track.getId() == null) {
            return createTrack(track);
        }
        Track result = trackRepository.save(track);
        trackSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("track", track.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tracks : get all the tracks.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of tracks in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/tracks",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Track>> getAllTracks(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Tracks");
        Page<Track> page = trackRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tracks");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tracks/:id : get the "id" track.
     *
     * @param id the id of the track to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the track, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/tracks/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Track> getTrack(@PathVariable Long id) {
        log.debug("REST request to get Track : {}", id);
        Track track = trackRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(track)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /tracks/:id : delete the "id" track.
     *
     * @param id the id of the track to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/tracks/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        log.debug("REST request to delete Track : {}", id);
        trackRepository.delete(id);
        trackSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("track", id.toString())).build();
    }

    /**
     * SEARCH  /_search/tracks?query=:query : search for the track corresponding
     * to the query.
     *
     * @param query the query of the track search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/tracks",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Track>> searchTracks(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Tracks for query {}", query);
        Page<Track> page = trackSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/tracks");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
