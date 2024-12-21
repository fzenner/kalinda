package com.fzenner.datademo.web.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@WebServlet(
		  name = "PingServlet",
		  description = "Simple servlet for basic smoke tests",
		  urlPatterns = {"/ping"}
		)
public class PingServlet extends HttpServlet {


	private static final long serialVersionUID = -770426353828524364L;
	private static Logger LOG = LoggerFactory.getLogger(WebFrontendDispatchServlet.class);

	private String message;
		

	public void init() throws ServletException {
		// Do required initialization
		message = "Pong!";
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Set response content type
		response.setContentType("text/html");

		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		out.println("<h1>" + message + "</h1>(by GET)");
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Set response content type
		response.setContentType("text/html");

		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		out.println("<h1>" +message + "</h1>(by POST)");
	}
		
	
	
	public static String bufferedReaderToString(BufferedReader reader) {
		
		StringBuffer jb = new StringBuffer();
		 String line = null;
		  try {
		    while ((line = reader.readLine()) != null)
		      jb.append(line);
		  } catch (Exception e) { 
			  LOG.error("Unexpected exception cought: ", e);
		  }
		  return jb.toString();
	}

	

	public void destroy() {
		// do nothing.
	}
}