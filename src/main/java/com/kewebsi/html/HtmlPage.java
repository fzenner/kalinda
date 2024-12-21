package com.kewebsi.html;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.kewebsi.util.SpringUtils;

public abstract class HtmlPage extends AbstractHtmlPage {
	
	
	protected String pageTemplate;
	
	protected UserSession userSession;

	protected HtmlBody dummyBody;


	public HtmlPage(String title, String pageName) {
		super(title, pageName);
		init();
		
	}
	
	
	protected void init() {
		pageTemplate = SpringUtils.readClassPathResourceFileAsString("webFrontend/mainPage.html");
		dummyBody = new HtmlBody(this);
	}
	

	public String getHtml() {

//		String body = "<BODY pageName='" + getPageName() + "' id='" + getId() + "'>";
//		if (children != null) {
//			for (HtmlTag run : children) {
//				body += run.getHtml();
//			}
//		}
//		body +="</body>";

		// String body = getBody().getHtml();
		// String pageString = pageTemplate.replaceFirst("\\$\\{BODY\\}", body);


		String emptyBody = dummyBody.getHtml();
		// String emptyBody = "";
		String pageString = pageTemplate.replaceFirst("\\$\\{BODY\\}", emptyBody);



		pageString = pageString.replaceFirst("\\$\\{TITLE\\}", title);
		return pageString;
	}

	public ObjectNode getJsonNode() {
		return null;
	}

	public GuiDef getGuiDef() {
		GuiDef guiDef = new GuiDef("body", "bodyId");
		body.addChildrenGuiDefs(guiDef);
		return guiDef;
	}



}
