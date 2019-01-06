package com.catalinms.fileupload.service.impl;

import com.catalinms.fileupload.service.FileService;
import com.catalinms.fileupload.domain.File;
import com.catalinms.fileupload.repository.FileRepository;
import com.catalinms.fileupload.service.dto.FileDTO;
import com.catalinms.fileupload.service.mapper.FileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing File.
 */
@Service
@Transactional
public class FileServiceImpl implements FileService {

    private final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    private final FileRepository fileRepository;

    private final FileMapper fileMapper;

    public FileServiceImpl(FileRepository fileRepository, FileMapper fileMapper) {
        this.fileRepository = fileRepository;
        this.fileMapper = fileMapper;
    }

    /**
     * Save a file.
     *
     * @param fileDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public FileDTO save(FileDTO fileDTO) {
        log.debug("Request to save File : {}", fileDTO);

        File file = fileMapper.toEntity(fileDTO);
        file = fileRepository.save(file);
        return fileMapper.toDto(file);
    }

    /**
     * Get all the files.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<FileDTO> findAll() {
        log.debug("Request to get all Files");
        return fileRepository.findAll().stream()
            .map(fileMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one file by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<FileDTO> findOne(Long id) {
        log.debug("Request to get File : {}", id);
        return fileRepository.findById(id)
            .map(fileMapper::toDto);
    }

    /**
     * Delete the file by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete File : {}", id);
        fileRepository.deleteById(id);
    }
}
