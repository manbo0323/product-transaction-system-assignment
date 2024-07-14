package com.manbo.common.util.utils;

import com.manbo.common.util.constant.HeaderConst;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MdcUtils {

    public static final String MDC_ORG_ID = "orgId";
    public static final String MDC_USER_ID = "userId";

    public static void setTraceId(String traceId) {
        if (StringUtils.isBlank(traceId)) {
            traceId = MDC.get(HeaderConst.COMMON_TRACE_ID);
        }
        if (StringUtils.isBlank(traceId)) {
            traceId = RandomStringUtils.randomAlphanumeric(10);
        }
        MDC.put(HeaderConst.COMMON_TRACE_ID, traceId);
    }

    public static void setContextInfo(final Long orgId, final Long userId) {
        MDC.put(MDC_ORG_ID, Objects.nonNull(orgId) ? orgId.toString() : null);
        MDC.put(MDC_USER_ID, Objects.nonNull(userId) ? userId.toString() : null);
    }

    public static String getTraceId() {
        return MDC.get(HeaderConst.COMMON_TRACE_ID);
    }

    public static void clear() {
        MDC.clear();
    }

    public static Runnable withMdc(Runnable runnable) {
        final Map<String, String> mdc = MDC.getCopyOfContextMap();
        return () -> {
            try {
                Optional.ofNullable(mdc).filter((map) -> !map.isEmpty()).ifPresent(MDC::setContextMap);
                runnable.run();
            } catch (Exception e) {
                log.error("[WithMdc] set mdc context map got error!", e);
            } finally {
                MDC.clear();
            }
        };
    }

}
