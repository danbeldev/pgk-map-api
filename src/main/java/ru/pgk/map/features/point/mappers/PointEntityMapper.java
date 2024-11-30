package ru.pgk.map.features.point.mappers;

import org.mapstruct.*;
import ru.pgk.map.features.point.dto.PointEntityDto;
import ru.pgk.map.features.point.entities.PointEntity;

import static ru.pgk.map.common.BaseConstants.FIELD_RESOURCES_URL;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PointEntityMapper {

    @Mapping(target = "image", expression = "java(getImageUrl(pointEntity))")
    @Mapping(expression = "java(pointEntity.getVRef())", target = "vRef")
    PointEntityDto toDto(PointEntity pointEntity);

    PointEntity toEntity(PointEntityDto pointEntityDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PointEntity partialUpdate(PointEntityDto pointEntityDto, @MappingTarget PointEntity pointEntity);

    default String getImageUrl(PointEntity pointEntity) {
        return FIELD_RESOURCES_URL + "/" + pointEntity.getField().getFolderId() + "/" + "images" + "/" + pointEntity.getName();
    }
}