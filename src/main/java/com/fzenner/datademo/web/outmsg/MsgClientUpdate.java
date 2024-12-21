package com.fzenner.datademo.web.outmsg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.html.HtmlTag;
import com.kewebsi.util.CommonUtils;

public class MsgClientUpdate extends MsgJsonOut {

	protected String idToUpdateOrParent;
	protected String updateOperation;
	protected String newHtmlCode;         // This variable is set, when the whole tag should be replaced.

	protected JsonNode newHtmlNode;

	protected GuiDef guiDef;

	protected String attributeKey;        // If an attribute is set, when only an attribute should be modified.
	protected String attributeValue;      // If an attribute is set, when only an attribute should be modified.
	protected HtmlTag.Visibility visibilityOrDisplay;

	// protected InputFieldModifications inputFieldModifications;


	public enum UpdateOperation {MODIFIED, NEW, REMOVED, ATTRIBUTE_MODIFIED, ATTRIBUTE_NEW, ATTRIBUTE_REMOVED, CSS_CLASSES_CHANGED, VISIBILITY_OR_DISPLAY_CHANGED,
		UPDATE_BY_DEF_NEW,
		UPDATE_BY_DEF_REMOVED,
		UPDATE_BY_DEF_MODIFIED,
		UPDATE_BY_DEF_CUSTOM,

		// UPDATE_HTML_INPUT_FIELD  // TODO: Generalize partial updates. Look also at table updates.
	};





	public MsgClientUpdate(String idToUpdateOrParent, UpdateOperation updateOperation) {
		super(MsgKewebsiOut.UPDATE_HTML_TAG);
		this.updateOperation = updateOperation.name();
		this.idToUpdateOrParent = idToUpdateOrParent;
	}

	public MsgClientUpdate(String idToUpdateOrParent, UpdateOperation updateOperation, String newHtmlCode) {
		super(MsgKewebsiOut.UPDATE_HTML_TAG);
		this.updateOperation = updateOperation.name();
		this.idToUpdateOrParent = idToUpdateOrParent;
		this.newHtmlCode = newHtmlCode;
	}

	public MsgClientUpdate(String idToUpdateOrParent, UpdateOperation updateOperation, JsonNode newHtmlNode) {
		super(MsgKewebsiOut.UPDATE_HTML_TAG);
		this.updateOperation = updateOperation.name();
		this.idToUpdateOrParent = idToUpdateOrParent;
		this.newHtmlNode = newHtmlNode;
	}

	public MsgClientUpdate(String idToUpdateOrParent, UpdateOperation updateOperation, GuiDef guiDef) {
		super(MsgKewebsiOut.UPDATE_HTML_TAG);
		this.updateOperation = updateOperation.name();
		this.idToUpdateOrParent = idToUpdateOrParent;
		this.guiDef = guiDef;
	}



	public MsgClientUpdate(String idToUpdateOrParent, UpdateOperation updateOperation, String attributeKey, String attributeValue) {
		super(MsgKewebsiOut.UPDATE_HTML_TAG);
		this.updateOperation = updateOperation.name();
		this.idToUpdateOrParent = idToUpdateOrParent;
		this.attributeKey = attributeKey;
		this.attributeValue= attributeValue;

	}

//	public ClientUpdateData(String id, InputFieldModifications inputFieldModifications) {
//		super(MsgKewebsiOut.UPDATE_HTML_TAG);
//		this.updateOperation = UpdateOperation.UPDATE_HTML_INPUT_FIELD.toString();
//		this.idToUpdateOrParent = id;
//		this.inputFieldModifications = inputFieldModifications;
//	}



	public String getIdToUpdateOrParent() {
		return idToUpdateOrParent;
	}


	public void setIdToUpdateOrParent(String idToUpdate) {
		this.idToUpdateOrParent = idToUpdate;
	}


	public String getNewHtmlCode() {
		return newHtmlCode;
	}

	public void setNewHtmlCode(String newHtmlCode) {
		this.newHtmlCode = newHtmlCode;
	}

	public JsonNode getNewHtmlNode() {
		return newHtmlNode;
	}

	public void setNewHtmlNode(ObjectNode newHtmlNode) {
		this.newHtmlNode = newHtmlNode;
	}

	public String getUpdateOperation() {
		return updateOperation;
	}


	public static MsgClientUpdate newMsg(HtmlTag newTag) {
		
		String parentId = newTag.getParent().getId();
		if (parentId == null) {
			throw new CodingErrorException("Attempt to add a new HTML tag to a tag without ID. That is not possible. ID of tag to add: " + newTag.getId());
		}
		MsgClientUpdate result = new MsgClientUpdate(newTag.getParent().getId(), UpdateOperation.UPDATE_BY_DEF_NEW, newTag.getGuiDef());
		return result;
	}
	
	
	public static MsgClientUpdate attributeModified(HtmlTag modifiedTag, String attributeKey, String attributeValue) {
		var result = new MsgClientUpdate(modifiedTag.getId(), UpdateOperation.ATTRIBUTE_MODIFIED, attributeKey, attributeValue);
		return result;
	}

	public static MsgClientUpdate attributeNew(HtmlTag modifiedTag, String attributeKey, String attributeValue) {
		var result = new MsgClientUpdate(modifiedTag.getId(), UpdateOperation.ATTRIBUTE_NEW, attributeKey, attributeValue);
		return result;
	}

	public static MsgClientUpdate attributeRemoved(HtmlTag modifiedTag, String attributeKey, String attributeValue) {
		var result = new MsgClientUpdate(modifiedTag.getId(), UpdateOperation.ATTRIBUTE_REMOVED, attributeKey, attributeValue);
		return result;
	}


//	public static ClientUpdateData inputFieldModifications(HtmlTag modifiedTag, InputFieldModifications inputFieldModifications) {
//		var result = new ClientUpdateData(modifiedTag.getId(), inputFieldModifications);
//		return result;
//	}


	public static MsgClientUpdate visibilityModified(HtmlTag modifiedTag) {
		var result = new MsgClientUpdate(modifiedTag.getId(), UpdateOperation.VISIBILITY_OR_DISPLAY_CHANGED);
		result.visibilityOrDisplay = modifiedTag.getVisibility();
		return result;
	}


	public static MsgClientUpdate cssClassesChanged(HtmlTag modifiedTag) {
		var result = new MsgClientUpdate(modifiedTag.getId(), UpdateOperation.CSS_CLASSES_CHANGED, modifiedTag.getCssClassesAsString());
		return result;
	}


	public static MsgClientUpdate modifiedMsg(HtmlTag modifiedTag) {
		MsgClientUpdate result =  new MsgClientUpdate(modifiedTag.getId(), UpdateOperation.UPDATE_BY_DEF_MODIFIED, modifiedTag.getGuiDefUpdate());
		return result;
	}

	
	public static MsgClientUpdate removedMsg(HtmlTag removedTag) {
		assert(CommonUtils.hasInfo(removedTag.getId()));

		MsgClientUpdate result =
				switch(removedTag.getUpdateProcedure()) {
					case HTML -> new MsgClientUpdate(removedTag.getId(), UpdateOperation.REMOVED);
					case GUI_DEF -> new MsgClientUpdate(removedTag.getId(), UpdateOperation.UPDATE_BY_DEF_REMOVED);
				};

		return result;
	}



	public String getAttributeKey() {
		return attributeKey;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public HtmlTag.Visibility getVisibilityOrDisplay() {
		return visibilityOrDisplay;
	}

	public GuiDef getGuiDef() {
		return guiDef;
	}

//	public InputFieldModifications getInputFieldModifications() {
//		return inputFieldModifications;
//	}


}
