package ru.pgk.map.features.point.entities;

import jakarta.persistence.*;
import lombok.*;
import ru.pgk.map.features.field.entities.FieldEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Entity(name = "points")
@AllArgsConstructor
public class PointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double latitude;
    private Double longitude;

    @Column(name = "speed_GPS")
    private Double speedGPS;
    @Column(name = "rotate_GPS")
    private Double rotateGPS;
    private Double altitude;
    private Double roll;
    private Double pitch;
    private Integer rotate;
    @Column(name = "v_ref")
    private Double vRef;
    @Column(name = "time_fly")
    private Integer timeFly;
    @Column(name = "altitude_PVD")
    private Double altitudePVD;
    @Column(name = "speed_PVD")
    private Double speedPVD;
    @Column(name = "num_Foto1")
    private Integer numFoto1;
    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private FieldEntity field;
}
