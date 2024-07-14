package com.manbo.common.util.dto.query;

import com.manbo.common.util.enums.QueryOrderDirectionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Manbo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryOrderDTO {
    private String field;
    private QueryOrderDirectionEnum direction;
}
