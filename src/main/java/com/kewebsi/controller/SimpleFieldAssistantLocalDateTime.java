package com.kewebsi.controller;

import com.kewebsi.errorhandling.StringParsingError;
import com.kewebsi.html.dateeditor.CalendarController;
import com.kewebsi.util.KewebsiDateUtils;

import java.time.LocalDateTime;

public class SimpleFieldAssistantLocalDateTime extends SimpleFieldAssistant<LocalDateTime>  {

    public SimpleFieldAssistantLocalDateTime(Enum<?> fieldName) {
        super(fieldName, FieldAssistant.FieldType.LOCALDATETIME, true);
    }

    @Override
    public LocalDateTime parseToObject(String str) throws StringParsingError {
        try {
            return KewebsiDateUtils.parseDateTime(str, false, false, CalendarController.YEAR_DIGITS.Required);
        } catch(Exception e) {
            throw new StringParsingError(e);
        }
    }

}
