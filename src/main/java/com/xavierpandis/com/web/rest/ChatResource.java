package com.xavierpandis.com.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.xavierpandis.com.domain.Chat;
import com.xavierpandis.com.repository.ChatRepository;
import com.xavierpandis.com.repository.search.ChatSearchRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Chat.
 */
@RestController
@RequestMapping("/api")
public class ChatResource {

    private final Logger log = LoggerFactory.getLogger(ChatResource.class);
        
    @Inject
    private ChatRepository chatRepository;
    
    @Inject
    private ChatSearchRepository chatSearchRepository;
    
    /**
     * POST  /chats : Create a new chat.
     *
     * @param chat the chat to create
     * @return the ResponseEntity with status 201 (Created) and with body the new chat, or with status 400 (Bad Request) if the chat has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/chats",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Chat> createChat(@RequestBody Chat chat) throws URISyntaxException {
        log.debug("REST request to save Chat : {}", chat);
        if (chat.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("chat", "idexists", "A new chat cannot already have an ID")).body(null);
        }
        Chat result = chatRepository.save(chat);
        chatSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/chats/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("chat", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /chats : Updates an existing chat.
     *
     * @param chat the chat to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated chat,
     * or with status 400 (Bad Request) if the chat is not valid,
     * or with status 500 (Internal Server Error) if the chat couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/chats",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Chat> updateChat(@RequestBody Chat chat) throws URISyntaxException {
        log.debug("REST request to update Chat : {}", chat);
        if (chat.getId() == null) {
            return createChat(chat);
        }
        Chat result = chatRepository.save(chat);
        chatSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("chat", chat.getId().toString()))
            .body(result);
    }

    /**
     * GET  /chats : get all the chats.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of chats in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/chats",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Chat>> getAllChats(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Chats");
        Page<Chat> page = chatRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/chats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /chats/:id : get the "id" chat.
     *
     * @param id the id of the chat to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the chat, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/chats/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Chat> getChat(@PathVariable Long id) {
        log.debug("REST request to get Chat : {}", id);
        Chat chat = chatRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(chat)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /chats/:id : delete the "id" chat.
     *
     * @param id the id of the chat to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/chats/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) {
        log.debug("REST request to delete Chat : {}", id);
        chatRepository.delete(id);
        chatSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("chat", id.toString())).build();
    }

    /**
     * SEARCH  /_search/chats?query=:query : search for the chat corresponding
     * to the query.
     *
     * @param query the query of the chat search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/chats",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Chat>> searchChats(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Chats for query {}", query);
        Page<Chat> page = chatSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/chats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
