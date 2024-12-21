package com.kewebsi.service;

import com.kewebsi.controller.*;
import com.kewebsi.errorhandling.MalformedClientDataException;
import com.kewebsi.html.PageState;
import com.kewebsi.html.PageStateVarIntf;
import com.kewebsi.util.KewebsiDateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PageVarLocalDateColdLink extends PageStateVarColdLink<LocalDate> implements PageStateVarIntf<LocalDate> {


    public PageVarLocalDateColdLink(PageState pageState) {
        super(pageState);
    }

    public PageVarLocalDateColdLink(PageState pageState, FieldAssistant<?, LocalDate> fieldAssistant) {
        super(pageState, fieldAssistant);
    }

    public PageVarLocalDateColdLink(PageState pageState, SimpleFieldAssistant<LocalDate> fieldAssistant, String htmlIdPrefix) {
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

    public LocalDate getValCore() {  // TODO: Check if parent class method is enough
        if (hasError()) {
            throw new MalformedClientDataException("Invalid data in field " + fieldAssistant.getFieldLabel());
        }
        return val;
    }


    public LocalDate getValOrThrowError() throws PageVarError {
        if (hasError()) {
            throw getError();
        }
        return val;
    }


    public void setValueFromBackend(BaseVal baseVal) {
        if (baseVal != null) {
            assert (baseVal.getType() == FieldAssistant.FieldType.LOCALDATE);
            val = ((BaseValLocalDate) baseVal).getVal();
        } else {
            val = null;
        }
        unparsedStringValue = null;
    }



    @Override
    public String getValAsString() {
        return KewebsiDateUtils.printGermanDate(val);
    }
}
