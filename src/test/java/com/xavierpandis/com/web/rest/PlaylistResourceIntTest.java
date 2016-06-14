package com.xavierpandis.com.web.rest;

import com.xavierpandis.com.Soundxtream3App;
import com.xavierpandis.com.domain.Playlist;
import com.xavierpandis.com.repository.PlaylistRepository;
import com.xavierpandis.com.repository.search.PlaylistSearchRepository;

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
 * Test class for the PlaylistResource REST controller.
 *
 * @see PlaylistResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Soundxtream3App.class)
@WebAppConfiguration
@IntegrationTest
public class PlaylistResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final Double DEFAULT_DURATION = 0D;
    private static final Double UPDATED_DURATION = 1D;
    private static final String DEFAULT_ARTWORK = "AAAAA";
    private static final String UPDATED_ARTWORK = "BBBBB";
    private static final String DEFAULT_VISUAL = "AAAAA";
    private static final String UPDATED_VISUAL = "BBBBB";

    private static final ZonedDateTime DEFAULT_DATE_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_CREATED_STR = dateTimeFormatter.format(DEFAULT_DATE_CREATED);

    @Inject
    private PlaylistRepository playlistRepository;

    @Inject
    private PlaylistSearchRepository playlistSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPlaylistMockMvc;

    private Playlist playlist;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PlaylistResource playlistResource = new PlaylistResource();
        ReflectionTestUtils.setField(playlistResource, "playlistSearchRepository", playlistSearchRepository);
        ReflectionTestUtils.setField(playlistResource, "playlistRepository", playlistRepository);
        this.restPlaylistMockMvc = MockMvcBuilders.standaloneSetup(playlistResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        playlistSearchRepository.deleteAll();
        playlist = new Playlist();
        playlist.setName(DEFAULT_NAME);
        playlist.setDescription(DEFAULT_DESCRIPTION);
        playlist.setDuration(DEFAULT_DURATION);
        playlist.setArtwork(DEFAULT_ARTWORK);
        playlist.setVisual(DEFAULT_VISUAL);
        playlist.setDateCreated(DEFAULT_DATE_CREATED);
    }

    @Test
    @Transactional
    public void createPlaylist() throws Exception {
        int databaseSizeBeforeCreate = playlistRepository.findAll().size();

        // Create the Playlist

        restPlaylistMockMvc.perform(post("/api/playlists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(playlist)))
                .andExpect(status().isCreated());

        // Validate the Playlist in the database
        List<Playlist> playlists = playlistRepository.findAll();
        assertThat(playlists).hasSize(databaseSizeBeforeCreate + 1);
        Playlist testPlaylist = playlists.get(playlists.size() - 1);
        assertThat(testPlaylist.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPlaylist.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPlaylist.getDuration()).isEqualTo(DEFAULT_DURATION);
        assertThat(testPlaylist.getArtwork()).isEqualTo(DEFAULT_ARTWORK);
        assertThat(testPlaylist.getVisual()).isEqualTo(DEFAULT_VISUAL);
        assertThat(testPlaylist.getDateCreated()).isEqualTo(DEFAULT_DATE_CREATED);

        // Validate the Playlist in ElasticSearch
        Playlist playlistEs = playlistSearchRepository.findOne(testPlaylist.getId());
        assertThat(playlistEs).isEqualToComparingFieldByField(testPlaylist);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = playlistRepository.findAll().size();
        // set the field null
        playlist.setName(null);

        // Create the Playlist, which fails.

        restPlaylistMockMvc.perform(post("/api/playlists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(playlist)))
                .andExpect(status().isBadRequest());

        List<Playlist> playlists = playlistRepository.findAll();
        assertThat(playlists).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = playlistRepository.findAll().size();
        // set the field null
        playlist.setDateCreated(null);

        // Create the Playlist, which fails.

        restPlaylistMockMvc.perform(post("/api/playlists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(playlist)))
                .andExpect(status().isBadRequest());

        List<Playlist> playlists = playlistRepository.findAll();
        assertThat(playlists).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPlaylists() throws Exception {
        // Initialize the database
        playlistRepository.saveAndFlush(playlist);

        // Get all the playlists
        restPlaylistMockMvc.perform(get("/api/playlists?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(playlist.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.doubleValue())))
                .andExpect(jsonPath("$.[*].artwork").value(hasItem(DEFAULT_ARTWORK.toString())))
                .andExpect(jsonPath("$.[*].visual").value(hasItem(DEFAULT_VISUAL.toString())))
                .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED_STR)));
    }

    @Test
    @Transactional
    public void getPlaylist() throws Exception {
        // Initialize the database
        playlistRepository.saveAndFlush(playlist);

        // Get the playlist
        restPlaylistMockMvc.perform(get("/api/playlists/{id}", playlist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(playlist.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION.doubleValue()))
            .andExpect(jsonPath("$.artwork").value(DEFAULT_ARTWORK.toString()))
            .andExpect(jsonPath("$.visual").value(DEFAULT_VISUAL.toString()))
            .andExpect(jsonPath("$.dateCreated").value(DEFAULT_DATE_CREATED_STR));
    }

    @Test
    @Transactional
    public void getNonExistingPlaylist() throws Exception {
        // Get the playlist
        restPlaylistMockMvc.perform(get("/api/playlists/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePlaylist() throws Exception {
        // Initialize the database
        playlistRepository.saveAndFlush(playlist);
        playlistSearchRepository.save(playlist);
        int databaseSizeBeforeUpdate = playlistRepository.findAll().size();

        // Update the playlist
        Playlist updatedPlaylist = new Playlist();
        updatedPlaylist.setId(playlist.getId());
        updatedPlaylist.setName(UPDATED_NAME);
        updatedPlaylist.setDescription(UPDATED_DESCRIPTION);
        updatedPlaylist.setDuration(UPDATED_DURATION);
        updatedPlaylist.setArtwork(UPDATED_ARTWORK);
        updatedPlaylist.setVisual(UPDATED_VISUAL);
        updatedPlaylist.setDateCreated(UPDATED_DATE_CREATED);

        restPlaylistMockMvc.perform(put("/api/playlists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPlaylist)))
                .andExpect(status().isOk());

        // Validate the Playlist in the database
        List<Playlist> playlists = playlistRepository.findAll();
        assertThat(playlists).hasSize(databaseSizeBeforeUpdate);
        Playlist testPlaylist = playlists.get(playlists.size() - 1);
        assertThat(testPlaylist.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPlaylist.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPlaylist.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testPlaylist.getArtwork()).isEqualTo(UPDATED_ARTWORK);
        assertThat(testPlaylist.getVisual()).isEqualTo(UPDATED_VISUAL);
        assertThat(testPlaylist.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);

        // Validate the Playlist in ElasticSearch
        Playlist playlistEs = playlistSearchRepository.findOne(testPlaylist.getId());
        assertThat(playlistEs).isEqualToComparingFieldByField(testPlaylist);
    }

    @Test
    @Transactional
    public void deletePlaylist() throws Exception {
        // Initialize the database
        playlistRepository.saveAndFlush(playlist);
        playlistSearchRepository.save(playlist);
        int databaseSizeBeforeDelete = playlistRepository.findAll().size();

        // Get the playlist
        restPlaylistMockMvc.perform(delete("/api/playlists/{id}", playlist.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean playlistExistsInEs = playlistSearchRepository.exists(playlist.getId());
        assertThat(playlistExistsInEs).isFalse();

        // Validate the database is empty
        List<Playlist> playlists = playlistRepository.findAll();
        assertThat(playlists).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPlaylist() throws Exception {
        // Initialize the database
        playlistRepository.saveAndFlush(playlist);
        playlistSearchRepository.save(playlist);

        // Search the playlist
        restPlaylistMockMvc.perform(get("/api/_search/playlists?query=id:" + playlist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(playlist.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.doubleValue())))
            .andExpect(jsonPath("$.[*].artwork").value(hasItem(DEFAULT_ARTWORK.toString())))
            .andExpect(jsonPath("$.[*].visual").value(hasItem(DEFAULT_VISUAL.toString())))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED_STR)));
    }
}
