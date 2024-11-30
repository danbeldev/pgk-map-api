package ru.pgk.map.features.border.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pgk.map.features.border.entities.BorderEntity;

public interface BorderRepository extends JpaRepository<BorderEntity, Long> {}
