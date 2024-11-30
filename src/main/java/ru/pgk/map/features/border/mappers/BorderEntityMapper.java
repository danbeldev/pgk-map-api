package ru.pgk.map.features.border.mappers;

import org.mapstruct.*;
import ru.pgk.map.features.border.dto.BorderEntityDto;
import ru.pgk.map.features.border.entities.BorderEntity;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BorderEntityMapper {
    BorderEntity toEntity(BorderEntityDto borderEntityDto);

    BorderEntityDto toDto(BorderEntity borderEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BorderEntity partialUpdate(BorderEntityDto borderEntityDto, @MappingTarget BorderEntity borderEntity);
}