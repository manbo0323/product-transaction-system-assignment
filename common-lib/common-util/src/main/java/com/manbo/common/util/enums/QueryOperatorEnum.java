package com.manbo.common.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Manbo
 */
@Getter
@RequiredArgsConstructor
public enum QueryOperatorEnum {

    /**
     * contains
     */
    CONTAINS("CN"),

    /**
     * equal
     */
    EQUAL("EQ"),

    /**
     * not equal
     */
    NOT_EQUAL("NE"),

    /**
     * less
     */
    LESS("LT"),

    /**
     * less or equal
     */
    LESS_OR_EQUAL("LE"),

    /**
     * greater
     */
    GREATER("GT"),

    /**
     * greater or equal
     */
    GREATER_OR_EQUAL("GE"),

    /**
     * begins with
     */
    BEGINS_WITH("BW"),

    /**
     * does not begin with
     */
    NOT_BEGIN_WITH("BN"),

    /**
     * is in
     */
    IS_IN("IN"),

    /**
     * is not in
     */
    IS_NOT_IN("NI"),

    /**
     * ends with
     */
    END_WITH("EW"),

    /**
     * does not end with
     */
    NOT_END_WITH("EN"),

    /**
     * does not contain
     */
    NOT_CONTAINS("NC"),

    /**
     * is null
     */
    IS_NULL("NU"),

    /**
     * is not null
     */
    IS_NOT_NULL("NN"),

    /**
     * between
     */
    BETWEEN("BE")
    ;

    private final String value;

    public static QueryOperatorEnum lookup(final String value) {
        if(StringUtils.isNotBlank(value)) {
            for(QueryOperatorEnum operator : values()) {
                if(operator.getValue().equalsIgnoreCase(value)) {
                    return operator;
                } else if(operator.name().equalsIgnoreCase(value)) {
                    return operator;
                }
            }
        }
        return null;
    }

}
