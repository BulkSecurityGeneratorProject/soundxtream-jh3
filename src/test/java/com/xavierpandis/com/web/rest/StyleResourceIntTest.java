package com.xavierpandis.com.web.rest;

import com.xavierpandis.com.Soundxtream3App;
import com.xavierpandis.com.domain.Style;
import com.xavierpandis.com.repository.StyleRepository;
import com.xavierpandis.com.repository.search.StyleSearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the StyleResource REST controller.
 *
 * @see StyleResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Soundxtream3App.class)
@WebAppConfiguration
@IntegrationTest
public class StyleResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    @Inject
    private StyleRepository styleRepository;

    @Inject
    private StyleSearchRepository styleSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restStyleMockMvc;

    private Style style;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StyleResource styleResource = new StyleResource();
        ReflectionTestUtils.setField(styleResource, "styleSearchRepository", styleSearchRepository);
        ReflectionTestUtils.setField(styleResource, "styleRepository", styleRepository);
        this.restStyleMockMvc = MockMvcBuilders.standaloneSetup(styleResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        styleSearchRepository.deleteAll();
        style = new Style();
        style.setName(DEFAULT_NAME);
        style.setImage(DEFAULT_IMAGE);
        style.setImageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createStyle() throws Exception {
        int databaseSizeBeforeCreate = styleRepository.findAll().size();

        // Create the Style

        restStyleMockMvc.perform(post("/api/styles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(style)))
                .andExpect(status().isCreated());

        // Validate the Style in the database
        List<Style> styles = styleRepository.findAll();
        assertThat(styles).hasSize(databaseSizeBeforeCreate + 1);
        Style testStyle = styles.get(styles.size() - 1);
        assertThat(testStyle.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStyle.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testStyle.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);

        // Validate the Style in ElasticSearch
        Style styleEs = styleSearchRepository.findOne(testStyle.getId());
        assertThat(styleEs).isEqualToComparingFieldByField(testStyle);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = styleRepository.findAll().size();
        // set the field null
        style.setName(null);

        // Create the Style, which fails.

        restStyleMockMvc.perform(post("/api/styles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(style)))
                .andExpect(status().isBadRequest());

        List<Style> styles = styleRepository.findAll();
        assertThat(styles).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStyles() throws Exception {
        // Initialize the database
        styleRepository.saveAndFlush(style);

        // Get all the styles
        restStyleMockMvc.perform(get("/api/styles?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(style.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    public void getStyle() throws Exception {
        // Initialize the database
        styleRepository.saveAndFlush(style);

        // Get the style
        restStyleMockMvc.perform(get("/api/styles/{id}", style.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(style.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    public void getNonExistingStyle() throws Exception {
        // Get the style
        restStyleMockMvc.perform(get("/api/styles/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStyle() throws Exception {
        // Initialize the database
        styleRepository.saveAndFlush(style);
        styleSearchRepository.save(style);
        int databaseSizeBeforeUpdate = styleRepository.findAll().size();

        // Update the style
        Style updatedStyle = new Style();
        updatedStyle.setId(style.getId());
        updatedStyle.setName(UPDATED_NAME);
        updatedStyle.setImage(UPDATED_IMAGE);
        updatedStyle.setImageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restStyleMockMvc.perform(put("/api/styles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedStyle)))
                .andExpect(status().isOk());

        // Validate the Style in the database
        List<Style> styles = styleRepository.findAll();
        assertThat(styles).hasSize(databaseSizeBeforeUpdate);
        Style testStyle = styles.get(styles.size() - 1);
        assertThat(testStyle.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStyle.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testStyle.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);

        // Validate the Style in ElasticSearch
        Style styleEs = styleSearchRepository.findOne(testStyle.getId());
        assertThat(styleEs).isEqualToComparingFieldByField(testStyle);
    }

    @Test
    @Transactional
    public void deleteStyle() throws Exception {
        // Initialize the database
        styleRepository.saveAndFlush(style);
        styleSearchRepository.save(style);
        int databaseSizeBeforeDelete = styleRepository.findAll().size();

        // Get the style
        restStyleMockMvc.perform(delete("/api/styles/{id}", style.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean styleExistsInEs = styleSearchRepository.exists(style.getId());
        assertThat(styleExistsInEs).isFalse();

        // Validate the database is empty
        List<Style> styles = styleRepository.findAll();
        assertThat(styles).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchStyle() throws Exception {
        // Initialize the database
        styleRepository.saveAndFlush(style);
        styleSearchRepository.save(style);

        // Search the style
        restStyleMockMvc.perform(get("/api/_search/styles?query=id:" + style.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(style.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }
}
