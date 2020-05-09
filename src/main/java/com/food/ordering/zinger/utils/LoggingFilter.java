package com.food.ordering.zinger.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.zinger.dao.interfaces.AuditLogDao;
import com.food.ordering.zinger.model.logger.ApplicationLogModel;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Autowired
    AuditLogDao auditLogDao;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpServletRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpServletResponse);

        filterChain.doFilter(requestWrapper, responseWrapper);

        String requestUrl = requestWrapper.getRequestURL().toString();
        HttpHeaders requestHeaders = new HttpHeaders();
        Enumeration headerNames = requestWrapper.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            requestHeaders.add(headerName, requestWrapper.getHeader(headerName));
        }
        HttpMethod httpMethod = HttpMethod.valueOf(requestWrapper.getMethod());
        String requestBody = IOUtils.toString(requestWrapper.getInputStream(), UTF_8);
        JsonNode requestJson = objectMapper.readTree(requestBody);
        RequestEntity<JsonNode> requestEntity = new RequestEntity<>(requestJson, requestHeaders, httpMethod, URI.create(requestUrl));

        HttpStatus responseStatus = HttpStatus.valueOf(responseWrapper.getStatusCode());
        HttpHeaders responseHeaders = new HttpHeaders();
        for (String headerName : responseWrapper.getHeaderNames())
            responseHeaders.add(headerName, responseWrapper.getHeader(headerName));

        String responseFromServer = " ";
        try {
            String responseBody = IOUtils.toString(responseWrapper.getContentInputStream(), UTF_8);
            JsonNode responseJson = objectMapper.readTree(responseBody);
            ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(responseJson, responseHeaders, responseStatus);
            responseFromServer = responseEntity.getBody().toString();
        } catch (Exception e) {
        }

        String requestBodyJson = new String(requestWrapper.getContentAsByteArray(), UTF_8);
        HttpMethod requestType = requestEntity.getMethod();
        ApplicationLogModel applicationLogModel = new ApplicationLogModel(requestType, requestEntity.getUrl().getPath(), requestEntity.getHeaders().toString(),
                requestBodyJson, responseFromServer);
        auditLogDao.insertLog(applicationLogModel);
        responseWrapper.copyBodyToResponse();
    }
}
