package com.catalinms.fileupload.web.rest;

import com.catalinms.fileupload.FileuploadApp;

import com.catalinms.fileupload.domain.FileContent;
import com.catalinms.fileupload.repository.FileContentRepository;
import com.catalinms.fileupload.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;


import static com.catalinms.fileupload.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the FileContentResource REST controller.
 *
 * @see FileContentResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileuploadApp.class)
public class FileContentResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_CONTENT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_CONTENT = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_CONTENT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_CONTENT_CONTENT_TYPE = "image/png";

    @Autowired
    private FileContentRepository fileContentRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restFileContentMockMvc;

    private FileContent fileContent;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FileContentResource fileContentResource = new FileContentResource(fileContentRepository);
        this.restFileContentMockMvc = MockMvcBuilders.standaloneSetup(fileContentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FileContent createEntity(EntityManager em) {
        FileContent fileContent = new FileContent()
            .name(DEFAULT_NAME)
            .content(DEFAULT_CONTENT)
            .contentContentType(DEFAULT_CONTENT_CONTENT_TYPE);
        return fileContent;
    }

    @Before
    public void initTest() {
        fileContent = createEntity(em);
    }

    @Test
    @Transactional
    public void createFileContent() throws Exception {
        int databaseSizeBeforeCreate = fileContentRepository.findAll().size();

        // Create the FileContent
        restFileContentMockMvc.perform(post("/api/file-contents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileContent)))
            .andExpect(status().isCreated());

        // Validate the FileContent in the database
        List<FileContent> fileContentList = fileContentRepository.findAll();
        assertThat(fileContentList).hasSize(databaseSizeBeforeCreate + 1);
        FileContent testFileContent = fileContentList.get(fileContentList.size() - 1);
        assertThat(testFileContent.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFileContent.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testFileContent.getContentContentType()).isEqualTo(DEFAULT_CONTENT_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createFileContentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = fileContentRepository.findAll().size();

        // Create the FileContent with an existing ID
        fileContent.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFileContentMockMvc.perform(post("/api/file-contents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileContent)))
            .andExpect(status().isBadRequest());

        // Validate the FileContent in the database
        List<FileContent> fileContentList = fileContentRepository.findAll();
        assertThat(fileContentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = fileContentRepository.findAll().size();
        // set the field null
        fileContent.setName(null);

        // Create the FileContent, which fails.

        restFileContentMockMvc.perform(post("/api/file-contents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileContent)))
            .andExpect(status().isBadRequest());

        List<FileContent> fileContentList = fileContentRepository.findAll();
        assertThat(fileContentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFileContents() throws Exception {
        // Initialize the database
        fileContentRepository.saveAndFlush(fileContent);

        // Get all the fileContentList
        restFileContentMockMvc.perform(get("/api/file-contents?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fileContent.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].contentContentType").value(hasItem(DEFAULT_CONTENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(Base64Utils.encodeToString(DEFAULT_CONTENT))));
    }
    
    @Test
    @Transactional
    public void getFileContent() throws Exception {
        // Initialize the database
        fileContentRepository.saveAndFlush(fileContent);

        // Get the fileContent
        restFileContentMockMvc.perform(get("/api/file-contents/{id}", fileContent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(fileContent.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.contentContentType").value(DEFAULT_CONTENT_CONTENT_TYPE))
            .andExpect(jsonPath("$.content").value(Base64Utils.encodeToString(DEFAULT_CONTENT)));
    }

    @Test
    @Transactional
    public void getNonExistingFileContent() throws Exception {
        // Get the fileContent
        restFileContentMockMvc.perform(get("/api/file-contents/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFileContent() throws Exception {
        // Initialize the database
        fileContentRepository.saveAndFlush(fileContent);

        int databaseSizeBeforeUpdate = fileContentRepository.findAll().size();

        // Update the fileContent
        FileContent updatedFileContent = fileContentRepository.findById(fileContent.getId()).get();
        // Disconnect from session so that the updates on updatedFileContent are not directly saved in db
        em.detach(updatedFileContent);
        updatedFileContent
            .name(UPDATED_NAME)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE);

        restFileContentMockMvc.perform(put("/api/file-contents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedFileContent)))
            .andExpect(status().isOk());

        // Validate the FileContent in the database
        List<FileContent> fileContentList = fileContentRepository.findAll();
        assertThat(fileContentList).hasSize(databaseSizeBeforeUpdate);
        FileContent testFileContent = fileContentList.get(fileContentList.size() - 1);
        assertThat(testFileContent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFileContent.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testFileContent.getContentContentType()).isEqualTo(UPDATED_CONTENT_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingFileContent() throws Exception {
        int databaseSizeBeforeUpdate = fileContentRepository.findAll().size();

        // Create the FileContent

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFileContentMockMvc.perform(put("/api/file-contents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fileContent)))
            .andExpect(status().isBadRequest());

        // Validate the FileContent in the database
        List<FileContent> fileContentList = fileContentRepository.findAll();
        assertThat(fileContentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteFileContent() throws Exception {
        // Initialize the database
        fileContentRepository.saveAndFlush(fileContent);

        int databaseSizeBeforeDelete = fileContentRepository.findAll().size();

        // Get the fileContent
        restFileContentMockMvc.perform(delete("/api/file-contents/{id}", fileContent.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<FileContent> fileContentList = fileContentRepository.findAll();
        assertThat(fileContentList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileContent.class);
        FileContent fileContent1 = new FileContent();
        fileContent1.setId(1L);
        FileContent fileContent2 = new FileContent();
        fileContent2.setId(fileContent1.getId());
        assertThat(fileContent1).isEqualTo(fileContent2);
        fileContent2.setId(2L);
        assertThat(fileContent1).isNotEqualTo(fileContent2);
        fileContent1.setId(null);
        assertThat(fileContent1).isNotEqualTo(fileContent2);
    }
}
