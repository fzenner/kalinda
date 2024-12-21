package com.fzenner.datademo.web.outmsg;

import com.kewebsi.html.HtmlTag;
import com.kewebsi.html.PageStateError;
import com.kewebsi.html.popup.ModalStandardDialogDef;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class MsgAjaxResponse extends MsgJsonOut {
	
	protected ErrorInfoExtern errorInfo;
	
	protected ArrayList<MsgClientUpdate> htmlUpdates;
	
	protected ModalStandardDialogDef modalStandardDialogDef;

	protected CustomCallbackData customCallbackData;


	private static Logger LOG = LoggerFactory.getLogger(MsgAjaxResponse.class);
	
	// protected String htmlTagToFocus;



	public FocusDef focusDef;
	
//	protected String showPopupUnderTag;
//
//	protected String nonTransparentTag;

	protected int editPageId;


	public MsgAjaxResponse() {
		super(MsgKewebsiOut.SUCCESS);
	}
	
	public MsgAjaxResponse(MsgKewebsiOut msgName) {
		super(msgName);
	}
	


	public ArrayList<MsgClientUpdate> getHtmlUpdates() {
		return htmlUpdates;
	}


	public void setHtmlUpdates(ArrayList<MsgClientUpdate> newHtmlUpdates) {
		this.htmlUpdates = newHtmlUpdates;
	}

	public void addHtmlUpdate(MsgClientUpdate htmlUpdateToAdd) {
		if (this.htmlUpdates == null) {
			this.htmlUpdates = new ArrayList<>();
		}
		htmlUpdates.add(htmlUpdateToAdd);
	}

	
	public void addHtmlUpdates(ArrayList<Pair<HtmlTag, MsgClientUpdate>> htmlUpdatesToAdd) {
		if (this.htmlUpdates == null) {
			this.htmlUpdates = new ArrayList<>();
		}
		for (var run : htmlUpdatesToAdd) {
			this.htmlUpdates.add(run.getValue1());
		}
	}
	
	
	
//	public ModalDialogDef getModalDialogToShow() {
//		return modalDialogDef;
//	}
//
//
//	public void setModalDialogToShow(ModalDialogDef modalDialogDef) {
//		this.modalDialogDef = modalDialogDef;
//	}


//	public String getHtmlTagToFocus() {
//		return htmlTagToFocus;
//	}

	public MsgAjaxResponse setHtmlTagToFocus(String htmlTagToFocusId) {
		setFocusDef(new FocusDef(htmlTagToFocusId));
		return this;
	}

	public MsgAjaxResponse setHtmlTagToFocus(HtmlTag htmlTagToFocus) {
		setFocusDef(FocusDef.focus(htmlTagToFocus));
		return this;
	}
	
	
	public static MsgAjaxResponse createErrorMsg(String errorCode, String errorText) {
		var result = new MsgAjaxResponse();
		result.errorInfo = new ErrorInfoExtern(errorCode, errorText);
		return result;
		
	}
	
	public static MsgAjaxResponse createErrorMsg(String errorText) {
		var errorInfo = new ErrorInfoExtern();
		errorInfo.errorText = errorText;
		var result = new MsgAjaxResponse(MsgKewebsiOut.BUSINESS_ERROR);
		result.errorInfo = errorInfo;
		return result;
	}

	public static MsgAjaxResponse createErrorMsg(PageStateError pageStateError) {
		var errorInfo = new ErrorInfoExtern();
		errorInfo.errorText = pageStateError.getErrorMsg();
		var result = new MsgAjaxResponse(MsgKewebsiOut.BUSINESS_ERROR);
		result.errorInfo = errorInfo;
		return result;
	}
	
	/**
	 * Message indicates there is nothing to do on the client side. 
	 * @return Indicator message.
	 */
	public static MsgAjaxResponse createSuccessMsg() {
		return new MsgAjaxResponse(MsgKewebsiOut.SUCCESS);
	}

	
	public ErrorInfoExtern getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(ErrorInfoExtern errorInfo) {
		this.errorInfo = errorInfo;
	}
	public CustomCallbackData getCustomCallbackData() {
		return customCallbackData;
	}

	public void setCustomCallbackData(CustomCallbackData customCallbackData) {
		this.customCallbackData = customCallbackData;
	}

	public int getEditPageId() {
		return editPageId;
	}

	public void setEditPageId(int editPageId) {
		this.editPageId = editPageId;
	}



	public MsgAjaxResponse setFocusDef(FocusDef newFocusDef) {
		if (focusDef != null) {
			LOG.warn("Problem in MsgAjaxResponse.setHtmlTagToFocus. Element to be focused is defined more then once. Existing element to focus: %s, New element to focus: %s",
					focusDef.tagIdToFocus,
					newFocusDef.tagIdToFocus);
		}
		this.focusDef = newFocusDef;
		return this;
	}

	public FocusDef getFocusDef() {
		return focusDef;
	}

}
