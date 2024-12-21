package com.fzenner.datademo.web;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserSessionHandler {

	private static Logger LOG = LoggerFactory.getLogger(UserSessionHandler.class);
	
	public ArrayList<UserSession> userSessions = new ArrayList<>();
	
	public UserSessionHandler() {
		LOG.info("UserSessionHandler instantiated");
	}
	
	public void registerUserSession(UserSession userSession) {
		userSessions.add(userSession);
	}
	
	public UserSession createAndRegisterUserSession(HttpSession session) {
		UserSession userSession = new UserSession(session.getId());
		registerUserSession(userSession);
		return userSession;
	}
	

	public boolean unregisterUserSession(UserSession userSession) {
		return userSessions.remove(userSession);
	}

	public UserSession getUserSession(String sessionId) {
		for (UserSession sessionRun : userSessions) {
			String sessionIdRun = sessionRun.getSessionId();
			if (sessionIdRun == null) {
				LOG.warn("Stored session without sessionId found.");
				return null;
			}
			if (sessionIdRun.equals(sessionId)) {
				return sessionRun;
			}
		}
		return null;
	}
	
	public boolean userSessionIsNew(String sessionId) {
		if (getUserSession(sessionId) == null) {
			return true;
		}
		return false;
	}
	
	
	public UserSession getUserSession(HttpServletRequest request) {
		HttpSession httpSession = request.getSession();  // Get old or create new session.
		return getUserSession(httpSession);
	}

	public UserSession getUserSession(HttpSession httpSession) {
		UserSession userSession = getUserSession(httpSession.getId());
		if (userSession == null) {
			userSession = createAndRegisterUserSession(httpSession);
		}
		return userSession;
	}

	
}
