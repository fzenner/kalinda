package com.kewebsi.html.popup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.inmsg.MsgInvokeServiceWithSimpleParams;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.EscapeKeyHandler;
import com.kewebsi.controller.GuiAction;
import com.kewebsi.controller.StandardController;
import com.kewebsi.html.*;
import com.kewebsi.util.JsonUtils;
import com.kewebsi.util.PageBuilder;

public class HtmlModalDialog extends SmartTag implements EscapeKeyHandler {

	protected String modalText;

	protected String confirmButtonText;

	protected GuiAction callBackWhenConfirmed;

	protected boolean contentModified;

	protected HtmlTag confirmButton;
	protected HtmlTag cancelButton;
	protected HtmlTag closeButtonX;


	public HtmlModalDialog(String id, String modalText, String confirmButtonText, GuiAction callBackWhenConfirmed) {
		this.id = id;
		this.modalText = modalText;
		addCssClass("modal");
		this.confirmButtonText = confirmButtonText;
		this.contentModified = false;
		this.callBackWhenConfirmed = callBackWhenConfirmed;
		createChildren();

	}




	public void setDialogActionCallback(GuiAction callBack) {
		this.callBackWhenConfirmed = callBack;
	}


	private void createChildren() {
		confirmButton = new HtmlButtonStandard(getConfirmButtonId(), "Confirm") {
			@Override
			public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
				var out =  callBackWhenConfirmed.execute();
				HtmlModalDialog.this.remove(); // Remove this modal window.
				return out;
			};
		};


		cancelButton = PageBuilder.createCloseModalDialogButton(getCancelButtonId(), "Cancel", id);
		closeButtonX = PageBuilder.createCloseModalDialogButtonX("id"+ "-cancel", id);

		confirmButton.addCssClass("standard-button");
		cancelButton.addCssClass("standard-button");

		addChildren(confirmButton, cancelButton, closeButtonX);
	}

	public String getConfirmButtonId() {
		return id + "-confirm";
	}

	public String getCancelButtonId() {
		return id + "-cancel";
	}

	@Override
	public boolean isContentOrGuiDefModified() {
		return contentModified;
	}

	@Override
	public void setContentOrGuiDefNotModified() {
		contentModified = false;

	}


	public GuiAction getCallBackWhenConfirmed() {
		return callBackWhenConfirmed;
	}

	public String getModalText() {
		return modalText;
	}

	public String getConfirmButtonText() {
		return confirmButtonText;
	}


	@Override
	public String getTag() {
		return "div";
	}

	@Override
	public ModalStandardDialogDef getGuiDef() {
		ModalStandardDialogDef modalStandardDialogDef = new ModalStandardDialogDef(id);
		modalStandardDialogDef.setModalDialogSpecificData(modalText, cancelButton.getGuiDef(), confirmButton.getGuiDef(), closeButtonX.getGuiDef());
		setClientEventHandler(ClientEventHandler.CEH_escapeKeyPressed);
		return modalStandardDialogDef;

	}

	@Override
	public MsgAjaxResponse handleEscapeKeyPressed(UserSession userSession) {
		remove();
		return MsgAjaxResponse.createSuccessMsg();
	}
}
