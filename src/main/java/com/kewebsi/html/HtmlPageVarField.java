package com.kewebsi.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.inmsg.MsgDateTimeEntered;
import com.fzenner.datademo.web.inmsg.MsgFieldDataEntered;
import com.fzenner.datademo.web.outmsg.DateTimeWireOrNull;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.fzenner.datademo.web.outmsg.InputFieldGuiDef;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.errorhandling.ErrorInfo;
import com.kewebsi.errorhandling.MalformedClientDataException;
import com.kewebsi.service.PageVarError;
import com.kewebsi.util.CommonUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class HtmlPageVarField extends AbstractPageVarField<String> implements InputChangeHandler {


    // protected PageStateVarIntf pageStateVar;
    protected String name;
    protected String placeholder;

    protected int htmlSize = -1;


    // protected String valueOld;
    // protected boolean editableOld;
    // protected boolean disabledOld;
    // protected boolean requiredOld;


    /**
     * It is possible, that the value on the client has changed, (for example a blank or a trailing zero was added),
     * but both the effective old value and the effective  new value are the same (when they are formatted)
     * In this case,  we want to update the client with the nice formatted value.
     * Hence we need to be able to indicate, that the value, in this case just the exact representation
     * has changed.
     */
    protected String lastReceivedVerbatimValue;


    public static final String SMART_INPUT_DIV_CLASS = "smartInputDiv";


    public HtmlPageVarField(PageStateVarIntf pageStateVar, String id) {
        this.pageStateVar = pageStateVar;
        this.id = id;
        setOldValuesToCurrentValues();
    }

    public HtmlPageVarField(PageStateVarIntf pageStateVar) {
        this.pageStateVar = pageStateVar;
        this.id = pageStateVar.getPageVarId();
        setOldValuesToCurrentValues();
    }




    @Override
    public GuiDef getGuiDef() {
        GuiDef guiDef = new GuiDef(getTagName(), id, getVisibility(),calcErrorInfo());
        String typeStr = pageStateVar.getFieldAssistant().getFieldType().name();
        InputFieldGuiDef inputFieldGuiDef = new InputFieldGuiDef(typeStr, htmlSize, name, placeholder, getValue(), isRequired(), isDisabled());
        guiDef.setTagSpecificData(inputFieldGuiDef);
        return guiDef;
    }


    public GuiDef getGuiDefUpdateFull() {
        GuiDef guiDef = getGuiDef();
        guiDef.setUpdateMode(GuiDef.UpdateMode.FULL);
        return guiDef;
    }


    @Override
    public GuiDef getGuiDefUpdate() {

        GuiDef guiDef = new GuiDef(getTagName(), getId(), calculateModificationOfVisibility(), calculateModificationOfErrorInfoToSendToClient());
        guiDef.setUpdateMode(GuiDef.UpdateMode.MODIFICATIONS_ONLY);



        // setVisibilityIfVisibilityModified(guiDef);
        // setErrorInfoIfErrorInfoModified(guiDef);


        Boolean modificationOfRequired = calculateModificationOfRequired();
        Boolean modificationOfDisabled = calculateModificationOfDisabled();

        // !!!!!!!!!!!!! Clear the values when disabled and errors exist!!!
        if (modificationOfDisabled != null) {
            if (modificationOfDisabled == true) {
                // When here, we freshly disabled the field.
                if (getRawError() != null) {         // XXXXX HOTSPOT
                    getPageStateVar().clear();
                    getPageStateVar().clearError();
                }
            }
        }
        String modificationOfValue = calculateModificationOfValue();



        /**
         * If we change size or placeholder, we need to replace the null parameter with the current values.
         * But in most cases that is not done and increases the noise on the wire.
         */
        String typeStr = pageStateVar.getFieldAssistant().getFieldType().name();
        InputFieldGuiDef inputFieldGuiDef = new InputFieldGuiDef(typeStr, null, null, null, modificationOfValue, modificationOfRequired, modificationOfDisabled);
        guiDef.setTagSpecificData(inputFieldGuiDef);

        return guiDef;

    }


    public String getValue() {
        return CommonUtils.modelToHtml(pageStateVar.getDisplayString());
    }


    /*  It is possible, that the value on the client has changed, (for example a blank or a trailing zero was added),
        but both the old value and the new semantic value are the same
        Than we want to update the client with the nice formatted value.
        Hence we indicate here, that the value, in this case just the exact representation, has changed.
     */
    // has changed.
    @Override
    public boolean clientNeedsRefresh() {
        if (!CommonUtils.equals(getValue(), lastReceivedVerbatimValue)) {
            return true;
        }
        return false;
    }

    @Override
    public void setGuiDoesNotNeedRefresh() {
        lastReceivedVerbatimValue = getValue();
    }








    public String getLastReceivedVerbatimValue() {
        return lastReceivedVerbatimValue;
    }

    public void setNonNullValueValidating(String val) {
        this.lastReceivedVerbatimValue = val;
        pageStateVar.setStringValueFromClient(val);
        setClientIsSynced();
    }

    @Override
    public void setValueValidatingAllowNull(String val) {
        this.lastReceivedVerbatimValue = val;
        pageStateVar.setStringValueFromClientAllowNull(val);
        setClientIsSynced();
    }


    /**
     * Returns the PageStateVar if a PageState exists and the PageState has a registered variable, otherwise null.
     *
     * @return A PageStateVar if a PageState exists, otherwise null.
     */
    public PageStateVarIntf getPageStateVar() {
        return pageStateVar;
    }


    public void setName(String name) {
        this.name = name;
    }


    /**
     * Note that editable (HTML contentEditable) is not effective on HTML input fields.
     * TODO: Review, possibly remove
     *
     * @return
     */


    public int getHtmlSize() {
        return htmlSize;
    }

    public HtmlPageVarField setHtmlSize(int htmlSize) {
        this.htmlSize = htmlSize;
        return this;
    }


    @Override
    public ArrayList<String> getDynamicCssClasses() {

        if (hasError()) {
            var result = new ArrayList<String>(1);
            result.add("textInputWithError");
            return result;

        } else {
            var result = new ArrayList<String>(1);
            result.add("textInput");
            return result;

        }
    }


    @Override
    public ErrorInfo getErrorInfoToDisplayToClient() {
        if (isDisabled()) {
            return null;
        }
        if (pageStateVar.hasEffectiveError()) {
            PageVarError error = pageStateVar.getEffectiveError();
            return error.getErrorInfo();
        }
        return null;
    }


    @Override
    public MsgAjaxResponse inputChanged(MsgFieldDataEntered newInput, JsonNode rootNode, UserSession userSession) {

        try {
            MsgDateTimeEntered.InputElementStateInfoFromClientToServer clientInputState = MsgDateTimeEntered.InputElementStateInfoFromClientToServer.valueOf(newInput.clientInputState);
            switch (clientInputState) {
                case CLIENT_INPUT_EMPTY -> {
                    setValueValidatingAllowNull(newInput.value);
                    setClientIsSynced();
                }

                case CLIENT_INPUT_NOT_TRANSMISSABLE -> {
                    pageStateVar.setClientSideIncompleteOrParsingError();
                    setClientIsNotSynced(ClientSyncState.CLIENT_INPUT_UNPARSEABLE_ON_CLIENT);
                }
                case CLIENT_INPUT_FILLED_OK -> {
                    setValueValidatingAllowNull(newInput.value);
                    setClientIsSynced();
                }
            }
            return MsgAjaxResponse.createSuccessMsg();
        } catch (Exception e) {
            throw new MalformedClientDataException(e);
        }
    }




    @Override
    public String getTagName() {
        return "kewebsi-input";
    }



}

