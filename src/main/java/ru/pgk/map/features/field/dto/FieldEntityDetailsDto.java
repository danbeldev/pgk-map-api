package ru.pgk.map.features.field.dto;

import ru.pgk.map.features.border.dto.BorderEntityDto;
import ru.pgk.map.features.point.dto.PointEntityDto;

import java.time.LocalDate;
import java.util.Collection;

/**
 * DTO for {@link ru.pgk.map.features.field.entities.FieldEntity}
 */
public record FieldEntityDetailsDto(
        Long id,
        String name,
        LocalDate date,
        Collection<PointEntityDto> points,
        Collection<BorderEntityDto> borders
) {}