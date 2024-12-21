package com.kewebsi.service;

import com.kewebsi.controller.*;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.html.PageState;

public class PageVarBoolColdLink extends PageStateVarColdLink<Boolean> {


    public PageVarBoolColdLink(PageState pageState) {
        super(pageState);
    }

    public PageVarBoolColdLink(PageState pageState, FieldAssistant fieldAssistant) {
        super(pageState, fieldAssistant);
    }



    public PageVarBoolColdLink(PageState pageState, SimpleFieldAssistant fieldAssistant, String fieldPrefix) {
        super(pageState, fieldAssistant, fieldPrefix);
    }


    @Override
    public void setValueFromBackend(BaseVal baseVal) {
        assert(baseVal.getType() == FieldAssistant.FieldType.LONG);
        val = ((BaseValBool) baseVal).getVal();
        unparsedStringValue = null;
    }


    public String getValAsString() {
        return String.valueOf(val);
    }




    public boolean getValidatedVal() throws PageVarError {
        //
        // If the field was already edited, the value is not null or the field is in error state.
        // If the field has not been edited yet, the  unparsedStringValue is null and we throw a PageVarError.
        //

        if (this.hasError()) {
            throw  new CodingErrorException("Attempt to get the value of a field with an error. " + " Error: " + this.getError().getErrorMsg() + " Field: " + this);
        }

        return val;
    }



    public void setVal(boolean val) {
        this.val = val;
    }


}
