package com.kewebsi.controller;

import com.kewebsi.errorhandling.StringParsingError;

import java.math.BigInteger;

public class FieldAssistantLong<E> extends FieldAssistant<E, Long> {


    public FieldAssistantLong(Enum<?> fieldName) {
        super(fieldName, FieldType.LONG);
    }


    @Override
    public void setUnconvertedValue(E entity, Object value) {
        if (value instanceof BigInteger) {
            BigInteger bigIntVal = (BigInteger) value;
            long longVal = bigIntVal.longValueExact();
            setter.accept(entity, longVal);
        } else {
            setter.accept(entity, (Long) value);
        }
    }


    @Override
    public Long parseToObject(String str) throws StringParsingError {
        try {
            return Long.parseLong(str);
        } catch(Exception e) {
          throw new StringParsingError(e);
        }
    }
}
