package com.manbo.common.util.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author Manbo
 */
@Data
@Builder
@AllArgsConstructor
public class PageDTO<T> implements Serializable {

    private Integer pageNumber;

    private Integer pageSize;

    private List<T> list;

    private Long totalCount;

    public PageDTO() {
        this.pageNumber = 0;
        this.pageSize = 10;
        this.totalCount = 0L;
        this.list = Collections.emptyList();
    }

}
