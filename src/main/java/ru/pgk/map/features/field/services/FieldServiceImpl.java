package ru.pgk.map.features.field.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pgk.map.common.exceptions.ResourceNotFoundException;
import ru.pgk.map.features.border.repositories.BorderRepository;
import ru.pgk.map.features.field.entities.FieldEntity;
import ru.pgk.map.features.field.repositories.FieldRepository;
import ru.pgk.map.features.point.repositories.PointRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FieldServiceImpl implements FieldService {

    private final FieldRepository fieldRepository;

    private final FieldResourceService fieldParserService;
    private final BorderRepository borderRepository;
    private final PointRepository pointRepository;

    @Override
    @Transactional(readOnly = true)
    public Slice<FieldEntity> getAll(String search, Integer offset, Integer limit) {
        var pageable = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "id"));

        if (search != null && !search.isBlank())
            return fieldRepository.search(search, pageable);

        return fieldRepository.findAllSlice(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public FieldEntity getById(Long id) {
        return fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field not found"));
    }

    @Override
    @Transactional
    public void update(Long id, String name, LocalDate date) {
        FieldEntity field = getById(id);
        field.setName(name);
        field.setDate(date);
        fieldRepository.save(field);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        FieldEntity field = getById(id);
        borderRepository.deleteAll(field.getBorders());
        pointRepository.deleteAll(field.getPoints());
        fieldRepository.delete(field);
        fieldParserService.deleteByFolderId(field.getFolderId());
    }
}
