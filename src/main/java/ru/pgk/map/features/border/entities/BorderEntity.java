package ru.pgk.map.features.border.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.pgk.map.features.field.entities.FieldEntity;

@Getter
@Setter
@Entity(name = "borders")
public class BorderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;
    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    private FieldEntity field;
}
