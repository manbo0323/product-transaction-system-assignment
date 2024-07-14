package com.manbo.common.util.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @author Manbo
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DecimalUtils {

    public static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    public static final BigDecimal ONE_THOUSAND = BigDecimal.valueOf(1000);

    /**
     * @return StringUtils.isBlank(d1) ? 0 : new BigDecimal(d1)
     */
    public static BigDecimal of(final String d1) {
        if(StringUtils.isBlank(d1)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(d1);
    }

    /**
     * @return d1 == null ? 0 : BigDecimal.valueOf(d1.doubleValue())
     */
    public static BigDecimal of(final Number d1) {
        if(Objects.isNull(d1)) {
            return BigDecimal.ZERO;
        }
        if(d1 instanceof BigDecimal decimal) {
            return decimal;
        }
        if (d1 instanceof Double || d1 instanceof Float) {
            return BigDecimal.valueOf(d1.doubleValue());
        }
        return BigDecimal.valueOf(d1.longValue());
    }

    /**
     * @return d1 == d2
     */
    public static boolean eq(final Number d1, final Number d2) {
        return of(d1).compareTo(of(d2)) == 0;
    }

    /**
     * @return d1 != d2
     */
    public static boolean ne(final Number d1, final Number d2) {
        return !eq(d1, d2);
    }

    /**
     * @return d1 > d2
     */
    public static boolean gt(final Number d1, final Number d2) {
        return of(d1).compareTo(of(d2)) > 0;
    }

    /**
     * @return d1 >= d2
     */
    public static boolean ge(final Number d1, final Number d2) {
        return of(d1).compareTo(of(d2)) >= 0;
    }

    /**
     * @return d1 < d2
     */
    public static boolean lt(final Number d1, final Number d2) {
        return !ge(d1, d2);//d1 < d2 = !(d1 >= d2)
    }

    /**
     * @return d1 <= d2
     */
    public static boolean le(final Number d1, final Number d2) {
        return !gt(d1, d2);//d1 <= d2 = !(d1 > d2)
    }

    /**
     * @return decimal == 0
     */
    public static boolean eqZero(final Number number) {
        return eq(number, BigDecimal.ZERO);
    }

    /**
     * @return decimal != 0
     */
    public static boolean neZero(final Number number) {
        return !eqZero(number);
    }

    /**
     * @return decimal > 0
     */
    public static boolean gtZero(final Number number) {
        return gt(number, BigDecimal.ZERO);
    }

    /**
     * @return decimal >= 0
     */
    public static boolean geZero(final Number number) {
        return ge(number, BigDecimal.ZERO);
    }

    /**
     * @return decimal < 0
     */
    public static boolean ltZero(final Number number) {
        return !geZero(number);//decimal < 0 = !(decimal >= 0)
    }

    /**
     * @return decimal <= 0
     */
    public static boolean leZero(final Number number) {
        return !gtZero(number);//decimal <= 0 = !(decimal > 0)
    }

    /**
     * @return d1 + d2
     */
    public static BigDecimal add(final Number d1, final Number d2) {
        return of(d1).add(of(d2));
    }

    /**
     * @return d1 - d2
     */
    public static BigDecimal subtract(final Number d1, final Number d2) {
        return of(d1).subtract(of(d2));
    }

    /**
     * @return d1 * d2
     */
    public static BigDecimal multiply(final Number d1, final Number d2) {
        if (eqZero(d1)) {
            return BigDecimal.ZERO;
        }
        return of(d1).multiply(of(d2));
    }

    /**
     * @return d1 * d2
     */
    public static BigDecimal multiply(final Number d1, final Number d2, final int scale, final RoundingMode mode) {
        if (eqZero(d1)) {
            return BigDecimal.ZERO;
        }
        return of(d1).multiply(of(d2)).setScale(scale, mode);
    }

    /**
     * default scale = d1.scale()
     * if d1.scale() == 0 then scale = 2
     * <p>
     * RoundingMode:HALF_UP
     *
     * @return d1 / d2
     */
    public static BigDecimal divide(final Number d1, final Number d2) {
        if (eqZero(d1)) {
            return BigDecimal.ZERO;
        }
        int scale = of(d1).scale() == 0 ? 2 : of(d1).scale();
        return divide(d1, d2, scale);
    }

    /**
     * @return d1 / d2
     */
    public static BigDecimal divide(final Number d1, final Number d2, final int scale) {
        if (eqZero(d1)) {
            return BigDecimal.ZERO;
        }
        return divide(d1, d2, scale, RoundingMode.HALF_UP);
    }

    /**
     * @return d1 / d2
     */
    public static BigDecimal divide(final Number d1, final Number d2, final int scale, final RoundingMode mode) {
        if (eqZero(d1)) {
            return BigDecimal.ZERO;
        }
        return of(d1).divide(of(d2), scale, mode);
    }

    /**
     * if d1 < d2 then d1 else d2
     *
     * @return the smaller of d1 and d2.
     */
    public static BigDecimal min(final Number d1, final Number d2) {
        return lt(d1, d2) ? of(d1) : of(d2);
    }

    /**
     * if d1 > d2 then d1 else d2
     *
     * @return the largest of d1 and d2.
     */
    public static BigDecimal max(final Number d1, final Number d2) {
        return gt(d1, d2) ? of(d1) : of(d2);
    }

    /**
     * @return abs(d1)
     */
    public static BigDecimal abs(final Number d1) {
        return of(d1).abs();
    }

    /**
     * @return low <= d1 <= high
     */
    public static boolean between(final Number d1, final Number low, final Number high) {
        return ge(d1, low) && le(d1, high);
    }
}
