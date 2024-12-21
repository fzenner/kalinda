package com.kewebsi.html;

import com.kewebsi.errorhandling.ErrorInfo;
import com.kewebsi.service.PageVarError;

public class HtmlRadioButton extends StringDisplay implements PageVarEditor<String> {


    /**
     * The name of the radio button also denotes the variable that is set with this element.
     */
    protected String name;
    protected Enum<?> valueIfSelected;
    protected String placeholder;


    protected boolean editable = true;


    protected ClientSyncState clientSyncState;


    public HtmlRadioButton(String id, String name, Enum<?> valueIfSelected) {
        this.id = id;
        this.name = name;
        this.valueIfSelected = valueIfSelected;
    }


    @Override
    /**
     * Returns the value of the linked PageStateVar is this variable exists.
     * If the variable does not yet exist yet, it is created if a PageState exists.
     * Returns the empty string if no PageState exists.
     */
    public String getValue() {
        PageStateVarIntf pageStateVar = getPageStateVar();
        if (pageStateVar != null) {
            String value = pageStateVar.getDisplayString();
            return value;
        } else {
            return "";
        }
    }


    @Override
    public void setNonNullValueValidating(String val) {
        PageStateVarIntf pageStateVar = getPageStateVar();
        // dtoAssistant.verifyValue(fieldName, val, entity);
        pageStateVar.setStringValueFromClient(val);
    }


    @Override
    public void setValueValidatingAllowNull(String val) {
        PageStateVarIntf pageStateVar = getPageStateVar();
        // dtoAssistant.verifyValue(fieldName, val, entity);
        pageStateVar.setStringValueFromClientAllowNull(val);
    }


    /**
     * Returns the PageStateVar if a PageState exists and the PageState has a registered variable, otherwise null.
     * @return A PageStateVar if a PageState exists, otherwise null.
     */
    public PageStateVarIntf getPageStateVar() {
        PageState pageState = getPageState();
        if (pageState != null) {
            PageStateVarIntf pageStateVar = pageState.getPageStateVar(name);
            return pageStateVar;
        }
        return null;
    }


    public void setName(String name) {
        this.name = name;
    }

    public boolean isEditable() {
        return editable;
    }

    public HtmlRadioButton setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }


    public Enum<?> getValueIfSelected() {
        return valueIfSelected;
    }

    public void setValueIfSelected(Enum<?> valueIfSelected) {
        this.valueIfSelected = valueIfSelected;
    }

    public String getName() {
        return name;
    }

    @Override
    public ErrorInfo getErrorInfoToDisplayToClient() {
        var pageStateVar = getPageStateVar();
        if (pageStateVar != null) {
            if (pageStateVar.hasEffectiveError()) {
                PageVarError error = pageStateVar.getEffectiveError();
                return error.getErrorInfo();
            }
        }
        return null;
    }

    @Override
    public ClientSyncState getClientSyncState() {
        return clientSyncState;
    }


}

