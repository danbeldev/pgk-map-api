package ru.pgk.map.features.field.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.pgk.map.features.field.entities.FieldEntity;

public interface FieldRepository extends JpaRepository<FieldEntity, Long> {

    @Query("SELECT f FROM fields f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Slice<FieldEntity> search(String q, Pageable pageable);

    @Query("SELECT f FROM fields f")
    Slice<FieldEntity> findAllSlice(Pageable pageable);
}
