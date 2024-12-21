package com.kewebsi.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.inmsg.MsgCalendarEditorPopupCreate;
import com.fzenner.datademo.web.inmsg.MsgDateTimeEntered;
import com.fzenner.datademo.web.outmsg.*;
import com.kewebsi.errorhandling.ErrorInfo;
import com.kewebsi.errorhandling.MalformedClientDataException;
import com.kewebsi.html.dateeditor.CalendarPopupHelper;
import com.kewebsi.service.PageVarError;
import com.kewebsi.util.JsonUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class HtmlPageVarFieldDateTime extends AbstractPageVarField<LocalDateTime> implements  GuiObjChangeHandler, CustomGuiEventHandler  {

    // protected PageStateVarIntf<LocalDateTime> pageStateVar;

    protected LocalDateTime valueOld;
    protected PageVarError errorOld = null;

    protected LocalTime defaultTimeWhenDateIsSet;


    public HtmlPageVarFieldDateTime(PageStateVarIntf<LocalDateTime> pageStateVar, String idPrefix) {
        this.pageStateVar = pageStateVar;
        this.id = idPrefix + pageStateVar.getPageVarId();
    }


    @Override
    public GuiDef getGuiDef() {

        GuiDef guiDef = new GuiDef(getTagName() , id, getVisibility(), getErrorInfoToDisplayToClient());

        LocalDateTime ldt = pageStateVar.getVal();
        DateTimeWireOrNull dtw = localDateTimeToDateTimeWire(ldt);
        DateTimeFieldGuiDef inputFieldGuiDef = new DateTimeFieldGuiDef(dtw, isRequired(), isDisabled(), new TimeWireOrNull(TimeWire.from(defaultTimeWhenDateIsSet)));
        guiDef.setTagSpecificData(inputFieldGuiDef);
        if (pageStateVar.hasEffectiveServerSideError()) {
            guiDef.errorInfo = new ErrorInfo(pageStateVar.getEffectiveError().getErrorMsg());
        }
        return guiDef;
    }



    public GuiDef getGuiDefUpdate() {
        GuiDef guiDef = new GuiDef(getTagName(), getId(), calculateModificationOfVisibility(), calculateModificationOfErrorInfoToSendToClient());
        guiDef.setUpdateMode(GuiDef.UpdateMode.MODIFICATIONS_ONLY);

        var modificationOfValue = calculateModificationOfValue();
        DateTimeWireOrNull dtw = modificationOfValue != null ? localDateTimeToDateTimeWire(modificationOfValue) : null;

        Boolean modificationOfRequired = calculateModificationOfRequired();
        Boolean modificationOfDisabled = calculateModificationOfDisabled();

        DateTimeFieldGuiDef inputFieldGuiDef = new DateTimeFieldGuiDef(dtw, modificationOfRequired, modificationOfDisabled, new TimeWireOrNull(defaultTimeWhenDateIsSet));

        guiDef.setTagSpecificData(inputFieldGuiDef);

        if (pageStateVar.hasEffectiveServerSideError()) {
            guiDef.errorInfo = new ErrorInfo(pageStateVar.getEffectiveError().getErrorMsg());
        }

        return guiDef;
    }



    @Override
    public boolean clientNeedsRefresh() {
        // The client of this field is responsible for the formatting. No nicing by the server ever required.
        return false;
    }


    public LocalDateTime getValue() {
        return pageStateVar.getValCore();
    }

    @Override
    public void setContentOrGuiDefNotModified() {
        valueOld = getValue();
        errorOld = pageStateVar.getEffectiveError();
    }


    @Override
    public String getTagName() {
        return "kewebsi-datetime-editor";
    }


    @Override
    public MsgAjaxResponse handleCustomGuiEvent(JsonNode rootNode, UserSession userSession) {
        String command = rootNode.get("command").textValue();
        var response = switch (command) {
            case "CALENDAR_POPUP_CREATE" -> createrCalendarPopup(rootNode);
            case "CALENDAR_POPUP_UPDATE" -> updateCalendarPopup(rootNode);
            default -> MsgAjaxResponse.createErrorMsg("Unknowm command: " + command);
        };
        return response;
    }

    protected MsgAjaxResponse createrCalendarPopup(JsonNode rootNode) {
        MsgCalendarEditorPopupCreate msg = JsonUtils.parseJsonFromClient(rootNode, MsgCalendarEditorPopupCreate.class);

        var now = LocalDate.now();
        int year = msg.year != null ? msg.year : now.getYear();
        int month = msg.month  != null ? msg.month : now.getMonthValue();
        // int day = msg.day != null ? msg.day : now.getDayOfMonth();

        var calenderGuiDef = CalendarPopupHelper.getCalendarGuiDef(year, month);

        var response = MsgAjaxResponse.createSuccessMsg();
        response.setCustomCallbackData(calenderGuiDef);
        return response;
    }

    protected MsgAjaxResponse updateCalendarPopup(JsonNode rootNode) {
        return CalendarPopupHelper.updateCalendarPopup(rootNode);
    }

    @Override
    public ErrorInfo getErrorInfoToDisplayToClient() {
        return calculateModificationOfErrorInfoToSendToClient(null, getEffectiveError());
    }

    @Override
    public MsgAjaxResponse inputChanged(JsonNode rootNode, UserSession userSession) {
        MsgDateTimeEntered msg = JsonUtils.parseJsonFromClient(rootNode, MsgDateTimeEntered.class);


        try {
            MsgDateTimeEntered.InputElementStateInfoFromClientToServer clientInputState = MsgDateTimeEntered.InputElementStateInfoFromClientToServer.valueOf(msg.clientInputState);

            switch (clientInputState) {
                case CLIENT_INPUT_EMPTY -> {
                    setValueToNull();
                    setClientIsSynced();
                }

                case CLIENT_INPUT_NOT_TRANSMISSABLE -> {
                    pageStateVar.setClientSideIncompleteOrParsingError();
                    setClientIsNotSynced(ClientSyncState.CLIENT_INPUT_UNPARSEABLE_ON_CLIENT);
                }
                case CLIENT_INPUT_FILLED_OK -> {
                    DateTimeWireOrNull dtw = msg.value;

                    LocalDateTime ldt = null;
                    boolean valueWasParsed = false;
                    try {
                        ldt = dateTimeWireToLocalDateTime(dtw);
                        valueWasParsed = true;
                    } catch(Exception e) {
                        pageStateVar.setServerSideParsingError(e.getMessage());
                        setClientIsNotSynced(ClientSyncState.CLIENT_INPUT_UNPARSEABLE_ON_SERVER);
                    }

                    if (valueWasParsed) {
                        setValueValidatingAllowNull(ldt);
                        setClientIsSynced();
                    }

                }
            }
            return MsgAjaxResponse.createSuccessMsg();
        } catch (Exception e) {
            throw new MalformedClientDataException(e);
        }
    }

    @Override
    public void setNonNullValueValidating(LocalDateTime val) {
        pageStateVar.setValueFromClient(val);
        setClientIsSynced();

    }

    @Override
    public void setValueValidatingAllowNull(LocalDateTime val) {
        pageStateVar.setValueFromClient(val);  // TODO: Review null handling.
        setClientIsSynced();
    }

    public void setValueToNull() {
        pageStateVar.setValueFromClient(null);  // TODO: Review null handling.
        setClientIsSynced();
    }


    @Override
    public PageStateVarIntf getPageStateVar() {
        return pageStateVar;
    }

    public static LocalDateTime dateTimeWireToLocalDateTime(DateTimeWireOrNull dtwOrNull) {
        if (dtwOrNull == null) {
            return  null;
        }
        var dtw = dtwOrNull.value();
        return LocalDateTime.of(dtw.y(), dtw.mo(), dtw.d(), dtw.h(), dtw.mi(), dtw.s(), dtw.n());
    }

    /**
     * Never returns null.
     * @param ldt
     * @return
     */
    public static DateTimeWireOrNull localDateTimeToDateTimeWire(LocalDateTime ldt) {
        if (ldt == null) {
            return new DateTimeWireOrNull(null);
        }
        return new DateTimeWireOrNull(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(),
                ldt.getHour(), ldt.getMinute(), ldt.getSecond(), ldt.getNano());
    }


    public LocalTime getDefaultTimeWhenDateIsSet() {
        return defaultTimeWhenDateIsSet;
    }

    public void setDefaultTimeWhenDateIsSet(LocalTime defaultTimeWhenDateIsSet) {
        this.defaultTimeWhenDateIsSet = defaultTimeWhenDateIsSet;
    }


}

