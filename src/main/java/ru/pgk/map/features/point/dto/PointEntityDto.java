package ru.pgk.map.features.point.dto;

import java.time.LocalDateTime;

/**
 * DTO for {@link ru.pgk.map.features.point.entities.PointEntity}
 */
public record PointEntityDto(Long id, String name, Double latitude, Double longitude, Double speedGPS, Double rotateGPS,
                             Double altitude, Double roll, Double pitch, Integer rotate, Double vRef, Integer timeFly,
                             Double altitudePVD, Double speedPVD, Integer numFoto1, String image, LocalDateTime dateTime) {
}