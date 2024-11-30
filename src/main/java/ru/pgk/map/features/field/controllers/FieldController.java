package ru.pgk.map.features.field.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pgk.map.common.dto.SliceDto;
import ru.pgk.map.common.exceptions.BadRequestException;
import ru.pgk.map.features.border.dto.BorderEntityDto;
import ru.pgk.map.features.field.dto.FieldEntityDetailsDto;
import ru.pgk.map.features.field.dto.FieldEntityDto;
import ru.pgk.map.features.field.mappers.FieldEntityDetailsMapper;
import ru.pgk.map.features.field.mappers.FieldEntityMapper;
import ru.pgk.map.features.field.services.FieldResourceService;
import ru.pgk.map.features.field.services.FieldService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fields")
public class FieldController {

    public final FieldService fieldService;
    private final FieldResourceService fieldResourceService;

    private final FieldEntityMapper fieldEntityMapper;
    private final FieldEntityDetailsMapper fieldEntityDetailsMapper;

    @GetMapping
    public SliceDto<FieldEntityDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer limit
    ) {
        return SliceDto.fromSlice(fieldService.getAll(search, offset, limit).map(fieldEntityMapper::toDto));
    }

    @GetMapping("{id}")
    public FieldEntityDetailsDto getById(@PathVariable Long id) {
        return fieldEntityDetailsMapper.toDto(fieldService.getById(id));
    }

    @PutMapping("{id}")
    public void update(
            @PathVariable Long id,
            @RequestParam String name
    ) {
        fieldService.update(id, name);
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable Long id) {
        fieldService.deleteById(id);
    }

    @PostMapping(value = "/upload-zip-file", consumes = "multipart/form-data")
    public long uploadZipFile(
            @RequestParam String name,
            @RequestParam LocalDate date,
            @RequestParam("borders") List<Double> bordersDouble,
            @RequestParam("file") MultipartFile file
    ) {
        if (bordersDouble.size() % 2 != 0) {
            throw new BadRequestException("Список границ должен содержать четное количество координат");
        }

        List<BorderEntityDto> borders = new ArrayList<>();

        for (int i = 0; i < bordersDouble.size(); i += 2) {
            Double latitude = bordersDouble.get(i);
            Double longitude = bordersDouble.get(i + 1);
            borders.add(new BorderEntityDto(latitude, longitude));
        }
        return fieldResourceService.parseAndSave(name, date, borders, file);
    }

    @GetMapping("resources/{folder-id}/images/{name}")
    public ResponseEntity<byte[]> getImage(
            @PathVariable("folder-id") String folderId,
            @PathVariable("name") String name
    ) {
        return fieldResourceService.getImage(folderId, name);
    }
}
