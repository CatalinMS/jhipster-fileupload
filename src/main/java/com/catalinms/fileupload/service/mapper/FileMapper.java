package com.catalinms.fileupload.service.mapper;

import com.catalinms.fileupload.domain.*;
import com.catalinms.fileupload.service.dto.FileDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity File and its DTO FileDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FileMapper extends EntityMapper<FileDTO, File> {



    default File fromId(Long id) {
        if (id == null) {
            return null;
        }
        File file = new File();
        file.setId(id);
        return file;
    }
}
