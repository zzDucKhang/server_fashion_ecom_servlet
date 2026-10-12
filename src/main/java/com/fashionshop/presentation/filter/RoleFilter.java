package com.fashionshop.presentation.filter;

import jakarta.servlet.*;

import java.io.IOException;

/**
 * RoleFilter (RBAC Filter)
 * Note: Fine-grained Role-Based Access Control in this module is enforced directly
 * via SecurityGuard.requireAdmin(req) and SecurityGuard.requireAuth(req) inside
 * each Servlet endpoint to allow flexible per-route permissions (e.g. self vs admin).
 * This class is reserved for declarative URL-pattern RBAC middleware extensions.
 */
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(request, response);
    }
}
