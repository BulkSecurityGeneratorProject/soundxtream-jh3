package com.xavierpandis.com.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.xavierpandis.com.domain.Seguimiento;
import com.xavierpandis.com.domain.User;
import com.xavierpandis.com.repository.SeguimientoRepository;
import com.xavierpandis.com.repository.UserRepository;
import com.xavierpandis.com.repository.search.SeguimientoSearchRepository;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Seguimiento.
 */
@RestController
@RequestMapping("/api")
public class SeguimientoResource {

    private final Logger log = LoggerFactory.getLogger(SeguimientoResource.class);

    @Inject
    private SeguimientoRepository seguimientoRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private SeguimientoSearchRepository seguimientoSearchRepository;

    /**
     * POST  /seguimientos : Create a new seguimiento.
     *
     * @param seguimiento the seguimiento to create
     * @return the ResponseEntity with status 201 (Created) and with body the new seguimiento, or with status 400 (Bad Request) if the seguimiento has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/seguimientos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Seguimiento> createSeguimiento(@RequestBody Seguimiento seguimiento) throws URISyntaxException {
        log.debug("REST request to save Seguimiento : {}", seguimiento);
        if (seguimiento.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("seguimiento", "idexists", "A new seguimiento cannot already have an ID")).body(null);
        }
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();

        String seguido = seguimiento.getFollowed().getLogin();

        Seguimiento exist = seguimientoRepository.findExistSeguimiento(user.getLogin(),seguido);
        if(exist != null){
            if(exist.isFollowing() == null || exist.isFollowing() == false){
                exist.setFollowing(true);
            }
            else{
                exist.setFollowing(false);
            }
            return updateSeguimiento(exist);
        }

        ZonedDateTime today = ZonedDateTime.now();
        seguimiento.setFollowingDate(today);
        seguimiento.setFollower(user);
        seguimiento.setFollowed(seguimiento.getFollowed());
        seguimiento.setFollowing(true);

        Seguimiento result = seguimientoRepository.save(seguimiento);
        seguimientoSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/seguimientos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("seguimiento", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /seguimientos : Updates an existing seguimiento.
     *
     * @param seguimiento the seguimiento to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated seguimiento,
     * or with status 400 (Bad Request) if the seguimiento is not valid,
     * or with status 500 (Internal Server Error) if the seguimiento couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/seguimientos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Seguimiento> updateSeguimiento(@RequestBody Seguimiento seguimiento) throws URISyntaxException {
        log.debug("REST request to update Seguimiento : {}", seguimiento);
        if (seguimiento.getId() == null) {
            return createSeguimiento(seguimiento);
        }
        Seguimiento result = seguimientoRepository.save(seguimiento);
        seguimientoSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("seguimiento", seguimiento.getId().toString()))
            .body(result);
    }

    /**
     * GET  /seguimientos : get all the seguimientos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of seguimientos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/seguimientos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Seguimiento>> getAllSeguimientos(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Seguimientos");
        Page<Seguimiento> page = seguimientoRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/seguimientos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /seguimientos/:id : get the "id" seguimiento.
     *
     * @param id the id of the seguimiento to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the seguimiento, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/seguimientos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Seguimiento> getSeguimiento(@PathVariable Long id) {
        log.debug("REST request to get Seguimiento : {}", id);
        Seguimiento seguimiento = seguimientoRepository.findOne(id);
        return Optional.ofNullable(seguimiento)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /seguimientos/:id : delete the "id" seguimiento.
     *
     * @param id the id of the seguimiento to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/seguimientos/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSeguimiento(@PathVariable Long id) {
        log.debug("REST request to delete Seguimiento : {}", id);
        seguimientoRepository.delete(id);
        seguimientoSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("seguimiento", id.toString())).build();
    }

    /**
     * SEARCH  /_search/seguimientos?query=:query : search for the seguimiento corresponding
     * to the query.
     *
     * @param query the query of the seguimiento search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/seguimientos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Seguimiento>> searchSeguimientos(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Seguimientos for query {}", query);
        Page<Seguimiento> page = seguimientoSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/seguimientos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
