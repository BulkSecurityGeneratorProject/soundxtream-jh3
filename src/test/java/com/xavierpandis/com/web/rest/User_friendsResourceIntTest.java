package com.xavierpandis.com.web.rest;

import com.xavierpandis.com.Soundxtream3App;
import com.xavierpandis.com.domain.User_friends;
import com.xavierpandis.com.repository.User_friendsRepository;
import com.xavierpandis.com.repository.search.User_friendsSearchRepository;

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
 * Test class for the User_friendsResource REST controller.
 *
 * @see User_friendsResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Soundxtream3App.class)
@WebAppConfiguration
@IntegrationTest
public class User_friendsResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));


    private static final Boolean DEFAULT_FRIEND = false;
    private static final Boolean UPDATED_FRIEND = true;

    private static final ZonedDateTime DEFAULT_FRIENDSHIP_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_FRIENDSHIP_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_FRIENDSHIP_DATE_STR = dateTimeFormatter.format(DEFAULT_FRIENDSHIP_DATE);

    @Inject
    private User_friendsRepository user_friendsRepository;

    @Inject
    private User_friendsSearchRepository user_friendsSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restUser_friendsMockMvc;

    private User_friends user_friends;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        User_friendsResource user_friendsResource = new User_friendsResource();
        ReflectionTestUtils.setField(user_friendsResource, "user_friendsSearchRepository", user_friendsSearchRepository);
        ReflectionTestUtils.setField(user_friendsResource, "user_friendsRepository", user_friendsRepository);
        this.restUser_friendsMockMvc = MockMvcBuilders.standaloneSetup(user_friendsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        user_friendsSearchRepository.deleteAll();
        user_friends = new User_friends();
        user_friends.setFriend(DEFAULT_FRIEND);
        user_friends.setFriendshipDate(DEFAULT_FRIENDSHIP_DATE);
    }

    @Test
    @Transactional
    public void createUser_friends() throws Exception {
        int databaseSizeBeforeCreate = user_friendsRepository.findAll().size();

        // Create the User_friends

        restUser_friendsMockMvc.perform(post("/api/user-friends")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user_friends)))
                .andExpect(status().isCreated());

        // Validate the User_friends in the database
        List<User_friends> user_friends = user_friendsRepository.findAll();
        assertThat(user_friends).hasSize(databaseSizeBeforeCreate + 1);
        User_friends testUser_friends = user_friends.get(user_friends.size() - 1);
        assertThat(testUser_friends.isFriend()).isEqualTo(DEFAULT_FRIEND);
        assertThat(testUser_friends.getFriendshipDate()).isEqualTo(DEFAULT_FRIENDSHIP_DATE);

        // Validate the User_friends in ElasticSearch
        User_friends user_friendsEs = user_friendsSearchRepository.findOne(testUser_friends.getId());
        assertThat(user_friendsEs).isEqualToComparingFieldByField(testUser_friends);
    }

    @Test
    @Transactional
    public void getAllUser_friends() throws Exception {
        // Initialize the database
        user_friendsRepository.saveAndFlush(user_friends);

        // Get all the user_friends
        restUser_friendsMockMvc.perform(get("/api/user-friends?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(user_friends.getId().intValue())))
                .andExpect(jsonPath("$.[*].friend").value(hasItem(DEFAULT_FRIEND.booleanValue())))
                .andExpect(jsonPath("$.[*].friendshipDate").value(hasItem(DEFAULT_FRIENDSHIP_DATE_STR)));
    }

    @Test
    @Transactional
    public void getUser_friends() throws Exception {
        // Initialize the database
        user_friendsRepository.saveAndFlush(user_friends);

        // Get the user_friends
        restUser_friendsMockMvc.perform(get("/api/user-friends/{id}", user_friends.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(user_friends.getId().intValue()))
            .andExpect(jsonPath("$.friend").value(DEFAULT_FRIEND.booleanValue()))
            .andExpect(jsonPath("$.friendshipDate").value(DEFAULT_FRIENDSHIP_DATE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingUser_friends() throws Exception {
        // Get the user_friends
        restUser_friendsMockMvc.perform(get("/api/user-friends/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUser_friends() throws Exception {
        // Initialize the database
        user_friendsRepository.saveAndFlush(user_friends);
        user_friendsSearchRepository.save(user_friends);
        int databaseSizeBeforeUpdate = user_friendsRepository.findAll().size();

        // Update the user_friends
        User_friends updatedUser_friends = new User_friends();
        updatedUser_friends.setId(user_friends.getId());
        updatedUser_friends.setFriend(UPDATED_FRIEND);
        updatedUser_friends.setFriendshipDate(UPDATED_FRIENDSHIP_DATE);

        restUser_friendsMockMvc.perform(put("/api/user-friends")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedUser_friends)))
                .andExpect(status().isOk());

        // Validate the User_friends in the database
        List<User_friends> user_friends = user_friendsRepository.findAll();
        assertThat(user_friends).hasSize(databaseSizeBeforeUpdate);
        User_friends testUser_friends = user_friends.get(user_friends.size() - 1);
        assertThat(testUser_friends.isFriend()).isEqualTo(UPDATED_FRIEND);
        assertThat(testUser_friends.getFriendshipDate()).isEqualTo(UPDATED_FRIENDSHIP_DATE);

        // Validate the User_friends in ElasticSearch
        User_friends user_friendsEs = user_friendsSearchRepository.findOne(testUser_friends.getId());
        assertThat(user_friendsEs).isEqualToComparingFieldByField(testUser_friends);
    }

    @Test
    @Transactional
    public void deleteUser_friends() throws Exception {
        // Initialize the database
        user_friendsRepository.saveAndFlush(user_friends);
        user_friendsSearchRepository.save(user_friends);
        int databaseSizeBeforeDelete = user_friendsRepository.findAll().size();

        // Get the user_friends
        restUser_friendsMockMvc.perform(delete("/api/user-friends/{id}", user_friends.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean user_friendsExistsInEs = user_friendsSearchRepository.exists(user_friends.getId());
        assertThat(user_friendsExistsInEs).isFalse();

        // Validate the database is empty
        List<User_friends> user_friends = user_friendsRepository.findAll();
        assertThat(user_friends).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchUser_friends() throws Exception {
        // Initialize the database
        user_friendsRepository.saveAndFlush(user_friends);
        user_friendsSearchRepository.save(user_friends);

        // Search the user_friends
        restUser_friendsMockMvc.perform(get("/api/_search/user-friends?query=id:" + user_friends.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(user_friends.getId().intValue())))
            .andExpect(jsonPath("$.[*].friend").value(hasItem(DEFAULT_FRIEND.booleanValue())))
            .andExpect(jsonPath("$.[*].friendshipDate").value(hasItem(DEFAULT_FRIENDSHIP_DATE_STR)));
    }
}
