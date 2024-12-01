package ru.pgk.map.features.border.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pgk.map.features.border.dto.BorderEntityDto;
import ru.pgk.map.features.border.entities.BorderEntity;
import ru.pgk.map.features.border.repositories.BorderRepository;
import ru.pgk.map.features.field.entities.FieldEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BorderServiceImpl implements BorderService {

    private final BorderRepository borderRepository;

    @Override
    @Transactional
    public void saveAll(FieldEntity field, List<BorderEntityDto> borders) {
        var entities = borders.stream().map(dto -> {
            var entity = new BorderEntity();
            entity.setField(field);
            entity.setLatitude(dto.latitude());
            entity.setLongitude(dto.longitude());
            return entity;
        }).toList();

        borderRepository.saveAll(entities);
    }
}
