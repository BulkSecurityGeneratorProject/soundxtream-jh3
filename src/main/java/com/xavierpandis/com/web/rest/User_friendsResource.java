package com.xavierpandis.com.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.xavierpandis.com.domain.User;
import com.xavierpandis.com.domain.User_friends;
import com.xavierpandis.com.repository.UserRepository;
import com.xavierpandis.com.repository.User_friendsRepository;
import com.xavierpandis.com.repository.search.User_friendsSearchRepository;
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
 * REST controller for managing User_friends.
 */
@RestController
@RequestMapping("/api")
public class User_friendsResource {

    private final Logger log = LoggerFactory.getLogger(User_friendsResource.class);

    @Inject
    private User_friendsRepository user_friendsRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private User_friendsSearchRepository user_friendsSearchRepository;

    /**
     * POST  /user-friends : Create a new user_friends.
     *
     * @param user_friends the user_friends to create
     * @return the ResponseEntity with status 201 (Created) and with body the new user_friends, or with status 400 (Bad Request) if the user_friends has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/user-friends",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<User_friends> createUser_friends(@RequestBody User_friends user_friends) throws URISyntaxException {
        log.debug("REST request to save User_friends : {}", user_friends);
        if (user_friends.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("user_friends", "idexists", "A new user_friends cannot already have an ID")).body(null);
        }

        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();

        ZonedDateTime today = ZonedDateTime.now();

        user_friends.setFriend_from(user);
        user_friends.setFriend(false);
        user_friends.setFriendshipDate(today);

        User_friends result = user_friendsRepository.save(user_friends);
        user_friendsSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/user-friends/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("user_friends", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /user-friends : Updates an existing user_friends.
     *
     * @param user_friends the user_friends to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated user_friends,
     * or with status 400 (Bad Request) if the user_friends is not valid,
     * or with status 500 (Internal Server Error) if the user_friends couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/user-friends",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<User_friends> updateUser_friends(@RequestBody User_friends user_friends) throws URISyntaxException {
        log.debug("REST request to update User_friends : {}", user_friends);
        if (user_friends.getId() == null) {
            return createUser_friends(user_friends);
        }

        User_friends result = user_friendsRepository.save(user_friends);
        user_friendsSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("user_friends", user_friends.getId().toString()))
            .body(result);
    }

    /**
     * GET  /user-friends : get all the user_friends.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of user_friends in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/user-friends",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<User_friends>> getAllUser_friends(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of User_friends");
        Page<User_friends> page = user_friendsRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user-friends");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /user-friends/:id : get the "id" user_friends.
     *
     * @param id the id of the user_friends to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the user_friends, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/user-friends/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<User_friends> getUser_friends(@PathVariable Long id) {
        log.debug("REST request to get User_friends : {}", id);
        User_friends user_friends = user_friendsRepository.findOne(id);
        return Optional.ofNullable(user_friends)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/friendship_notifications",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<User_friends>> getFriendRequests() {
        List <User_friends> user_friends = user_friendsRepository.findFriendRequests();

        return Optional.ofNullable(user_friends)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /user-friends/:id : delete the "id" user_friends.
     *
     * @param id the id of the user_friends to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/user-friends/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteUser_friends(@PathVariable Long id) {
        log.debug("REST request to delete User_friends : {}", id);
        user_friendsRepository.delete(id);
        user_friendsSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("user_friends", id.toString())).build();
    }

    /**
     * SEARCH  /_search/user-friends?query=:query : search for the user_friends corresponding
     * to the query.
     *
     * @param query the query of the user_friends search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/user-friends",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<User_friends>> searchUser_friends(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of User_friends for query {}", query);
        Page<User_friends> page = user_friendsSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/user-friends");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
