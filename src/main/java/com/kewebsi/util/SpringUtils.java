package com.kewebsi.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;


@Component
public class SpringUtils {
	
	
	private static ResourceLoader resourceLoader;
	
	private static Logger LOG = LoggerFactory.getLogger(SpringUtils.class);
	
	/**
	 * Placing a bean in a static variable during initialization for convenience. 
	 * This approach is considered somewhat dirty but worth it.
	 * @param resourceLoader Will be injected automatically.
	 */
	@Autowired 
	public SpringUtils(ResourceLoader resourceLoader) {
		System.out.println("SpringUtils initialized!!!");
		SpringUtils.resourceLoader = resourceLoader;
	}
		
	
	
	public static String readClassPathResourceFileAsString(String fileInResourceFolder) { 
		Resource resource = resourceLoader.getResource("classpath:" + fileInResourceFolder);
	    return readResourceAsString(resource);
	}

	
	
	public static byte[] readClassPathResourceFileAsByteArray(String fileInResourceFolder) { 
		Resource resource = resourceLoader.getResource("classpath:" + fileInResourceFolder);
	    return readResourceAsByteArray(resource);
	}

	
	public static String readResourceFileAsString(String resourcePath) {
		Resource resource = resourceLoader.getResource(resourcePath);
	    return readResourceAsString(resource);
	}
	

	private static String readResourceAsString(Resource resource) {
		String result;
		try {
	    	InputStream inputStream = resource.getInputStream();   // We cannot load the file as file, since it is placed in a jar after deployment.
	    	result = StreamUtils.copyToString(inputStream, Charset.defaultCharset());
	    } catch (Exception e) {
	    	result = exceptionToString(e);
	    	LOG.error(result);
	    	
	    }
	    return result;
	}
	

	private static byte[] readResourceAsByteArray(Resource resource) {
		byte[] result;
		try {
	    	InputStream inputStream = resource.getInputStream();   // We cannot load the file as file, since it is placed in a jar after deployment.
	    	result = StreamUtils.copyToByteArray(inputStream);
	    } catch (Exception e) {
	    	String exceptionAsString = exceptionToString(e);
	    	LOG.error(exceptionAsString);
	    	result = exceptionAsString.getBytes();	    	
	    }
	    return result;
	}
	
	
	/**
	 * Reads file into string. 
	 * @param filePathAndName Filepath must be absolute or relative. 
	 * Pseudo-Paths ("classpath:") is not supported in this method.
	 * Files lokated in jars are not supported in this method.
	 * @return String containing the file.
	 */
	public static String readExternalFileAsString(String filePathAndName) { 
		String result;
		String filePathForErrorHandling=null; 
		Resource resource = resourceLoader.getResource(filePathAndName);
	    
	    try {
	    	// InputStream input = resource.getInputStream();
	    	File file = resource.getFile();
	    	filePathForErrorHandling = file.getAbsolutePath();
	    	byte[] bytes = Files.readAllBytes( Paths.get(file.getPath()) );
	    	result = new String(bytes);
	    } catch (Exception e) {
	    	
	    	result = exceptionToString(e);
	    	LOG.error("Error accessing the following file: " + filePathForErrorHandling);
	    	LOG.error(result);
	    	
	    }
	    return result;
	}
	
	
	
	public static void readClassPathResourceFileToOutputStream(String fileInResourceFolder, OutputStream out) {


		Resource resource = resourceLoader.getResource("classpath:" + fileInResourceFolder);

		if (!resource.exists()) {
			String errorMsg = "Error reading file. The following file does not exist: " + resource.getDescription();
			LOG.error(errorMsg);
			try {
				out.write(errorMsg.getBytes());
			} catch (IOException e) {
				LOG.error("Exception during error handling: \n" + exceptionToString(e));
			}
		} else {
			try {
				InputStream inputStream = resource.getInputStream(); // We cannot load the file as file, since it is placed in a jar after deployment.
				StreamUtils.copy(inputStream, out);
				// String debug = StreamUtils.copyToString(inputStream, Charset.defaultCharset());  // TODO remove
				// LOG.debug(debug);
			} catch (Exception e) {
				String exceptionAsString = exceptionToString(e);
				LOG.error(exceptionAsString);
			}
		}
	}

	
	
	public static String readFileTesterWillNotWorkInJars(String fileInResourceFolder) { 
	    
		String result;
		String filePathForErrorHandling=null; 
		Resource resource = resourceLoader.getResource("classpath*:" + fileInResourceFolder);
	    
	    try {
	    	// InputStream input = resource.getInputStream();
	    	File file = resource.getFile();
	    	filePathForErrorHandling = file.getAbsolutePath();
	    	byte[] bytes = Files.readAllBytes( Paths.get(file.getPath()) );
	    	result = new String(bytes);
	    } catch (Exception e) {
	    	
	    	result = exceptionToString(e);
	    	LOG.error("Error accessing the following file: " + filePathForErrorHandling);
	    	LOG.error(result);
	    	
	    }
	    return result;
	}
	
	
	
	public static String exceptionToString(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}	
	
	
	
}
