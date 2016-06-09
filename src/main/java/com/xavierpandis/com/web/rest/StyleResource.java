package com.xavierpandis.com.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.xavierpandis.com.domain.Style;
import com.xavierpandis.com.repository.StyleRepository;
import com.xavierpandis.com.repository.search.StyleSearchRepository;
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
 * REST controller for managing Style.
 */
@RestController
@RequestMapping("/api")
public class StyleResource {

    private final Logger log = LoggerFactory.getLogger(StyleResource.class);
        
    @Inject
    private StyleRepository styleRepository;
    
    @Inject
    private StyleSearchRepository styleSearchRepository;
    
    /**
     * POST  /styles : Create a new style.
     *
     * @param style the style to create
     * @return the ResponseEntity with status 201 (Created) and with body the new style, or with status 400 (Bad Request) if the style has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/styles",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Style> createStyle(@Valid @RequestBody Style style) throws URISyntaxException {
        log.debug("REST request to save Style : {}", style);
        if (style.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("style", "idexists", "A new style cannot already have an ID")).body(null);
        }
        Style result = styleRepository.save(style);
        styleSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/styles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("style", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /styles : Updates an existing style.
     *
     * @param style the style to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated style,
     * or with status 400 (Bad Request) if the style is not valid,
     * or with status 500 (Internal Server Error) if the style couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/styles",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Style> updateStyle(@Valid @RequestBody Style style) throws URISyntaxException {
        log.debug("REST request to update Style : {}", style);
        if (style.getId() == null) {
            return createStyle(style);
        }
        Style result = styleRepository.save(style);
        styleSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("style", style.getId().toString()))
            .body(result);
    }

    /**
     * GET  /styles : get all the styles.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of styles in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/styles",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Style>> getAllStyles(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Styles");
        Page<Style> page = styleRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/styles");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /styles/:id : get the "id" style.
     *
     * @param id the id of the style to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the style, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/styles/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Style> getStyle(@PathVariable Long id) {
        log.debug("REST request to get Style : {}", id);
        Style style = styleRepository.findOne(id);
        return Optional.ofNullable(style)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /styles/:id : delete the "id" style.
     *
     * @param id the id of the style to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/styles/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteStyle(@PathVariable Long id) {
        log.debug("REST request to delete Style : {}", id);
        styleRepository.delete(id);
        styleSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("style", id.toString())).build();
    }

    /**
     * SEARCH  /_search/styles?query=:query : search for the style corresponding
     * to the query.
     *
     * @param query the query of the style search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/styles",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Style>> searchStyles(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Styles for query {}", query);
        Page<Style> page = styleSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/styles");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
