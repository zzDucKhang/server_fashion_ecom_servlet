package com.fashionshop.presentation.controller.common;

import com.fashionshop.presentation.controller.base.BaseHttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Map;

@WebServlet(urlPatterns = {"/health", "/api/v1/health"})
public class HealthServlet extends BaseHttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long uptimeSeconds = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        sendJson(resp, HttpServletResponse.SC_OK, Map.of(
                "status", "ok",
                "uptime", uptimeSeconds
        ));
    }
}
