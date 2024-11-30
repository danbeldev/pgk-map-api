package ru.pgk.map.features.field.dto;

import ru.pgk.map.features.border.dto.BorderEntityDto;

import java.time.LocalDate;
import java.util.Collection;

public record FieldEntityDto(Long id, String name, LocalDate date, Collection<BorderEntityDto> borders) {}