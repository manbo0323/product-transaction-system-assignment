package com.manbo.common.util.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Manbo
 */
public enum QueryOrderDirectionEnum {
    /**
     * asc
     */
    ASC,
    /**
     * desc
     */
    DESC
    ;

    public static QueryOrderDirectionEnum lookup(final String value) {
        if(StringUtils.isNotBlank(value)) {
            for(QueryOrderDirectionEnum direction : values()) {
                if(direction.name().equalsIgnoreCase(value)) {
                    return direction;
                }
            }
        }
        return null;
    }
}
