package com.kewebsi.html;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fzenner.datademo.web.inmsg.ClientEventHandlerConsts;
import com.fzenner.datademo.web.inmsg.ServerMessageHandler;
import com.kewebsi.controller.GuiDelegate;
import com.kewebsi.util.JsonUtils;


public class HtmlSelect extends StringInputFieldByPageState {

	ArrayList<HtmlSelectOption> options = new ArrayList<HtmlSelectOption>(0);
	
	public <S> HtmlSelect(StringGetter<S> stringGetter, String id) {
		this.stringGetter = stringGetter;
		this.id = id;
		this.name = id;
	}
	
	
	public <T extends Enum<T>, S> HtmlSelect(StringGetter<S> stringGetter, T  id) {
		this.stringGetter = stringGetter;
		this.id = id.name();
		this.name = this.id;;
	}
	

	public HtmlSelect setOptions(ArrayList<HtmlSelectOption> options) {
		this.options = options;
		return this;
	}
	
	
	public void addOption(String value, String text) {
		options.add(new HtmlSelectOption(value, text));
	}
	
	public void addOption(int valueAndDisplay) {
		String valueAndDisplayStr = Integer.toString(valueAndDisplay);
		options.add(new HtmlSelectOption(valueAndDisplayStr, valueAndDisplayStr));
	}
	
	public String generateOptions() {
		var result = "";
		
		String currentValue = getValue();
		for (var run: options) {
			String runValue = run.getValue();
			String selectedStr = "";
			if (currentValue != null) {
				selectedStr = currentValue.equals(runValue) ? " selected" : "";
			}
			result += "<option value='" + runValue + "'" + selectedStr + ">" + run.getText() + "</option>\n";
		}
		return result;
	}

	public void createOptionChildNodes(ObjectNode selectNode) {

		String currentValue = getValue();
		for (var run : options) {
			String runValue = run.getValue();
			String selectedStr = "";
			if (currentValue != null) {
				selectedStr = currentValue.equals(runValue) ? " selected" : "";
			}

			var optionNode = JsonUtils.createJsonNode("option", "value", runValue);
			if (currentValue != null) {
				if (currentValue.equals(runValue)) {
					addAttributeToAttributeSubnote(optionNode,"selected", null);
				}
			}
			JsonUtils.createTextChildNode(optionNode, run.getText());
			JsonUtils.addChildNode(selectNode, optionNode);

		}
	}
	public HtmlSelect linkCallback(GuiDelegate serverMsgHandler) {
		return (HtmlSelect) super.linkCallback(serverMsgHandler);
	}
	
}