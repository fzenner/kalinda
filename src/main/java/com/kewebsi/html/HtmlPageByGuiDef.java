package com.kewebsi.html;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.kewebsi.util.SpringUtils;

public abstract class HtmlPageByGuiDef extends AbstractHtmlPage {


	protected String pageTemplate;

	protected UserSession userSession;


	public HtmlPageByGuiDef(String title, String pageName) {
		super(title, pageName);
		init();
		
	}
	
	
	protected void init() {
		pageTemplate = SpringUtils.readClassPathResourceFileAsString("webFrontend/mainPageByGuiDef.html");
	}
	

	public String getHtml() {
		
		String body = "<BODY pageName='" + getPageName() + "' id='" + "weisnet" + "'>";
//		if (children != null) {
//			for (HtmlTag run : children) {
//				body += run.getHtml();
//			}
//		}
		body +="</body>";

		String initialization = "loadPageByGuiDefImpl(" + pageName + ");" ;

		String pageString = pageTemplate.replaceFirst("\\$\\{BODY\\}", body);
		pageString = pageString.replaceFirst("\\$\\{TITLE\\}", title);
		pageString = pageString.replaceFirst("\\$\\{INITIALISATION\\}", initialization);



		return pageString;
	}

	public ObjectNode getJsonNode() {
		return null;
	}

	public GuiDef getGuiDef() {
		GuiDef guiDef = new GuiDef("body", "bodyId");
		guiDef.addAttribute("pagename", getPageName());
		body.addChildrenGuiDefs(guiDef);
		return guiDef;
	}



}
