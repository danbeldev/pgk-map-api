package ru.pgk.map.features.point.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pgk.map.features.point.entities.PointEntity;

public interface PointRepository extends JpaRepository<PointEntity, Long> {}
