package com.kewebsi.controller;

public class BaseValEnm extends BaseVal<Enum> {

    // protected final Enum val;

    public BaseValEnm(Enum val) {
        assert(val != null);
        this.val = val;
    }

    @Override
    public Enum<?> getVal() {
        return val;
    }

    public FieldAssistant.FieldType getType() {
        return FieldAssistant.FieldType.ENM;
    }

    @Override
    public String toHtml() {
        return String.valueOf(val);
    }

}
