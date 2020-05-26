package com.yanzord.votingappservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestControllerAdvice
public class ExceptionController {
    @Autowired
    private ObjectMapper objectMapper;

    @ExceptionHandler(FeignException.BadRequest.class)
    public Map<String, Object> handleBadRequestException(FeignException e, HttpServletResponse response) throws JsonProcessingException {
        response.setStatus(e.status());
        return objectMapper.readValue(e.contentUTF8(), new TypeReference<Map<String,Object>>(){});
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public Map<String, Object> handleNotFoundException(FeignException e, HttpServletResponse response) throws JsonProcessingException {
        response.setStatus(e.status());
        return objectMapper.readValue(e.contentUTF8(), new TypeReference<Map<String,Object>>(){});
    }
}