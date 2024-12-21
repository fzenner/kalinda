package com.fzenner.datademo.web.servlets;


import com.fzenner.datademo.web.AjaxDispatcher;
import com.fzenner.datademo.web.UserSessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

//@Component
//@Order(1)
public class SessionFilter implements Filter {

    @Autowired
    UserSessionHandler userSessionHandler;

    static final Logger LOG = LoggerFactory.getLogger(SessionFilter.class);

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

//        HttpServletRequest req = (HttpServletRequest) request;
//
//        // userSessionHandler.getUserSession(req);
//
//
//        LOG.info(
//                "SESSION FILTER HIT Starting a transaction for req : {}",
//                req.getRequestURI());
//
//        chain.doFilter(request, response);
//        LOG.info(
//                "Committing a transaction for req : {}",
//                req.getRequestURI());
    }

    // other methods
}