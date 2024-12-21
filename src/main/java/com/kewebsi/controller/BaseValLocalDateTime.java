package com.kewebsi.controller;

import java.time.LocalDateTime;

public class BaseValLocalDateTime extends BaseVal<LocalDateTime> {

    public BaseValLocalDateTime(LocalDateTime localDate) {
        this.val = localDate;
    }


    public FieldAssistant.FieldType getType() {
        return FieldAssistant.FieldType.LOCALDATETIME;
    }


    @Override
    public String toHtml() {
        return String.valueOf(val);  // TODO: Make sure that we do not transfer seconds or milliseconds where we do not expect it.
    }

}
