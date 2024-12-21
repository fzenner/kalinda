package com.kewebsi.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.FieldAssistantEnumIntf;
import com.kewebsi.errorhandling.ErrorInfo;
import com.kewebsi.service.PageVarError;
import com.kewebsi.util.CommonUtils;
import com.kewebsi.util.JsonUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;


public class HtmlSelectV3 extends StringDisplay implements PageVarEditor<String>, SelectionMadeHandler {

    protected PageStateVarIntf<Enum> pageStateVar;

    protected ArrayList<HtmlSelectOption> options = new ArrayList<HtmlSelectOption>(0);

    protected boolean optionsModified = false;


    boolean editable = true;

    protected ClientSyncState clientSyncState;

    public HtmlSelectV3(String id, PageStateVarIntf<Enum> pageStateVar) {
        this.id = id;
        this.pageStateVar = pageStateVar;

        addOption("", "");

        FieldAssistantEnumIntf faEnum = (FieldAssistantEnumIntf) pageStateVar.getFieldAssistant();
        EnumSet<?> enumSet = faEnum.getEnumSet();
        for (var en : enumSet) {
            addOption(en.name(), en.name());
        }

        init();



    }


    public HtmlSelectV3 setOptions(ArrayList<HtmlSelectOption> options) {
        this.options = options;
        init();
        return this;
    }


    public void init() {
    }


    public void addOption(String value, String text) {
        options.add(new HtmlSelectOption(value, text));
        optionsModified = true;
    }

    public void addOption(int valueAndDisplay) {
        String valueAndDisplayStr = Integer.toString(valueAndDisplay);
        options.add(new HtmlSelectOption(valueAndDisplayStr, valueAndDisplayStr));
        optionsModified = true;
    }


    @Override
    public GuiDef getGuiDef() {
        var guiDef = new GuiDef("select", id);
        // guiDef.setServerCallback(StandardController.SMH_selectionMade);
        String valueStr = CommonUtils.nullToEmpty(this.getValue());
        guiDef.addAttribute("value", valueStr);

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
        for (var optionRun : options) {
            String optionRunValue = optionRun.getValue();
            String selectedStr = "";
            if (currentValue != null) {
                selectedStr = currentValue.equals(optionRunValue) ? " selected" : "";
            }
            result += "<option value='" + optionRunValue + "'" + selectedStr + ">" + optionRun.getText() + "</option>\n";
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

    @Override
    public void setNonNullValueValidating(String val) {
        pageStateVar.setStringValueFromClient(val);
    }

    @Override
    public void setValueValidatingAllowNull(String val) {
        pageStateVar.setStringValueFromClientAllowNull(val);
    }

    @Override
    public PageStateVarIntf<Enum> getPageStateVar() {
        return pageStateVar;
    }

    @Override
    public String getValue() {
        var val = pageStateVar.getVal();
        return val == null ? "" : val.name();
    }

    @Override
    public boolean isContentOrGuiDefModified() {
        return optionsModified;
    }

    @Override
    public void setContentOrGuiDefNotModified() {
        optionsModified = false;
    }

    @Override
    public boolean isAttributesModified() {
        return valueModified();
    }


    public boolean valueModified() {
        return ! getValue().equals(oldValue);
    }

    @Override
    public void setAttributesNotModified() {
        oldValue = getValue();
    }


    @Override
    public HashMap<String, AttributeModification> getAttributeModifications() {
        final String checked = "checked";
        if (valueModified()) {
            HashMap<String, AttributeModification> result = new HashMap<>(1);
            AttributeModification mod = new AttributeModification("value", AttributeModification.Modification.MODIFIED, getValue());
            result.put(checked, mod);
            return result;
        }
        return null;
    }

    @Override
    public ErrorInfo getErrorInfoToDisplayToClient() {
        if (pageStateVar.hasEffectiveError()) {
            PageVarError error = pageStateVar.getEffectiveError();
            return error.getErrorInfo();
        }
        return null;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }


    public boolean isEditable() {
        if (pageStateVar.isMeaningless()) {
            return false;
        }
        return editable;
    }


    @Override
    public MsgAjaxResponse handleSelectionEvent(String newValue, JsonNode rootNode, UserSession userSession) {
        setNonNullValueValidating(newValue);
        return MsgAjaxResponse.createSuccessMsg();
    }

    @Override
    public ClientSyncState getClientSyncState() {
        return clientSyncState;
    }

}