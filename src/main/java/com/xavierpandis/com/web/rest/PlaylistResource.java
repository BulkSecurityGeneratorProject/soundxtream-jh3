package com.xavierpandis.com.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.xavierpandis.com.domain.Playlist;
import com.xavierpandis.com.domain.User;
import com.xavierpandis.com.repository.PlaylistRepository;
import com.xavierpandis.com.repository.UserRepository;
import com.xavierpandis.com.repository.search.PlaylistSearchRepository;
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
 * REST controller for managing Playlist.
 */
@RestController
@RequestMapping("/api")
public class PlaylistResource {

    private final Logger log = LoggerFactory.getLogger(PlaylistResource.class);

    @Inject
    private PlaylistRepository playlistRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private PlaylistSearchRepository playlistSearchRepository;

    /**
     * POST  /playlists : Create a new playlist.
     *
     * @param playlist the playlist to create
     * @return the ResponseEntity with status 201 (Created) and with body the new playlist, or with status 400 (Bad Request) if the playlist has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/playlists",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Playlist> createPlaylist(@Valid @RequestBody Playlist playlist) throws URISyntaxException {
        log.debug("REST request to save Playlist : {}", playlist);
        if (playlist.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("playlist", "idexists", "A new playlist cannot already have an ID")).body(null);
        }

        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();

        ZonedDateTime today = ZonedDateTime.now();

        playlist.setDateCreated(today);
        playlist.setUser(user);

        Playlist result = playlistRepository.save(playlist);
        playlistSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/playlists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("playlist", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /playlists : Updates an existing playlist.
     *
     * @param playlist the playlist to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated playlist,
     * or with status 400 (Bad Request) if the playlist is not valid,
     * or with status 500 (Internal Server Error) if the playlist couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/playlists",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Playlist> updatePlaylist(@Valid @RequestBody Playlist playlist) throws URISyntaxException {
        log.debug("REST request to update Playlist : {}", playlist);
        if (playlist.getId() == null) {
            return createPlaylist(playlist);
        }
        Playlist result = playlistRepository.save(playlist);
        playlistSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("playlist", playlist.getId().toString()))
            .body(result);
    }

    /**
     * GET  /playlists : get all the playlists.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of playlists in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/playlists",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Playlist>> getAllPlaylists(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Playlists");
        Page<Playlist> page = playlistRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/playlists");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /playlists/:id : get the "id" playlist.
     *
     * @param id the id of the playlist to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the playlist, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/playlists/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Playlist> getPlaylist(@PathVariable Long id) {
        log.debug("REST request to get Playlist : {}", id);
        Playlist playlist = playlistRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(playlist)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /playlists/:id : delete the "id" playlist.
     *
     * @param id the id of the playlist to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/playlists/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        log.debug("REST request to delete Playlist : {}", id);
        playlistRepository.delete(id);
        playlistSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("playlist", id.toString())).build();
    }

    /**
     * SEARCH  /_search/playlists?query=:query : search for the playlist corresponding
     * to the query.
     *
     * @param query the query of the playlist search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/playlists",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Playlist>> searchPlaylists(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Playlists for query {}", query);
        Page<Playlist> page = playlistSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/playlists");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
