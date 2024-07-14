package com.manbo.common.util.dto.query;

import com.manbo.common.util.constant.StringConst;
import com.manbo.common.util.enums.QueryOperatorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Manbo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryRuleDTO {
    private String field;
    private QueryOperatorEnum op;
    private Object data;
    private String delimiter;

    public QueryRuleDTO(final String field, final Object data) {
        this(field, QueryOperatorEnum.EQUAL, data);
    }

    public QueryRuleDTO(final String field, final QueryOperatorEnum op, final Object data) {
        this.field = field;
        this.op = op;
        this.data = data;
    }

    public String getDelimiter() {
        return StringUtils.defaultIfBlank(delimiter, StringConst.COMMA);
    }
}
