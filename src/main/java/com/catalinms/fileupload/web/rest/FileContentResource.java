package com.catalinms.fileupload.web.rest;

import com.catalinms.fileupload.domain.FileContent;
import com.catalinms.fileupload.repository.FileContentRepository;
import com.catalinms.fileupload.web.rest.errors.BadRequestAlertException;
import com.catalinms.fileupload.web.rest.errors.InternalServerErrorException;
import com.catalinms.fileupload.web.rest.util.HeaderUtil;
import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing FileContent.
 */
@RestController
@RequestMapping("/api")
public class FileContentResource {

    private final Logger log = LoggerFactory.getLogger(FileContentResource.class);

    private static final String ENTITY_NAME = "fileContent";

    private final FileContentRepository fileContentRepository;

    public FileContentResource(FileContentRepository fileContentRepository) {
        this.fileContentRepository = fileContentRepository;
    }

    /**
     * POST  /file-contents : Create a new fileContent.
     *
     * @param fileContent the fileContent to update
     * @return the ResponseEntity with status 201 (Created) and with body the new fileContent, or with status 400 (Bad Request) if the fileContent has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/file-contents")
    @Timed
    public ResponseEntity<FileContent> createFileContent(@RequestParam("file") MultipartFile fileContent) throws URISyntaxException {
        log.debug("REST request to save FileContent : {}", fileContent);

        // todo validate only csv if required
        FileContent result = saveMultipartFile(fileContent);

        return ResponseEntity.created(new URI("/api/file-contents/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /file-contents : Updates an existing fileContent.
     *
     * @param fileContent the fileContent to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated fileContent,
     * or with status 400 (Bad Request) if the fileContent is not valid,
     * or with status 500 (Internal Server Error) if the fileContent couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/file-contents")
    @Timed
    public ResponseEntity<FileContent> updateFileContent(@Valid @RequestBody FileContent fileContent) throws URISyntaxException {
        log.debug("REST request to update FileContent : {}", fileContent);
        if (fileContent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        FileContent result = fileContentRepository.save(fileContent);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, fileContent.getId().toString()))
            .body(result);
    }

    /**
     * GET  /file-contents : get all the fileContents.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of fileContents in body
     */
    @GetMapping("/file-contents")
    @Timed
    public List<FileContent> getAllFileContents() {
        log.debug("REST request to get all FileContents");
        return fileContentRepository.findAll();
    }

    /**
     * GET  /file-contents/:id : get the "id" fileContent.
     *
     * @param id the id of the fileContent to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the fileContent, or with status 404 (Not Found)
     */
    @GetMapping("/file-contents/{id}")
    @Timed
    public ResponseEntity<FileContent> getFileContent(@PathVariable Long id) {
        log.debug("REST request to get FileContent : {}", id);
        Optional<FileContent> fileContent = fileContentRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(fileContent);
    }

    /**
     * DELETE  /file-contents/:id : delete the "id" fileContent.
     *
     * @param id the id of the fileContent to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/file-contents/{id}")
    @Timed
    public ResponseEntity<Void> deleteFileContent(@PathVariable Long id) {
        log.debug("REST request to delete FileContent : {}", id);

        fileContentRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    private FileContent saveMultipartFile(MultipartFile fileContent) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(fileContent.getOriginalFilename()));

        try {
            FileContent file = new FileContent(fileName, fileContent.getBytes(), fileContent.getContentType());

            return fileContentRepository.save(file);
        } catch (IOException e) {
            throw new InternalServerErrorException("Failed to upload file");
        }
    }
}
