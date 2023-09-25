package net.example.report.promotionanalysis.model.dto;

import lombok.Value;
import org.springframework.data.domain.Page;

import java.util.List;

@Value
public class PageResponse<T> {
    List<T> content;
    Metadata metadata;

    public static <T> PageResponse<T> of(Page<T> page) {
        var metadata = new Metadata(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements());
        return new PageResponse<>(page.getContent(), metadata);
    }

    public static <T> PageResponse<T> of(List<T> content, int pageNumber, int pageSize, long totalElements) {
        var metadata = new Metadata(
                pageNumber,
                pageSize,
                totalElements);
        return new PageResponse<>(content, metadata);
    }

    @Value
    public static class Metadata {
        int page;
        int size;
        long totalElements;
    }
}
