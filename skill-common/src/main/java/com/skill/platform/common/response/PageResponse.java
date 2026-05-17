package com.skill.platform.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Pagination response wrapper.
 *
 * @param <T> the type of items in the page
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private long total;
    private int page;
    private int pageSize;
    private List<T> items;

    /**
     * Create a PageResponse from a Spring Data {@link Page}.
     *
     * @param page the Spring Data page
     * @param <T>  the item type
     * @return a PageResponse wrapping the page content and metadata
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .total(page.getTotalElements())
                .page(page.getNumber() + 1)
                .pageSize(page.getSize())
                .items(page.getContent())
                .build();
    }

    /**
     * Create a PageResponse with explicit values.
     *
     * @param total    total number of elements
     * @param page     current page number (1-based)
     * @param pageSize number of items per page
     * @param items    the list of items
     * @param <T>      the item type
     * @return a PageResponse with the given values
     */
    public static <T> PageResponse<T> of(long total, int page, int pageSize, List<T> items) {
        return PageResponse.<T>builder()
                .total(total)
                .page(page)
                .pageSize(pageSize)
                .items(items)
                .build();
    }
}
