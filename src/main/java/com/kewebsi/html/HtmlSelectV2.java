package com.kewebsi.html;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.inmsg.ClientEventHandlerConsts;
import com.fzenner.datademo.web.inmsg.MsgFieldDataEntered;
import com.fzenner.datademo.web.inmsg.ServerMessageHandler;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.GuiDelegate;
import com.kewebsi.controller.StandardController;
import com.kewebsi.util.CommonUtils;
import com.kewebsi.util.JsonUtils;


public abstract class HtmlSelectV2 extends StringDisplay implements SelectionMadeHandler {

    protected ArrayList<HtmlSelectOption> options = new ArrayList<HtmlSelectOption>(0);

    public HtmlSelectV2(String id) {
        this.id = id;
    }

    public HtmlSelectV2 setOptions(ArrayList<HtmlSelectOption> options) {
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


    @Override
    public GuiDef getGuiDef() {
        var guiDef = new GuiDef("select", id);
        // guiDef.setServerCallback(StandardController.SMH_selectionMade);
        String valueStr = CommonUtils.nullToEmpty(this.getValue());
        guiDef.text = valueStr;


        ArrayList<String> optionsOut = new ArrayList<>(options.size());
        guiDef.state = optionsOut;
        for (var run : options) {
            String runValue = run.getValue();
            optionsOut.add(run.getValue());
        }
        return guiDef;
    }

    public String generateOptions() {
        var result = "";

        String currentValue = getValue();
        for (var run : options) {
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


    public MsgAjaxResponse handleSelectionEvent(MsgFieldDataEntered msg , UserSession userSession) {
        return MsgAjaxResponse.createSuccessMsg();
    }

}