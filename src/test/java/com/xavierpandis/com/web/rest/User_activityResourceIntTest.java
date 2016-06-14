package com.xavierpandis.com.web.rest;

import com.xavierpandis.com.Soundxtream3App;
import com.xavierpandis.com.domain.User_activity;
import com.xavierpandis.com.repository.User_activityRepository;
import com.xavierpandis.com.repository.search.User_activitySearchRepository;

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

import com.xavierpandis.com.domain.enumeration.TypeActivity;

/**
 * Test class for the User_activityResource REST controller.
 *
 * @see User_activityResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Soundxtream3App.class)
@WebAppConfiguration
@IntegrationTest
public class User_activityResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));


    private static final TypeActivity DEFAULT_TYPE = TypeActivity.uploadTrack;
    private static final TypeActivity UPDATED_TYPE = TypeActivity.likedTrack;

    private static final Boolean DEFAULT_LIKED = false;
    private static final Boolean UPDATED_LIKED = true;

    private static final Boolean DEFAULT_SHARED = false;
    private static final Boolean UPDATED_SHARED = true;

    private static final ZonedDateTime DEFAULT_DATE_LIKED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE_LIKED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_LIKED_STR = dateTimeFormatter.format(DEFAULT_DATE_LIKED);

    private static final ZonedDateTime DEFAULT_SHARED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_SHARED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_SHARED_DATE_STR = dateTimeFormatter.format(DEFAULT_SHARED_DATE);

    private static final ZonedDateTime DEFAULT_UPLOAD_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_UPLOAD_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_UPLOAD_DATE_STR = dateTimeFormatter.format(DEFAULT_UPLOAD_DATE);

    private static final ZonedDateTime DEFAULT_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CREATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_CREATED_DATE_STR = dateTimeFormatter.format(DEFAULT_CREATED_DATE);

    @Inject
    private User_activityRepository user_activityRepository;

    @Inject
    private User_activitySearchRepository user_activitySearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restUser_activityMockMvc;

    private User_activity user_activity;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        User_activityResource user_activityResource = new User_activityResource();
        ReflectionTestUtils.setField(user_activityResource, "user_activitySearchRepository", user_activitySearchRepository);
        ReflectionTestUtils.setField(user_activityResource, "user_activityRepository", user_activityRepository);
        this.restUser_activityMockMvc = MockMvcBuilders.standaloneSetup(user_activityResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        user_activitySearchRepository.deleteAll();
        user_activity = new User_activity();
        user_activity.setType(DEFAULT_TYPE);
        user_activity.setLiked(DEFAULT_LIKED);
        user_activity.setShared(DEFAULT_SHARED);
        user_activity.setDateLiked(DEFAULT_DATE_LIKED);
        user_activity.setSharedDate(DEFAULT_SHARED_DATE);
        user_activity.setUploadDate(DEFAULT_UPLOAD_DATE);
        user_activity.setCreatedDate(DEFAULT_CREATED_DATE);
    }

    @Test
    @Transactional
    public void createUser_activity() throws Exception {
        int databaseSizeBeforeCreate = user_activityRepository.findAll().size();

        // Create the User_activity

        restUser_activityMockMvc.perform(post("/api/user-activities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user_activity)))
                .andExpect(status().isCreated());

        // Validate the User_activity in the database
        List<User_activity> user_activities = user_activityRepository.findAll();
        assertThat(user_activities).hasSize(databaseSizeBeforeCreate + 1);
        User_activity testUser_activity = user_activities.get(user_activities.size() - 1);
        assertThat(testUser_activity.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testUser_activity.isLiked()).isEqualTo(DEFAULT_LIKED);
        assertThat(testUser_activity.isShared()).isEqualTo(DEFAULT_SHARED);
        assertThat(testUser_activity.getDateLiked()).isEqualTo(DEFAULT_DATE_LIKED);
        assertThat(testUser_activity.getSharedDate()).isEqualTo(DEFAULT_SHARED_DATE);
        assertThat(testUser_activity.getUploadDate()).isEqualTo(DEFAULT_UPLOAD_DATE);
        assertThat(testUser_activity.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);

        // Validate the User_activity in ElasticSearch
        User_activity user_activityEs = user_activitySearchRepository.findOne(testUser_activity.getId());
        assertThat(user_activityEs).isEqualToComparingFieldByField(testUser_activity);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = user_activityRepository.findAll().size();
        // set the field null
        user_activity.setType(null);

        // Create the User_activity, which fails.

        restUser_activityMockMvc.perform(post("/api/user-activities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user_activity)))
                .andExpect(status().isBadRequest());

        List<User_activity> user_activities = user_activityRepository.findAll();
        assertThat(user_activities).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllUser_activities() throws Exception {
        // Initialize the database
        user_activityRepository.saveAndFlush(user_activity);

        // Get all the user_activities
        restUser_activityMockMvc.perform(get("/api/user-activities?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(user_activity.getId().intValue())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].liked").value(hasItem(DEFAULT_LIKED.booleanValue())))
                .andExpect(jsonPath("$.[*].shared").value(hasItem(DEFAULT_SHARED.booleanValue())))
                .andExpect(jsonPath("$.[*].dateLiked").value(hasItem(DEFAULT_DATE_LIKED_STR)))
                .andExpect(jsonPath("$.[*].sharedDate").value(hasItem(DEFAULT_SHARED_DATE_STR)))
                .andExpect(jsonPath("$.[*].uploadDate").value(hasItem(DEFAULT_UPLOAD_DATE_STR)))
                .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE_STR)));
    }

    @Test
    @Transactional
    public void getUser_activity() throws Exception {
        // Initialize the database
        user_activityRepository.saveAndFlush(user_activity);

        // Get the user_activity
        restUser_activityMockMvc.perform(get("/api/user-activities/{id}", user_activity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(user_activity.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.liked").value(DEFAULT_LIKED.booleanValue()))
            .andExpect(jsonPath("$.shared").value(DEFAULT_SHARED.booleanValue()))
            .andExpect(jsonPath("$.dateLiked").value(DEFAULT_DATE_LIKED_STR))
            .andExpect(jsonPath("$.sharedDate").value(DEFAULT_SHARED_DATE_STR))
            .andExpect(jsonPath("$.uploadDate").value(DEFAULT_UPLOAD_DATE_STR))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingUser_activity() throws Exception {
        // Get the user_activity
        restUser_activityMockMvc.perform(get("/api/user-activities/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUser_activity() throws Exception {
        // Initialize the database
        user_activityRepository.saveAndFlush(user_activity);
        user_activitySearchRepository.save(user_activity);
        int databaseSizeBeforeUpdate = user_activityRepository.findAll().size();

        // Update the user_activity
        User_activity updatedUser_activity = new User_activity();
        updatedUser_activity.setId(user_activity.getId());
        updatedUser_activity.setType(UPDATED_TYPE);
        updatedUser_activity.setLiked(UPDATED_LIKED);
        updatedUser_activity.setShared(UPDATED_SHARED);
        updatedUser_activity.setDateLiked(UPDATED_DATE_LIKED);
        updatedUser_activity.setSharedDate(UPDATED_SHARED_DATE);
        updatedUser_activity.setUploadDate(UPDATED_UPLOAD_DATE);
        updatedUser_activity.setCreatedDate(UPDATED_CREATED_DATE);

        restUser_activityMockMvc.perform(put("/api/user-activities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedUser_activity)))
                .andExpect(status().isOk());

        // Validate the User_activity in the database
        List<User_activity> user_activities = user_activityRepository.findAll();
        assertThat(user_activities).hasSize(databaseSizeBeforeUpdate);
        User_activity testUser_activity = user_activities.get(user_activities.size() - 1);
        assertThat(testUser_activity.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testUser_activity.isLiked()).isEqualTo(UPDATED_LIKED);
        assertThat(testUser_activity.isShared()).isEqualTo(UPDATED_SHARED);
        assertThat(testUser_activity.getDateLiked()).isEqualTo(UPDATED_DATE_LIKED);
        assertThat(testUser_activity.getSharedDate()).isEqualTo(UPDATED_SHARED_DATE);
        assertThat(testUser_activity.getUploadDate()).isEqualTo(UPDATED_UPLOAD_DATE);
        assertThat(testUser_activity.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);

        // Validate the User_activity in ElasticSearch
        User_activity user_activityEs = user_activitySearchRepository.findOne(testUser_activity.getId());
        assertThat(user_activityEs).isEqualToComparingFieldByField(testUser_activity);
    }

    @Test
    @Transactional
    public void deleteUser_activity() throws Exception {
        // Initialize the database
        user_activityRepository.saveAndFlush(user_activity);
        user_activitySearchRepository.save(user_activity);
        int databaseSizeBeforeDelete = user_activityRepository.findAll().size();

        // Get the user_activity
        restUser_activityMockMvc.perform(delete("/api/user-activities/{id}", user_activity.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean user_activityExistsInEs = user_activitySearchRepository.exists(user_activity.getId());
        assertThat(user_activityExistsInEs).isFalse();

        // Validate the database is empty
        List<User_activity> user_activities = user_activityRepository.findAll();
        assertThat(user_activities).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchUser_activity() throws Exception {
        // Initialize the database
        user_activityRepository.saveAndFlush(user_activity);
        user_activitySearchRepository.save(user_activity);

        // Search the user_activity
        restUser_activityMockMvc.perform(get("/api/_search/user-activities?query=id:" + user_activity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(user_activity.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].liked").value(hasItem(DEFAULT_LIKED.booleanValue())))
            .andExpect(jsonPath("$.[*].shared").value(hasItem(DEFAULT_SHARED.booleanValue())))
            .andExpect(jsonPath("$.[*].dateLiked").value(hasItem(DEFAULT_DATE_LIKED_STR)))
            .andExpect(jsonPath("$.[*].sharedDate").value(hasItem(DEFAULT_SHARED_DATE_STR)))
            .andExpect(jsonPath("$.[*].uploadDate").value(hasItem(DEFAULT_UPLOAD_DATE_STR)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE_STR)));
    }
}
