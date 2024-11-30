package ru.pgk.map.features.field.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.pgk.map.features.border.entities.BorderEntity;
import ru.pgk.map.features.point.entities.PointEntity;

import java.time.LocalDate;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "fields")
public class FieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String folderId;
    private LocalDate date;

    @OneToMany(mappedBy = "field", fetch = FetchType.LAZY)
    private Collection<PointEntity> points;

    @OneToMany(mappedBy = "field", fetch = FetchType.LAZY)
    private Collection<BorderEntity> borders;
}
