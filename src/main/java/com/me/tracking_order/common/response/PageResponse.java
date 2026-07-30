package com.me.tracking_order.common.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Getter
@Builder
public class PageResponse<T> {

    private List<T> content;

    private int page;
    private int size;

    private long totalElements;
    private int totalPages;

    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <S, T> PageResponse<T> from(
            Page<S> source,
            Function<S, T> mapper
    ) {
        List<T> content = source
                .getContent()
                .stream()
                .map(mapper)
                .toList();

        return PageResponse.<T>builder()
                .content(content)
                .page(source.getNumber()+1)
                .size(source.getSize())
                .totalElements(source.getTotalElements())
                .totalPages(source.getTotalPages())
                .first(source.isFirst())
                .last(source.isLast())
                .hasNext(source.hasNext())
                .hasPrevious(source.hasPrevious())
                .build();
    }
}
