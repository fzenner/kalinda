package com.kewebsi.controller;

public class BaseValBool extends BaseVal<Boolean> {


    public BaseValBool(boolean val) {
        this.val = val;
    }


    public FieldAssistant.FieldType getType() {
        return FieldAssistant.FieldType.LONG;
    }

    @Override
    public String toHtml() {
        return String.valueOf(val);
    }

}
