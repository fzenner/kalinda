package com.kewebsi.service;

import com.kewebsi.controller.BaseVal;
import com.kewebsi.controller.BaseValInt;
import com.kewebsi.controller.FieldAssistant;
import com.kewebsi.controller.SimpleFieldAssistant;
import com.kewebsi.html.PageState;

public class PageVarIntColdLink extends PageStateVarColdLink<Integer> {

    public PageVarIntColdLink(PageState pageState) {
        super(pageState);
    }

    public PageVarIntColdLink(PageState pageState, FieldAssistant fieldAssistant) {
        super(pageState, fieldAssistant);
    }




    public PageVarIntColdLink(PageState pageState, SimpleFieldAssistant fieldAssistant, String htmlIdPrefix) {
        super(pageState, fieldAssistant, htmlIdPrefix);
    }


    @Override
    public PageVarError validateUnparsedStringValueAndSetValueOrErrorAllowNull() {
        try {
            val = Integer.parseInt(getUnparsedStringValue().trim());
            var error = this.fieldAssistant.validate(val);
            if (error != null) {
                return new PageVarError(this, error);
            }
            clearError();
            unparsedStringValue = null;
        } catch(Exception e) {
            var newError = new PageVarError(this, "Not a valid number");
            newError.setErrorType(PageVarError.ErrorType.SERVER_SIDE_PARSING);
            setError(newError);
        }
        return getError();
    }


    @Override
    public String getValAsString() {
        return String.valueOf(val);
    }

    public Integer getValCore() {
        return val;
    }


    public Integer getValidatedVal() throws PageVarError {
        return val;
    }



    @Override
    public void setValueFromBackend(BaseVal baseVal) {
        assert(baseVal.getType() == FieldAssistant.FieldType.INT);
        val = ((BaseValInt) baseVal).getVal();
        unparsedStringValue = null;
    }


}
