package com.kewebsi.html.search;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.CheckBoxGuiDef;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.errorhandling.DataOrError;
import com.kewebsi.errorhandling.ErrorUpdate;
import com.kewebsi.html.*;
import com.kewebsi.service.PageVarError;
import com.kewebsi.service.PageVarErrorCore;

import java.util.function.Function;

/**
 * TODO: Consider to not inherit from AbstractPageVarField in its current form, which focuses of getting its values by
 * string.
 * @param <T>
 */
public class HtmlCheckbox<T> extends AbstractPageVarField<Boolean> implements CheckBoxClickHandler {

    protected String fieldName;
    Function<Boolean, DataOrError<Boolean>> customClickAction = null;

    public HtmlCheckbox(PageStateVarIntf<Boolean> pageStateVar, String id) {
        this.pageStateVar = pageStateVar;
        this.id = id;
    }

    @Override
    public Boolean getValue() {
        return pageStateVar.getVal();
    }

    @Override
    public void setNonNullValueValidating(Boolean val) {
        pageStateVar.setValueFromClient(val, this);
        setClientIsSynced();
    }

    @Override
    public void setValueValidatingAllowNull(Boolean val) {
        pageStateVar.setValueFromClient(val, this);
        setClientIsSynced();
    }

    public ErrorUpdate getErrorInfoToDisplayToClient() {
        if (isDisabled()) {
            return null;
        }
        if (pageStateVar.hasEffectiveError()) {
            PageVarErrorCore error = pageStateVar.getEffectiveError();
            return error.getErrorInfo();
        }
        return null;
    }


    @Override
    public boolean clientNeedsRefresh() {
        // The client of this field is responsible for the formatting. No nicing by the server ever required.
        return false;
    }


    @Override
    public MsgAjaxResponse handleClick(boolean checked, JsonNode rootNode, UserSession userSession) {
        if (customClickAction != null) {
            var newValOrOrror = customClickAction.apply(checked);
            if (!newValOrOrror.hasError()) {
                pageStateVar.setValueFromClient(checked, this);
                return MsgAjaxResponse.createSuccessMsg();
            } else {
                return MsgAjaxResponse.createErrorMsg(newValOrOrror.getError().getErrorText());
            }
        } else {
            pageStateVar.setValueFromClient(checked, this);
            return MsgAjaxResponse.createSuccessMsg();
        }
    }


    @Override
    public GuiDef getGuiDef() {
        GuiDef guiDef = new GuiDef("kewebsi-checkbox", id);
        var val = pageStateVar.getVal();
        CheckBoxGuiDef checkBoxGuiDef = new CheckBoxGuiDef(val, isDisabled());
        guiDef.setTagSpecificData(checkBoxGuiDef);

        return guiDef;
    }

    public void setCustomClickAction(Function<Boolean, DataOrError<Boolean>> customClickAction) {
        this.customClickAction = customClickAction;
    }

}