package com.fashionshop.common.response;


public class ApiResponse<T> {
    private boolean success;
    private int statusCode;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return ok(data, "Success");
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = true;
        res.statusCode = 200;
        res.message = message;
        res.data = data;
        return res;
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = true;
        res.statusCode = 201;
        res.message = message;
        res.data = data;
        return res;
    }

    public static <T> ApiResponse<T> error(int statusCode, String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = false;
        res.statusCode = statusCode;
        res.message = message;
        return res;
    }

    // Getters and Setters...
    public boolean isSuccess() { return success; }
    public int getStatusCode() { return statusCode; }
    public int getStatus() { return statusCode; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
