package com.manbo.common.web.filter;

import com.google.common.base.Stopwatch;
import com.manbo.common.util.constant.HeaderConst;
import com.manbo.common.util.utils.MdcUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author kai
 */
@Slf4j
public class LogProcessFilter extends AbstractRequestLoggingFilter {

    private final String[] ignoreMessages;

    public LogProcessFilter(final List<String> ignoreMessages) {
        this.ignoreMessages = Optional.ofNullable(ignoreMessages)
            .map(msgs -> msgs.toArray(new String[0]))
            .orElseGet(() -> new String[0]);
    }

    @Override
    protected boolean shouldLog(@NonNull final HttpServletRequest request) {
        final String requestURI = request.getRequestURI();
        return logger.isInfoEnabled() && !StringUtils.containsAnyIgnoreCase(requestURI, ignoreMessages);
    }

    @Override
    protected void beforeRequest(@NonNull final HttpServletRequest request, @NonNull final String message) {
        MdcUtils.setTraceId(request.getHeader(HeaderConst.COMMON_TRACE_ID));
        final Stopwatch stopwatch = Stopwatch.createStarted();
        request.setAttribute("stopwatch", stopwatch);
    }

    @Override
    protected void afterRequest(@NonNull final HttpServletRequest request, @NonNull final String message) {
        final Stopwatch stopwatch = (Stopwatch) request.getAttribute("stopwatch");
        log.info("{}, process time - {}", message, stopwatch.stop());
        MdcUtils.clear();
    }

    @Override
    protected String getMessagePayload(@NonNull final HttpServletRequest request) {
        final String contentType = request.getContentType();
        final boolean supportedContentType = StringUtils.isNotBlank(contentType) && StringUtils.containsAny(contentType, MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE, MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        final ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if(Objects.isNull(wrapper) || !supportedContentType) {
            return null;
        }
        final byte[] buf = wrapper.getContentAsByteArray();
        if(buf.length == 0) {
            return null;
        }
        int length = Math.min(buf.length, getMaxPayloadLength());
        try {
            return new String(buf, 0, length, wrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException ex) {
            return "[unknown]";
        }
    }
}
