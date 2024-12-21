package com.kewebsi.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.html.*;

public class PageBuilder {


	public static HtmlButtonStandard createCloseModalDialogButton(String id, String label, String idOfModalTagToClose) {

		var cancelButton = new HtmlButtonStandard(id, label) {

			@Override
			public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
				var modalDialogToClose = getPage().getElementById(idOfModalTagToClose);
				modalDialogToClose.remove();
				return MsgAjaxResponse.createSuccessMsg();
			}
		};



		return cancelButton;

	}



	public static HtmlDivButtonStandard createCloseModalDialogButtonX(String id, String idOfModalTagToClose) {

		var cancelButton = new HtmlDivButtonStandard(id, "&#x2716;") {

			@Override
			public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
				var modalDialogToClose = getPage().getElementById(idOfModalTagToClose);
				modalDialogToClose.remove();
				return MsgAjaxResponse.createSuccessMsg();
			}
		};

		cancelButton.addCssClass("close");
		return cancelButton;
	}





	public static <T extends Enum<T>> void  addLabelAndInputField(
			StringGetter stringGetter,
			T inputFieldId, 
			String labelText, 
			HtmlDiv addTo) {
		HtmlLabel label = new HtmlLabel(labelText);
		HtmlInput inputField = new HtmlInput(stringGetter, inputFieldId.name());
		label.setForTag(inputField);
		label.addCssClass(CssConstants.cssInputFieldLabelLeft);
		addTo.addChild(label);
		addTo.addChild(inputField);
	}
	
	public static <S, T extends Enum<T>> void  addLabelAndRoInputField(
			StringGetter stringGetter,
			T inputFieldId, 
			String labelText, 
			HtmlDiv addTo) {
		HtmlLabel label = new HtmlLabel(labelText);
		HtmlInput inputField = new HtmlInput(stringGetter, inputFieldId.name());
		inputField.setReadOnly(true);
		label.setForTag(inputField);
		label.addCssClass(CssConstants.cssInputFieldLabelLeft);
		addTo.addChild(label);
		addTo.addChild(inputField);
	}
	

	public static <S, T extends Enum<T>> void  addLabelAndTag(
			String labelText,
			HtmlTag tagToAdd,
			HtmlDiv addTo) {
		HtmlLabel label = new HtmlLabel(labelText);
		label.setForTag(tagToAdd);
		label.addCssClass(CssConstants.cssInputFieldLabelLeft);
		addTo.addChild(label);
		addTo.addChild(tagToAdd);
	}


	public static <S, T extends Enum<T>> void  addLabelAndTag(
			HtmlTag tag,
			String labelText,
			HtmlDiv addTo) {
		HtmlLabel label = new HtmlLabel(labelText);
		label.setForTag(tag);
		label.addCssClass(CssConstants.cssInputFieldLabelLeft);
		addTo.addChild(label);
		addTo.addChild(tag);
	}



	public static <S, T extends Enum<T>> void  addLabelAndDynText(
			StringGetter<S> stringGetter,
			T inputFieldId,
			String labelText,
			HtmlDiv addTo) {
		HtmlLabel label = new HtmlLabel(labelText);

		var dynTextField =  new HtmlDynPlainText(stringGetter, inputFieldId.name());
		label.setForTag(dynTextField);
		label.addCssClass(CssConstants.cssInputFieldLabelLeft);
		addTo.addChild(label);
		addTo.addChild(dynTextField);
	}

	
	/**
	 * Generates a string for defining an attribute in an HTML tag.
	 * @param name The name of the attribute.
	 * @param value The value of the attribue.
	 * @return A string of the following form " name='value'" if the value
	 * is not null and not empty. (Not the leading blank). 
	 * Returns the empty string otherwise.
	 */
	public static String constructOptionalAttribute(String name, String value) {
		if (CommonUtils.hasInfo(value)) { 
			return " " + name + "='" + value + "'";
		} else {
			return "";
		}
	}

	public static String constructTag(String name, String value) {
		value = value!=null ? value : "";
		return " " + name + "='" + value + "'";
	}
	
	public static String generateOpenTag(String tagName, String[] attributesAndValues) {
		if (attributesAndValues.length % 2 != 0) {
			throw new RuntimeException("The number of elements in attributesAndValues is not even: " + attributesAndValues.length);
		}
		String result = "<" + tagName;
		int inputIdx = 0;
		
		while (inputIdx < attributesAndValues.length ) {
			String attrName = attributesAndValues[inputIdx++];
			String attrValue = attributesAndValues[inputIdx++];
			result += " " + attrName + "='" + attrValue + "'";
		}
		result += ">"; 
		return result;
	}
	
	
}
