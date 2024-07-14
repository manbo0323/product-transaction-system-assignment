package com.manbo.common.util.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.manbo.common.util.dto.query.QueryObj;
import com.manbo.common.util.dto.query.QueryOrderDTO;
import com.manbo.common.util.dto.query.QueryRuleDTO;
import com.manbo.common.util.enums.QueryGroupOperatorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Manbo
 */
@Data
@Builder
@AllArgsConstructor
@FieldNameConstants
public class QueryDTO implements QueryObj {

    private Set<QueryRuleDTO> rules;
    private Integer pageNumber;
    private Integer pageSize;
    private QueryGroupOperatorEnum groupOp;
    private String keyword;
    private boolean ignoreAuth;

    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<QueryOrderDTO> orders;

    public QueryDTO() {
        this.rules = new LinkedHashSet<>();
        this.orders = new LinkedHashSet<>();
        this.pageNumber = 0;
        this.pageSize = 10;
    }

    public QueryDTO(final Integer pageSize) {
        this();
        this.pageSize = pageSize;
    }
}
