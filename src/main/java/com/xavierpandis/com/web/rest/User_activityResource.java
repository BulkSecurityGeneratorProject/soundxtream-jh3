package com.xavierpandis.com.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.xavierpandis.com.domain.User_activity;
import com.xavierpandis.com.repository.User_activityRepository;
import com.xavierpandis.com.repository.search.User_activitySearchRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing User_activity.
 */
@RestController
@RequestMapping("/api")
public class User_activityResource {

    private final Logger log = LoggerFactory.getLogger(User_activityResource.class);
        
    @Inject
    private User_activityRepository user_activityRepository;
    
    @Inject
    private User_activitySearchRepository user_activitySearchRepository;
    
    /**
     * POST  /user-activities : Create a new user_activity.
     *
     * @param user_activity the user_activity to create
     * @return the ResponseEntity with status 201 (Created) and with body the new user_activity, or with status 400 (Bad Request) if the user_activity has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/user-activities",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<User_activity> createUser_activity(@Valid @RequestBody User_activity user_activity) throws URISyntaxException {
        log.debug("REST request to save User_activity : {}", user_activity);
        if (user_activity.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("user_activity", "idexists", "A new user_activity cannot already have an ID")).body(null);
        }
        User_activity result = user_activityRepository.save(user_activity);
        user_activitySearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/user-activities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("user_activity", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /user-activities : Updates an existing user_activity.
     *
     * @param user_activity the user_activity to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated user_activity,
     * or with status 400 (Bad Request) if the user_activity is not valid,
     * or with status 500 (Internal Server Error) if the user_activity couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/user-activities",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<User_activity> updateUser_activity(@Valid @RequestBody User_activity user_activity) throws URISyntaxException {
        log.debug("REST request to update User_activity : {}", user_activity);
        if (user_activity.getId() == null) {
            return createUser_activity(user_activity);
        }
        User_activity result = user_activityRepository.save(user_activity);
        user_activitySearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("user_activity", user_activity.getId().toString()))
            .body(result);
    }

    /**
     * GET  /user-activities : get all the user_activities.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of user_activities in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/user-activities",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<User_activity>> getAllUser_activities(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of User_activities");
        Page<User_activity> page = user_activityRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user-activities");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /user-activities/:id : get the "id" user_activity.
     *
     * @param id the id of the user_activity to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the user_activity, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/user-activities/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<User_activity> getUser_activity(@PathVariable Long id) {
        log.debug("REST request to get User_activity : {}", id);
        User_activity user_activity = user_activityRepository.findOne(id);
        return Optional.ofNullable(user_activity)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /user-activities/:id : delete the "id" user_activity.
     *
     * @param id the id of the user_activity to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/user-activities/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteUser_activity(@PathVariable Long id) {
        log.debug("REST request to delete User_activity : {}", id);
        user_activityRepository.delete(id);
        user_activitySearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("user_activity", id.toString())).build();
    }

    /**
     * SEARCH  /_search/user-activities?query=:query : search for the user_activity corresponding
     * to the query.
     *
     * @param query the query of the user_activity search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/user-activities",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<User_activity>> searchUser_activities(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of User_activities for query {}", query);
        Page<User_activity> page = user_activitySearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/user-activities");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
