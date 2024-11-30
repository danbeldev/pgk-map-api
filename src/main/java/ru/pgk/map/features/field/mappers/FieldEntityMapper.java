package ru.pgk.map.features.field.mappers;

import org.mapstruct.*;
import ru.pgk.map.features.border.mappers.BorderEntityMapper;
import ru.pgk.map.features.field.dto.FieldEntityDto;
import ru.pgk.map.features.field.entities.FieldEntity;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {BorderEntityMapper.class})
public interface FieldEntityMapper {
    FieldEntity toEntity(FieldEntityDto fieldDto);

    FieldEntityDto toDto(FieldEntity fieldEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    FieldEntity partialUpdate(FieldEntityDto fieldDto, @MappingTarget FieldEntity fieldEntity);
}