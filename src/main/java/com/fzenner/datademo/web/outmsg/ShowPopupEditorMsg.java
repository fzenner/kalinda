package com.fzenner.datademo.web.outmsg;

public class ShowPopupEditorMsg extends MsgAjaxResponse {
	
	protected String showPopupUnderTag;
	
	protected String visibleContentTag;
	
	
	public String getVisibleContentTag() {
		return visibleContentTag;
	}

	public void setVisibleContentTag(String visibleContentTag) {
		this.visibleContentTag = visibleContentTag;
	}

	public String getShowPopupUnderTag() {
		return showPopupUnderTag;
	}

	public void setShowPopupUnderTag(String showPopupUnderTag) {
		this.showPopupUnderTag = showPopupUnderTag;
	}

	
}
