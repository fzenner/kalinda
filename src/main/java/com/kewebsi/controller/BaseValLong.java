package com.kewebsi.controller;

public class BaseValLong extends BaseVal<Long> {

    // protected final long val;

    public BaseValLong(long val) {
        this.val = val;
    }

//    public long getVal() {
//        return val;
//    }

    public FieldAssistant.FieldType getType() {
        return FieldAssistant.FieldType.LONG;
    }

    @Override
    public String toHtml() {
        return String.valueOf(val);
    }

}
