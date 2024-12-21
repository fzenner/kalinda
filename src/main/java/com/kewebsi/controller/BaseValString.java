package com.kewebsi.controller;

public class BaseValString extends BaseVal<String> {

    // protected final String val;

    public BaseValString(String val) {
        assert(val != null);
        this.val = val;
    }

//    public String getVal() {
//        return val;
//    }

    public FieldAssistant.FieldType getType() {
        return FieldAssistant.FieldType.STR;
    }

    @Override
    public String toHtml() {
        if (val == null) {
            return "";
        } else {
            return String.valueOf(val);
        }
    }

}
