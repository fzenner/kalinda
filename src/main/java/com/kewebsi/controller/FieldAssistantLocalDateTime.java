package com.kewebsi.controller;

import com.kewebsi.errorhandling.StringParsingError;
import com.kewebsi.html.dateeditor.CalendarController;
import com.kewebsi.util.KewebsiDateUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class FieldAssistantLocalDateTime<E> extends FieldAssistant<E, LocalDateTime> {

    public boolean isCreationTimeStamp = false;

    public FieldAssistantLocalDateTime(Enum<?> fieldName) {
        super(fieldName, FieldType.LOCALDATETIME);
    }

    public void setUnconvertedValue(E entity, Object value) {
        if (value instanceof Timestamp) {
            Timestamp ts = (Timestamp) value;
            LocalDateTime ldt = ts.toLocalDateTime();
            setter.accept(entity, ldt);
        } else {
            setter.accept(entity, (LocalDateTime) value);
        }
    }

    public boolean isCreationTimeStamp() {
        return isCreationTimeStamp;
    }

    public FieldAssistantLocalDateTime<E> setIsCreationTimeStamp(boolean isCreationTimeStamp) {
        this.isCreationTimeStamp = isCreationTimeStamp;
        return this;
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




