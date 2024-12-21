package com.kewebsi.service;

import com.kewebsi.controller.*;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.html.PageState;

public class PageVarFloatColdLink extends PageStateVarColdLink<Float> {


    public PageVarFloatColdLink(PageState pageState) {
        super(pageState);
    }

    public PageVarFloatColdLink(PageState pageState, FieldAssistant fieldAssistant) {
        super(pageState, fieldAssistant);
    }

    public PageVarFloatColdLink(PageState pageState, SimpleFieldAssistant fieldAssistant, String fieldPrefix) {
        super(pageState, fieldAssistant, fieldPrefix);
    }

    @Override
    public void setValueFromBackend(BaseVal baseVal) {
        assert(baseVal.getType() == FieldAssistant.FieldType.LONG);
        val = ((BaseValFloat) baseVal).getVal();
        unparsedStringValue = null;
    }


    public String getValAsString() {
        return String.valueOf(val);
    }




}
