package ru.pgk.map.features.field.services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.pgk.map.features.border.dto.BorderEntityDto;

import java.time.LocalDate;
import java.util.List;

public interface FieldResourceService {

    long parseAndSave(String name, LocalDate date, List<BorderEntityDto> borders, MultipartFile file);

    void deleteByFolderId(String folderId);

    ResponseEntity<byte[]> getImage(String folderId, String name);
}
