package com.kewebsi.service;

import com.kewebsi.controller.BaseVal;
import com.kewebsi.controller.BaseValLong;
import com.kewebsi.controller.FieldAssistant;
import com.kewebsi.controller.SimpleFieldAssistant;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.html.PageState;

public class PageVarLongColdLink extends PageStateVarColdLink<Long> {


    public PageVarLongColdLink(PageState pageState) {
        super(pageState);
    }

    public PageVarLongColdLink(PageState pageState, FieldAssistant fieldAssistant) {
        super(pageState, fieldAssistant);
    }



    public PageVarLongColdLink(PageState pageState, SimpleFieldAssistant fieldAssistant, String fieldPrefix) {
        super(pageState, fieldAssistant, fieldPrefix);
    }


    @Override
    public void setValueFromBackend(BaseVal baseVal) {
        assert(baseVal.getType() == FieldAssistant.FieldType.LONG);
        val = ((BaseValLong) baseVal).getVal();
        unparsedStringValue = null;
    }


    public String getValAsString() {
        return String.valueOf(val);
    }




    public long getValidatedVal() throws PageVarError {
        //
        // If the field was already edited, the value is not null or the field is in error state.
        // If the field has not been edited yet, the  unparsedStringValue is null and we throw a PageVarError.
        //

        if (this.hasError()) {
            throw  new CodingErrorException("Attempt to get the value of a field with an error. " + " Error: " + this.getError().getErrorMsg() + " Field: " + this);
        }

        if (val == null) {
            String unparsedStringValue = getUnparsedStringValue();
            if (unparsedStringValue == null || unparsedStringValue.length() == 0) {
                throw new PageVarError(this, "Field must not be empty!");
            }
        }
        return val;
    }



    public void setVal(long val) {
        this.val = val;
    }


}
