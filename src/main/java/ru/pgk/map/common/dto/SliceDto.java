package ru.pgk.map.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@AllArgsConstructor
public class SliceDto<T> {
    private List<T> content;

    public static <T> SliceDto<T> fromPage(Page<T> page) {
        return new SliceDto<>(page.getContent());
    }

    public static <T> SliceDto<T> fromSlice(Slice<T> slice) {
        return new SliceDto<>(slice.getContent());
    }
}
