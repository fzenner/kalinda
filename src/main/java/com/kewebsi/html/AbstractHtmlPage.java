package com.kewebsi.html;

import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgClientUpdate;
import com.kewebsi.controller.UpdateProcessor;
import org.javatuples.Pair;

import java.util.ArrayList;

public abstract class AbstractHtmlPage  {


	protected String title;

	protected String pageName;

	protected UserSession userSession;

	protected int editorPageId;

	protected HtmlBody body;

	public AbstractHtmlPage(String title, String pageName) {
		this.title = title;
		this.pageName = pageName;
		// this.id = pageName;
		this.editorPageId = 0;
		body = new HtmlBody(this);
	}



	public boolean isAjaxInProgress() {
		return userSession.isAjaxInProgress();
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public ArrayList<Pair<HtmlTag, MsgClientUpdate>> collectPageUpdates() {

		var modifiedTags = new ArrayList<Pair<HtmlTag, MsgClientUpdate>>();
		UpdateProcessor.getModifiedTags(body, modifiedTags);
		return modifiedTags;
	}


//	@SuppressWarnings("unchecked")
//	@Override
//	public AbstractHtmlPage getPage() {
//		return this;
//	}


	public String getPageName() {
		return pageName;
	}


	public void setPageName(String pageName) {
		this.pageName = pageName;
	}


	public HtmlBody getBody() {
		return body;
	}

	abstract public PageState getPageState();

//
//	@Override
//	public boolean isContentModified() {
//		return false;
//	}
//
//
//	@Override
//	public void setContentNotModified() {
//
//	}

	public HtmlTag getElementById(String htmlId) {
		return body.getDescendent(htmlId);
	}

	public HtmlTag getDescendent(String htmlId) {
		return body.getDescendent(htmlId);
	}


}
