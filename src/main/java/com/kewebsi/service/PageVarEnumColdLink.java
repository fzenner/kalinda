package com.kewebsi.service;

import com.kewebsi.controller.*;
import com.kewebsi.html.PageState;

public class PageVarEnumColdLink extends PageStateVarColdLink<Enum<?>> {

    protected Class enumClass;

    // protected Enum<?> val;


    public PageVarEnumColdLink(PageState pageState) {
        super(pageState);
    }

    public PageVarEnumColdLink(PageState pageState, FieldAssistant fieldAssistant) {
        super(pageState, fieldAssistant);
    }



    @Override
    public void setValueFromBackend(BaseVal baseVal) {
        assert(baseVal.getType() == FieldAssistant.FieldType.ENM);
        val = ((BaseValEnm) baseVal).getVal();
        unparsedStringValue = null;
    }


    public PageVarEnumColdLink(PageState pageState, SimpleFieldAssistant fieldAssistant, String htmlIdPrefix) {
        super(pageState, fieldAssistant, htmlIdPrefix);
        FieldAssistantEnumIntf faEnum = (FieldAssistantEnumIntf) fieldAssistant;
        this.enumClass = faEnum.getEnumClass();
    }




//    @Override
//    public PageVarError validateUnparsedStringValueAndSetValueOrError() {
//
//        String strVal = getUnparsedStringValue();
//
//        PageVarError localError;  // Use local variable in order to compiler-check for not-null;
//        if (!CommonUtils.hasInfo(strVal)) {
//            if (!fieldAssistant.canBeEmpty()) {
//                val = null;  // We set the null value, although it is not a valid value. Otherwise the old (valid but by the user unintended) value stays in the field.
//                localError = new PageVarError(this, "Enumeration value must be set.");
//            } else {
//                val = null;
//                localError = null;
//                unparsedStringValue = null;
//            }
//        } else {
//            Enum<?> checkedVal = EnumUtils.getEnumFromString(enumClass, strVal);
//            if (checkedVal == null) {
//                localError = new PageVarError(this, "Not a valid enumeration value.");
//            } else {
//                // When here, the string value is good.
//                val = checkedVal;
//                localError = null;
//                unparsedStringValue = null;
//            }
//        }
//        setError(localError);
//        return localError;
//    }


    public Enum<?> getValCore() {
        return val;
    }


    public Enum<?> getValidatedVal() throws PageVarError {
        if (hasError()) {
            throw getError();
        }
        return val;
    }



    public void setValueCore(Enum<?> val) {
        super.setValueCore(val);
        clearError();
    }

    @Override
    public String getValAsString() {
        return val.name();
    }


}
