package com.userservice.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.StringJoiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(
            (HttpServletRequest) request);
        chain.doFilter(wrappedRequest, response);

        String method = wrappedRequest.getMethod();
        String uri = wrappedRequest.getRequestURI();
        String queryParams = getQueryParams(wrappedRequest);
        if (!queryParams.isEmpty()) {
            uri += "?" + queryParams;
        }
        String body = getRequestBody(wrappedRequest);
        if (body.isEmpty()) {
            log.info("[요청] {} {}", method, uri);
        } else {
            log.info("[요청] {} {}\nRequest Body: {}", method, uri, body);
        }
    }

    private String getQueryParams(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                sj.add(key + "=" + value);
            }
        }
        return sj.toString();
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                return new String(buf, 0, buf.length, request.getCharacterEncoding());
            } catch (UnsupportedEncodingException ex) {
                return "[unknown]";
            }
        }
        return "";
    }
}
