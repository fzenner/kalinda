package com.kewebsi.service;

import com.kewebsi.controller.*;
import com.kewebsi.errorhandling.MalformedClientDataException;
import com.kewebsi.html.PageState;
import com.kewebsi.util.KewebsiDateUtils;

import java.time.LocalDate;
import java.time.LocalTime;

public class PageVarLocalTimeColdLink extends PageStateVarColdLink<LocalTime> {


    public PageVarLocalTimeColdLink(PageState pageState) {
        super(pageState);
    }

    public PageVarLocalTimeColdLink(PageState pageState, FieldAssistant<?, LocalDate> fieldAssistant) {
        super(pageState, fieldAssistant);
    }

    public PageVarLocalTimeColdLink(PageState pageState, SimpleFieldAssistant<LocalDate> fieldAssistant, String htmlIdPrefix) {
        super(pageState, fieldAssistant, htmlIdPrefix);
    }

//
//    @Override
//    public PageVarError validateUnparsedStringValueAndSetValueOrError() {
//        try {
//            val = KewebsiDateUtils.parseGermanDate(getUnparsedStringValue().trim());
//            resetError();
//            unparsedStringValue = null;
//        } catch(Exception e) {
//            setError(new PageVarError(this, "Date format is not valid."));
//        }
//        return getError();
//    }

    public LocalTime getValCore() {  // TODO: Check if parent class method is enough
        if (hasError()) {
            throw new MalformedClientDataException("Invalid data in field " + fieldAssistant.getFieldLabel());
        }
        return val;
    }


    public LocalTime getValOrThrowError() throws PageVarError {
        if (hasError()) {
            throw getError();
        }
        return val;
    }


    public void setValueFromBackend(BaseVal baseVal) {
        if (baseVal != null) {
            assert (baseVal.getType() == FieldAssistant.FieldType.LOCALDATE);
            val = ((BaseValLocalTime) baseVal).getVal();
        } else {
            val = null;
        }
        unparsedStringValue = null;
    }



    @Override
    public String getValAsString() {
        return KewebsiDateUtils.printTime24h(val);
    }
}
