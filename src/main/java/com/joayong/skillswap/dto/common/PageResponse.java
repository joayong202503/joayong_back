package com.joayong.skillswap.dto.common;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class PageResponse<T> {
    private long totalCount;        // 전체 데이터 개수
    private int totalPages;         // 전체 페이지 수
    private int currentPage;        // 현재 페이지
    private boolean hasNext;        // 다음 페이지가 있는지 여부
    private boolean hasPrevious;    // 이전 페이지가 있는지 여부
    private int pageSize;           // 페이지 당 항목 수
    private List<T> data;           // 실제 데이터 리스트

    // 생성자, getter, setter 생략
}
