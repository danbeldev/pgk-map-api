package ru.pgk.map.features.border.services;

import ru.pgk.map.features.border.dto.BorderEntityDto;
import ru.pgk.map.features.field.entities.FieldEntity;

import java.util.List;

public interface BorderService {

    void saveAll(FieldEntity field, List<BorderEntityDto> borders);
}
