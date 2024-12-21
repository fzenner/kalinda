package com.kewebsi.controller;

public class BaseValFloat extends BaseVal<Float> {

    public BaseValFloat(float val) {
        this.val = val;
    }


    public FieldAssistant.FieldType getType() {
        return FieldAssistant.FieldType.FLOAT;
    }

    @Override
    public String toHtml() {
        return String.valueOf(val);
    }

}
