package com.yanzord.votingappservice.controller;

import feign.FeignException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(FeignException.BadRequest.class)
    public Map<String, Object> handleBadRequestException(FeignException e, HttpServletResponse response) {
        response.setStatus(e.status());
        return new JSONObject(e.contentUTF8()).toMap();
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public Map<String, Object> handleNotFoundException(FeignException e, HttpServletResponse response) {
        response.setStatus(e.status());
        return new JSONObject(e.contentUTF8()).toMap();
    }
}