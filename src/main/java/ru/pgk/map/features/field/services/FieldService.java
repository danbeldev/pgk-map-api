package ru.pgk.map.features.field.services;

import org.springframework.data.domain.Slice;
import ru.pgk.map.features.field.entities.FieldEntity;

import java.time.LocalDate;

public interface FieldService {

    Slice<FieldEntity> getAll(String search, Integer offset, Integer limit);

    FieldEntity getById(Long id);

    void update(Long id, String name, LocalDate date);

    void deleteById(Long id);
}
