package com.xavierpandis.com.web.rest;

import com.xavierpandis.com.Soundxtream3App;
import com.xavierpandis.com.domain.Seguimiento;
import com.xavierpandis.com.repository.SeguimientoRepository;
import com.xavierpandis.com.repository.search.SeguimientoSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the SeguimientoResource REST controller.
 *
 * @see SeguimientoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Soundxtream3App.class)
@WebAppConfiguration
@IntegrationTest
public class SeguimientoResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));


    private static final ZonedDateTime DEFAULT_FOLLOWING_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_FOLLOWING_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_FOLLOWING_DATE_STR = dateTimeFormatter.format(DEFAULT_FOLLOWING_DATE);

    private static final Boolean DEFAULT_FOLLOWING = false;
    private static final Boolean UPDATED_FOLLOWING = true;

    @Inject
    private SeguimientoRepository seguimientoRepository;

    @Inject
    private SeguimientoSearchRepository seguimientoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSeguimientoMockMvc;

    private Seguimiento seguimiento;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SeguimientoResource seguimientoResource = new SeguimientoResource();
        ReflectionTestUtils.setField(seguimientoResource, "seguimientoSearchRepository", seguimientoSearchRepository);
        ReflectionTestUtils.setField(seguimientoResource, "seguimientoRepository", seguimientoRepository);
        this.restSeguimientoMockMvc = MockMvcBuilders.standaloneSetup(seguimientoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        seguimientoSearchRepository.deleteAll();
        seguimiento = new Seguimiento();
        seguimiento.setFollowingDate(DEFAULT_FOLLOWING_DATE);
        seguimiento.setFollowing(DEFAULT_FOLLOWING);
    }

    @Test
    @Transactional
    public void createSeguimiento() throws Exception {
        int databaseSizeBeforeCreate = seguimientoRepository.findAll().size();

        // Create the Seguimiento

        restSeguimientoMockMvc.perform(post("/api/seguimientos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(seguimiento)))
                .andExpect(status().isCreated());

        // Validate the Seguimiento in the database
        List<Seguimiento> seguimientos = seguimientoRepository.findAll();
        assertThat(seguimientos).hasSize(databaseSizeBeforeCreate + 1);
        Seguimiento testSeguimiento = seguimientos.get(seguimientos.size() - 1);
        assertThat(testSeguimiento.getFollowingDate()).isEqualTo(DEFAULT_FOLLOWING_DATE);
        assertThat(testSeguimiento.isFollowing()).isEqualTo(DEFAULT_FOLLOWING);

        // Validate the Seguimiento in ElasticSearch
        Seguimiento seguimientoEs = seguimientoSearchRepository.findOne(testSeguimiento.getId());
        assertThat(seguimientoEs).isEqualToComparingFieldByField(testSeguimiento);
    }

    @Test
    @Transactional
    public void getAllSeguimientos() throws Exception {
        // Initialize the database
        seguimientoRepository.saveAndFlush(seguimiento);

        // Get all the seguimientos
        restSeguimientoMockMvc.perform(get("/api/seguimientos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(seguimiento.getId().intValue())))
                .andExpect(jsonPath("$.[*].followingDate").value(hasItem(DEFAULT_FOLLOWING_DATE_STR)))
                .andExpect(jsonPath("$.[*].following").value(hasItem(DEFAULT_FOLLOWING.booleanValue())));
    }

    @Test
    @Transactional
    public void getSeguimiento() throws Exception {
        // Initialize the database
        seguimientoRepository.saveAndFlush(seguimiento);

        // Get the seguimiento
        restSeguimientoMockMvc.perform(get("/api/seguimientos/{id}", seguimiento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(seguimiento.getId().intValue()))
            .andExpect(jsonPath("$.followingDate").value(DEFAULT_FOLLOWING_DATE_STR))
            .andExpect(jsonPath("$.following").value(DEFAULT_FOLLOWING.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingSeguimiento() throws Exception {
        // Get the seguimiento
        restSeguimientoMockMvc.perform(get("/api/seguimientos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSeguimiento() throws Exception {
        // Initialize the database
        seguimientoRepository.saveAndFlush(seguimiento);
        seguimientoSearchRepository.save(seguimiento);
        int databaseSizeBeforeUpdate = seguimientoRepository.findAll().size();

        // Update the seguimiento
        Seguimiento updatedSeguimiento = new Seguimiento();
        updatedSeguimiento.setId(seguimiento.getId());
        updatedSeguimiento.setFollowingDate(UPDATED_FOLLOWING_DATE);
        updatedSeguimiento.setFollowing(UPDATED_FOLLOWING);

        restSeguimientoMockMvc.perform(put("/api/seguimientos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSeguimiento)))
                .andExpect(status().isOk());

        // Validate the Seguimiento in the database
        List<Seguimiento> seguimientos = seguimientoRepository.findAll();
        assertThat(seguimientos).hasSize(databaseSizeBeforeUpdate);
        Seguimiento testSeguimiento = seguimientos.get(seguimientos.size() - 1);
        assertThat(testSeguimiento.getFollowingDate()).isEqualTo(UPDATED_FOLLOWING_DATE);
        assertThat(testSeguimiento.isFollowing()).isEqualTo(UPDATED_FOLLOWING);

        // Validate the Seguimiento in ElasticSearch
        Seguimiento seguimientoEs = seguimientoSearchRepository.findOne(testSeguimiento.getId());
        assertThat(seguimientoEs).isEqualToComparingFieldByField(testSeguimiento);
    }

    @Test
    @Transactional
    public void deleteSeguimiento() throws Exception {
        // Initialize the database
        seguimientoRepository.saveAndFlush(seguimiento);
        seguimientoSearchRepository.save(seguimiento);
        int databaseSizeBeforeDelete = seguimientoRepository.findAll().size();

        // Get the seguimiento
        restSeguimientoMockMvc.perform(delete("/api/seguimientos/{id}", seguimiento.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean seguimientoExistsInEs = seguimientoSearchRepository.exists(seguimiento.getId());
        assertThat(seguimientoExistsInEs).isFalse();

        // Validate the database is empty
        List<Seguimiento> seguimientos = seguimientoRepository.findAll();
        assertThat(seguimientos).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSeguimiento() throws Exception {
        // Initialize the database
        seguimientoRepository.saveAndFlush(seguimiento);
        seguimientoSearchRepository.save(seguimiento);

        // Search the seguimiento
        restSeguimientoMockMvc.perform(get("/api/_search/seguimientos?query=id:" + seguimiento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(seguimiento.getId().intValue())))
            .andExpect(jsonPath("$.[*].followingDate").value(hasItem(DEFAULT_FOLLOWING_DATE_STR)))
            .andExpect(jsonPath("$.[*].following").value(hasItem(DEFAULT_FOLLOWING.booleanValue())));
    }
}
