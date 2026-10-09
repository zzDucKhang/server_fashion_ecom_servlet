package com.fashionshop.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.response.ApiResponse;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class GlobalExceptionFilter implements Filter {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;

        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {
            handleException(ex, res);
        }
    }

    private void handleException(Throwable ex, HttpServletResponse res) throws IOException {
        Throwable root = ex;
        while (root.getCause() != null && !(root instanceof AppException)) {
            root = root.getCause();
        }

        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String message = root.getMessage() != null ? root.getMessage() : "Internal Server Error";

        if (root instanceof AppException appEx) {
            status = appEx.getStatusCode();
        }

        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(res.getOutputStream(), ApiResponse.error(status, message));
    }
}