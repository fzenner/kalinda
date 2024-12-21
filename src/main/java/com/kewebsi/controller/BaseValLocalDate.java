package com.kewebsi.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BaseValLocalDate extends BaseVal<LocalDate> {

    public BaseValLocalDate(LocalDate localDate) {
        this.val = localDate;
    }


    public FieldAssistant.FieldType getType() {
        return FieldAssistant.FieldType.LOCALDATE;
    }


    @Override
    public String toHtml() {
        return String.valueOf(val);
    }

}
