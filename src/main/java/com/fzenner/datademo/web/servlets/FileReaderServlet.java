package com.fzenner.datademo.web.servlets;



import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.kewebsi.util.SpringUtils;



//Extend HttpServlet class
@WebServlet(
		  name = "FileReaderServlete",
		  description = "Example Servlet Using Annotations",
		  urlPatterns = {"/fileread"}
		)
public class FileReaderServlet extends HttpServlet {


	private static final long serialVersionUID = -770426353828524364L;
	private static Logger LOG = LoggerFactory.getLogger(WebFrontendDispatchServlet.class);

	private String message;
	
	public static long helperId = 1;
	
    @Autowired
    ResourceLoader resourceLoader;
	

	public void init() throws ServletException {
		// Do required initialization
		message = "Hello World";
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Set response content type
		response.setContentType("text/html");

		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		// out.println("<h1>" + "Get " + message + "</h1>");
		
	
		String fileInResourceFolder = "mainPage.html";
		
		String fileAsString = readClassPathResourceFile(fileInResourceFolder);
		
		out.println(fileAsString);
		
		
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Set response content type
		response.setContentType("text/html");

		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		out.println("<h1>" + "Post " +message + "</h1>");
	}
		
	
	

	public String readFileTester(String fileInResourceFolder) { 
	    
		String result;
		
		Resource resource = resourceLoader.getResource("classpath:" + fileInResourceFolder);
	    
	    try {
	    	// InputStream input = resource.getInputStream();
	    	File file = resource.getFile();
	    	byte[] bytes = Files.readAllBytes( Paths.get(file.getPath()) );
	    	result = new String(bytes);
	    } catch (Exception e) {
	    	result = exceptionToString(e);
	    }
	    return result;
	}
	
	
	public String readClassPathResourceFile(String fileInResourceFolder) { 
		String result = SpringUtils.readClassPathResourceFileAsString(fileInResourceFolder);
	    return result;
	}	
	
	    
	public String exceptionToString(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return pw.toString();
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