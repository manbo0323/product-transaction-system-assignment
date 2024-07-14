package com.manbo.common.util.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Manbo
 */
public enum QueryGroupOperatorEnum {

    /**
     * and
     */
    AND,
    /**
     * or
     */
    OR
    ;

    public static QueryGroupOperatorEnum lookup(final String value) {
        if(StringUtils.isNotBlank(value)) {
            for(QueryGroupOperatorEnum operator : values()) {
                if(operator.name().equalsIgnoreCase(value)) {
                    return operator;
                }
            }
        }
        return null;
    }
}
