package com.fzenner.datademo.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.kewebsi.errorhandling.CodingErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fzenner.datademo.gui.DataDemoPage;
import com.fzenner.datademo.gui.EntityNavigatorPage;
import com.fzenner.datademo.web.outmsg.MsgJsonOut;
import com.kewebsi.errorhandling.ErrorInSessionHandling;
import com.kewebsi.html.HtmlPage;

public class UserSession {
	
	public static final String ATTRIBUTE_NAME = "USER_SESSION";
	
	static final Logger LOG = LoggerFactory.getLogger(UserSession.class);

	public HashMap<String, HtmlPage> pages = new HashMap<>();

	protected int nextEditorPageId = 1;

	/**
	 * Editor pages are pages that maintain serverside status.
	 */
	public HashMap<Integer, HtmlPage> editorPages = new HashMap<>();


	public int registerEditorPage(HtmlPage htmlPage) {
		int editorPageId = nextEditorPageId++;
		editorPages.put(editorPageId, htmlPage);
		return editorPageId;
	}

	public HtmlPage getEditorPage(int editorPageId) {
		return editorPages.get(editorPageId);
	}


	public void registerPage(HtmlPage htmlPage) {
		String key = htmlPage.getPageName();

		if (key == null) {
			throw new CodingErrorException("Registering page with null name.");
		}

		if (pages.get(key) != null) {
			throw new CodingErrorException("Duplicate registering of a page: " + key);
		}
		pages.put(key, htmlPage);
	}


//	public HtmlPage getPage(String pageName) {
//		HtmlPage result = pages.get(pageName);
//		if (result == null) {
//			throw new ErrorInSessionHandling("Session does not exist (anymore). No session for page " + pageName + " found.");
//		}
//		return result;
//	}


	public HtmlPage getPageIfExists(String pageName) {
		return pages.get(pageName);
	}

	protected AtomicInteger pageIdGenerator;
	
	protected ArrayList<MsgJsonOut> outMessages;
	
	protected Date creationTimeStamp; 
	
	protected String sessionId;

	/**
	 * True, when we are currently processing a servlet request.
	 * In such a case, commands to dynamically modify the page on the client side 
	 * when the request returns are generated.
	 */
	protected boolean ajaxInProgress = false;


	public UserSession(String sessionId) {
		this.sessionId = sessionId;
		creationTimeStamp = new Date();
		pageIdGenerator = new AtomicInteger();
		
	}

	public void initializeAjaxProcessing() {
		if (ajaxInProgress) {
			LOG.warn("Ajax handling already initialized. Second initialization might drop already accumlated Ajax results!");  
			return;
		}
		ajaxInProgress = true;
		outMessages = new ArrayList<MsgJsonOut>();

	}

	public void stopAjaxProcessing() {
		if (!ajaxInProgress) {
			LOG.warn("Ajax proxessing is already stopped.");  
		}
		ajaxInProgress = false;
		outMessages = new ArrayList<MsgJsonOut>();
		
	}
	
	public int generatePageId() {
		return this.pageIdGenerator.getAndIncrement();
	}

	public Date getCreationTimeStamp() {
		return creationTimeStamp;
	}

	public boolean isAjaxInProgress() {
		return ajaxInProgress;
	}


	public String getSessionId() {
		return sessionId;
	}


	
}
