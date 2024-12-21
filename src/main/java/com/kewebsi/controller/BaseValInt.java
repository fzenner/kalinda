package com.kewebsi.controller;

public class BaseValInt extends BaseVal<Integer> {

    // protected final int val;

    public BaseValInt(int val) {
        this.val = val;
    }

//    public int getVal() {
//        return val;
//    }

    public FieldAssistant.FieldType getType() {
        return FieldAssistant.FieldType.INT;
    }

    @Override
    public String toHtml() {
        return String.valueOf(val);
    }

}
