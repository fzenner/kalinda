package com.kewebsi.controller;

import java.time.LocalDate;
import java.time.LocalTime;

public class BaseValLocalTime extends BaseVal<LocalTime> {

    public BaseValLocalTime(LocalTime localTime) {
        this.val = localTime;
    }


    public FieldAssistant.FieldType getType() {
        return FieldAssistant.FieldType.LOCALTIME;
    }


    @Override
    public String toHtml() {
        return String.valueOf(val);
    }

}
