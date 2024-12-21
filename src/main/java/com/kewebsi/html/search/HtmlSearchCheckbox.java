package com.kewebsi.html.search;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.CheckBoxGuiDef;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.FieldAssistant;
import com.kewebsi.controller.StandardController;
import com.kewebsi.controller.StaticGuiDelegate;
import com.kewebsi.html.*;

import java.util.HashMap;
import java.util.function.Function;

public class HtmlSearchCheckbox<T> extends HtmlTag implements CheckBoxClickHandler {

    protected String clientEventHandler;
    protected StaticGuiDelegate serverMsgHandler;

    protected String fieldName;
    protected HtmlAtomicConstraintRow htmlAtomicSearchCosntraintRow;

    protected Function<?, EntityNavigatorState<T>> esg;

    // protected FieldAssistant fieldAssistant;
    protected AtomicSearchConstraint atomicSearchConstraint;

    protected boolean checkedOldValue = false;


    public HtmlSearchCheckbox(String id, AtomicSearchConstraint atomicSearchConstraint, HtmlAtomicConstraintRow htmlAtomicSearchCosntraintRow) {
        this.id = id;
        this.atomicSearchConstraint = atomicSearchConstraint;
        this.htmlAtomicSearchCosntraintRow = htmlAtomicSearchCosntraintRow;
        initEventHandling();
    }


    @Override
    public MsgAjaxResponse handleClick(boolean checked, JsonNode rootNode, UserSession userSession) {
        atomicSearchConstraint.setConstraintIsActive(checked);
        return MsgAjaxResponse.createSuccessMsg();
    }


    public boolean isChecked() {
        return atomicSearchConstraint.isConstraintIsActive();
    }


    protected void initEventHandling() {
        this.clientEventHandler = "CEH_checkBoxClicked";
        this.serverMsgHandler = StandardController.SMH_checkBoxClicked;
    }


    @Override
    public GuiDef getGuiDef() {
        GuiDef guiDef = new GuiDef("kewebsi-checkbox", id);
        CheckBoxGuiDef checkBoxGuiDef = new CheckBoxGuiDef(isChecked(), false);
        guiDef.setTagSpecificData(checkBoxGuiDef);
        return guiDef;
    }


    @Override
    public boolean isContentOrGuiDefModified() {
        return false;
    }

    @Override
    public void setContentOrGuiDefNotModified() {

    }

    @Override
    public boolean isAttributesModified() {
        return isChecked() != checkedOldValue;
    }

    @Override
    public void setAttributesNotModified() {
        checkedOldValue = isChecked();
    }


    @Override
    public HashMap<String, AttributeModification> getAttributeModifications() {
        final String checked = "checked";
        if (isChecked() != checkedOldValue) {
            HashMap<String, AttributeModification> result = new HashMap<>(1);
            AttributeModification mod;
            if (isChecked()) {
                mod = new AttributeModification(checked, AttributeModification.Modification.MODIFIED, "yes");
            } else {
                mod = new AttributeModification(checked, AttributeModification.Modification.MODIFIED, null);
            }
            result.put(checked, mod);
            return result;
        }
        return null;
    }

    public AtomicSearchConstraint getAtomicSearchConstraint() {
        return atomicSearchConstraint;
    }


}