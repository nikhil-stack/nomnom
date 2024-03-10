package com.example.nomnom.utils;

import lombok.Data;

@Data
public class ApiResponse<T> {


    private int status;
    private String message;
    private T data;

    private ApiResponse() {
    }
    public static <T> ApiResponse<T> successResponse(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.status = 200;
        response.message = "";
        response.data = data;
        return response;
    }

    public static <T> ApiResponse<T> failureResponse(int status, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.status = status;
        response.message = message;
        response.data = null;
        return response;
    }
}
