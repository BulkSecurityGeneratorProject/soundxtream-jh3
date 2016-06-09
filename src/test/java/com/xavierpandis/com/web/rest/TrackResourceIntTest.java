package com.xavierpandis.com.web.rest;

import com.xavierpandis.com.Soundxtream3App;
import com.xavierpandis.com.domain.Track;
import com.xavierpandis.com.repository.TrackRepository;
import com.xavierpandis.com.repository.search.TrackSearchRepository;

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
import org.springframework.util.Base64Utils;

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

import com.xavierpandis.com.domain.enumeration.TypeTrack;

/**
 * Test class for the TrackResource REST controller.
 *
 * @see TrackResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Soundxtream3App.class)
@WebAppConfiguration
@IntegrationTest
public class TrackResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_LABEL = "AAAAA";
    private static final String UPDATED_LABEL = "BBBBB";
    private static final String DEFAULT_BUY_URL = "AAAAA";
    private static final String UPDATED_BUY_URL = "BBBBB";
    private static final String DEFAULT_TAGS = "AAAAA";
    private static final String UPDATED_TAGS = "BBBBB";

    private static final ZonedDateTime DEFAULT_DATE_UPLOAD = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE_UPLOAD = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_UPLOAD_STR = dateTimeFormatter.format(DEFAULT_DATE_UPLOAD);
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";
    private static final String DEFAULT_LOCATION_TRACK = "AAAAA";
    private static final String UPDATED_LOCATION_TRACK = "BBBBB";

    private static final TypeTrack DEFAULT_TYPE = TypeTrack.original;
    private static final TypeTrack UPDATED_TYPE = TypeTrack.remix;
    private static final String DEFAULT_ACCESS_URL = "AAAAA";
    private static final String UPDATED_ACCESS_URL = "BBBBB";

    private static final byte[] DEFAULT_ARTWORK = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_ARTWORK = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_ARTWORK_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_ARTWORK_CONTENT_TYPE = "image/png";

    private static final byte[] DEFAULT_VISUAL = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_VISUAL = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_VISUAL_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_VISUAL_CONTENT_TYPE = "image/png";

    @Inject
    private TrackRepository trackRepository;

    @Inject
    private TrackSearchRepository trackSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTrackMockMvc;

    private Track track;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TrackResource trackResource = new TrackResource();
        ReflectionTestUtils.setField(trackResource, "trackSearchRepository", trackSearchRepository);
        ReflectionTestUtils.setField(trackResource, "trackRepository", trackRepository);
        this.restTrackMockMvc = MockMvcBuilders.standaloneSetup(trackResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        trackSearchRepository.deleteAll();
        track = new Track();
        track.setName(DEFAULT_NAME);
        track.setLabel(DEFAULT_LABEL);
        track.setBuy_url(DEFAULT_BUY_URL);
        track.setTags(DEFAULT_TAGS);
        track.setDate_upload(DEFAULT_DATE_UPLOAD);
        track.setDescription(DEFAULT_DESCRIPTION);
        track.setLocation_track(DEFAULT_LOCATION_TRACK);
        track.setType(DEFAULT_TYPE);
        track.setAccessUrl(DEFAULT_ACCESS_URL);
        track.setArtwork(DEFAULT_ARTWORK);
        track.setArtworkContentType(DEFAULT_ARTWORK_CONTENT_TYPE);
        track.setVisual(DEFAULT_VISUAL);
        track.setVisualContentType(DEFAULT_VISUAL_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createTrack() throws Exception {
        int databaseSizeBeforeCreate = trackRepository.findAll().size();

        // Create the Track

        restTrackMockMvc.perform(post("/api/tracks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(track)))
                .andExpect(status().isCreated());

        // Validate the Track in the database
        List<Track> tracks = trackRepository.findAll();
        assertThat(tracks).hasSize(databaseSizeBeforeCreate + 1);
        Track testTrack = tracks.get(tracks.size() - 1);
        assertThat(testTrack.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTrack.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testTrack.getBuy_url()).isEqualTo(DEFAULT_BUY_URL);
        assertThat(testTrack.getTags()).isEqualTo(DEFAULT_TAGS);
        assertThat(testTrack.getDate_upload()).isEqualTo(DEFAULT_DATE_UPLOAD);
        assertThat(testTrack.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTrack.getLocation_track()).isEqualTo(DEFAULT_LOCATION_TRACK);
        assertThat(testTrack.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTrack.getAccessUrl()).isEqualTo(DEFAULT_ACCESS_URL);
        assertThat(testTrack.getArtwork()).isEqualTo(DEFAULT_ARTWORK);
        assertThat(testTrack.getArtworkContentType()).isEqualTo(DEFAULT_ARTWORK_CONTENT_TYPE);
        assertThat(testTrack.getVisual()).isEqualTo(DEFAULT_VISUAL);
        assertThat(testTrack.getVisualContentType()).isEqualTo(DEFAULT_VISUAL_CONTENT_TYPE);

        // Validate the Track in ElasticSearch
        Track trackEs = trackSearchRepository.findOne(testTrack.getId());
        assertThat(trackEs).isEqualToComparingFieldByField(testTrack);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackRepository.findAll().size();
        // set the field null
        track.setName(null);

        // Create the Track, which fails.

        restTrackMockMvc.perform(post("/api/tracks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(track)))
                .andExpect(status().isBadRequest());

        List<Track> tracks = trackRepository.findAll();
        assertThat(tracks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDate_uploadIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackRepository.findAll().size();
        // set the field null
        track.setDate_upload(null);

        // Create the Track, which fails.

        restTrackMockMvc.perform(post("/api/tracks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(track)))
                .andExpect(status().isBadRequest());

        List<Track> tracks = trackRepository.findAll();
        assertThat(tracks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLocation_trackIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackRepository.findAll().size();
        // set the field null
        track.setLocation_track(null);

        // Create the Track, which fails.

        restTrackMockMvc.perform(post("/api/tracks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(track)))
                .andExpect(status().isBadRequest());

        List<Track> tracks = trackRepository.findAll();
        assertThat(tracks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackRepository.findAll().size();
        // set the field null
        track.setType(null);

        // Create the Track, which fails.

        restTrackMockMvc.perform(post("/api/tracks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(track)))
                .andExpect(status().isBadRequest());

        List<Track> tracks = trackRepository.findAll();
        assertThat(tracks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAccessUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackRepository.findAll().size();
        // set the field null
        track.setAccessUrl(null);

        // Create the Track, which fails.

        restTrackMockMvc.perform(post("/api/tracks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(track)))
                .andExpect(status().isBadRequest());

        List<Track> tracks = trackRepository.findAll();
        assertThat(tracks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTracks() throws Exception {
        // Initialize the database
        trackRepository.saveAndFlush(track);

        // Get all the tracks
        restTrackMockMvc.perform(get("/api/tracks?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(track.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL.toString())))
                .andExpect(jsonPath("$.[*].buy_url").value(hasItem(DEFAULT_BUY_URL.toString())))
                .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS.toString())))
                .andExpect(jsonPath("$.[*].date_upload").value(hasItem(DEFAULT_DATE_UPLOAD_STR)))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].location_track").value(hasItem(DEFAULT_LOCATION_TRACK.toString())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].accessUrl").value(hasItem(DEFAULT_ACCESS_URL.toString())))
                .andExpect(jsonPath("$.[*].artworkContentType").value(hasItem(DEFAULT_ARTWORK_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].artwork").value(hasItem(Base64Utils.encodeToString(DEFAULT_ARTWORK))))
                .andExpect(jsonPath("$.[*].visualContentType").value(hasItem(DEFAULT_VISUAL_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].visual").value(hasItem(Base64Utils.encodeToString(DEFAULT_VISUAL))));
    }

    @Test
    @Transactional
    public void getTrack() throws Exception {
        // Initialize the database
        trackRepository.saveAndFlush(track);

        // Get the track
        restTrackMockMvc.perform(get("/api/tracks/{id}", track.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(track.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL.toString()))
            .andExpect(jsonPath("$.buy_url").value(DEFAULT_BUY_URL.toString()))
            .andExpect(jsonPath("$.tags").value(DEFAULT_TAGS.toString()))
            .andExpect(jsonPath("$.date_upload").value(DEFAULT_DATE_UPLOAD_STR))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.location_track").value(DEFAULT_LOCATION_TRACK.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.accessUrl").value(DEFAULT_ACCESS_URL.toString()))
            .andExpect(jsonPath("$.artworkContentType").value(DEFAULT_ARTWORK_CONTENT_TYPE))
            .andExpect(jsonPath("$.artwork").value(Base64Utils.encodeToString(DEFAULT_ARTWORK)))
            .andExpect(jsonPath("$.visualContentType").value(DEFAULT_VISUAL_CONTENT_TYPE))
            .andExpect(jsonPath("$.visual").value(Base64Utils.encodeToString(DEFAULT_VISUAL)));
    }

    @Test
    @Transactional
    public void getNonExistingTrack() throws Exception {
        // Get the track
        restTrackMockMvc.perform(get("/api/tracks/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTrack() throws Exception {
        // Initialize the database
        trackRepository.saveAndFlush(track);
        trackSearchRepository.save(track);
        int databaseSizeBeforeUpdate = trackRepository.findAll().size();

        // Update the track
        Track updatedTrack = new Track();
        updatedTrack.setId(track.getId());
        updatedTrack.setName(UPDATED_NAME);
        updatedTrack.setLabel(UPDATED_LABEL);
        updatedTrack.setBuy_url(UPDATED_BUY_URL);
        updatedTrack.setTags(UPDATED_TAGS);
        updatedTrack.setDate_upload(UPDATED_DATE_UPLOAD);
        updatedTrack.setDescription(UPDATED_DESCRIPTION);
        updatedTrack.setLocation_track(UPDATED_LOCATION_TRACK);
        updatedTrack.setType(UPDATED_TYPE);
        updatedTrack.setAccessUrl(UPDATED_ACCESS_URL);
        updatedTrack.setArtwork(UPDATED_ARTWORK);
        updatedTrack.setArtworkContentType(UPDATED_ARTWORK_CONTENT_TYPE);
        updatedTrack.setVisual(UPDATED_VISUAL);
        updatedTrack.setVisualContentType(UPDATED_VISUAL_CONTENT_TYPE);

        restTrackMockMvc.perform(put("/api/tracks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTrack)))
                .andExpect(status().isOk());

        // Validate the Track in the database
        List<Track> tracks = trackRepository.findAll();
        assertThat(tracks).hasSize(databaseSizeBeforeUpdate);
        Track testTrack = tracks.get(tracks.size() - 1);
        assertThat(testTrack.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTrack.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testTrack.getBuy_url()).isEqualTo(UPDATED_BUY_URL);
        assertThat(testTrack.getTags()).isEqualTo(UPDATED_TAGS);
        assertThat(testTrack.getDate_upload()).isEqualTo(UPDATED_DATE_UPLOAD);
        assertThat(testTrack.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTrack.getLocation_track()).isEqualTo(UPDATED_LOCATION_TRACK);
        assertThat(testTrack.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTrack.getAccessUrl()).isEqualTo(UPDATED_ACCESS_URL);
        assertThat(testTrack.getArtwork()).isEqualTo(UPDATED_ARTWORK);
        assertThat(testTrack.getArtworkContentType()).isEqualTo(UPDATED_ARTWORK_CONTENT_TYPE);
        assertThat(testTrack.getVisual()).isEqualTo(UPDATED_VISUAL);
        assertThat(testTrack.getVisualContentType()).isEqualTo(UPDATED_VISUAL_CONTENT_TYPE);

        // Validate the Track in ElasticSearch
        Track trackEs = trackSearchRepository.findOne(testTrack.getId());
        assertThat(trackEs).isEqualToComparingFieldByField(testTrack);
    }

    @Test
    @Transactional
    public void deleteTrack() throws Exception {
        // Initialize the database
        trackRepository.saveAndFlush(track);
        trackSearchRepository.save(track);
        int databaseSizeBeforeDelete = trackRepository.findAll().size();

        // Get the track
        restTrackMockMvc.perform(delete("/api/tracks/{id}", track.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean trackExistsInEs = trackSearchRepository.exists(track.getId());
        assertThat(trackExistsInEs).isFalse();

        // Validate the database is empty
        List<Track> tracks = trackRepository.findAll();
        assertThat(tracks).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTrack() throws Exception {
        // Initialize the database
        trackRepository.saveAndFlush(track);
        trackSearchRepository.save(track);

        // Search the track
        restTrackMockMvc.perform(get("/api/_search/tracks?query=id:" + track.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(track.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL.toString())))
            .andExpect(jsonPath("$.[*].buy_url").value(hasItem(DEFAULT_BUY_URL.toString())))
            .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS.toString())))
            .andExpect(jsonPath("$.[*].date_upload").value(hasItem(DEFAULT_DATE_UPLOAD_STR)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].location_track").value(hasItem(DEFAULT_LOCATION_TRACK.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].accessUrl").value(hasItem(DEFAULT_ACCESS_URL.toString())))
            .andExpect(jsonPath("$.[*].artworkContentType").value(hasItem(DEFAULT_ARTWORK_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].artwork").value(hasItem(Base64Utils.encodeToString(DEFAULT_ARTWORK))))
            .andExpect(jsonPath("$.[*].visualContentType").value(hasItem(DEFAULT_VISUAL_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].visual").value(hasItem(Base64Utils.encodeToString(DEFAULT_VISUAL))));
    }
}
