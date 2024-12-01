package ru.pgk.map.features.field.mappers;

import org.mapstruct.*;
import ru.pgk.map.features.border.mappers.BorderEntityMapper;
import ru.pgk.map.features.field.dto.FieldEntityDetailsDto;
import ru.pgk.map.features.field.entities.FieldEntity;
import ru.pgk.map.features.point.mappers.PointEntityMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {PointEntityMapper.class, BorderEntityMapper.class})
public interface FieldEntityDetailsMapper {
    FieldEntity toEntity(FieldEntityDetailsDto fieldEntityDetailsDto);

    @AfterMapping
    default void linkPoints(@MappingTarget FieldEntity fieldEntity) {
        fieldEntity.getPoints().forEach(point -> point.setField(fieldEntity));
    }

    FieldEntityDetailsDto toDto(FieldEntity fieldEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    FieldEntity partialUpdate(FieldEntityDetailsDto fieldEntityDetailsDto, @MappingTarget FieldEntity fieldEntity);
}