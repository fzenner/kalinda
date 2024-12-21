package com.kewebsi.controller;

import com.kewebsi.errorhandling.StringParsingError;

import java.math.BigInteger;

public class FieldAssistantInt<E> extends FieldAssistant<E, Integer> {


    public FieldAssistantInt(Enum<?> fieldName) {
        super(fieldName, FieldType.INT);
    }


    @Override
    public void setUnconvertedValue(E entity, Object value) {
        if (value == null) {
            setter.accept(entity, null);
        } else {
            if (value instanceof BigInteger) {
                BigInteger bigIntVal = (BigInteger) value;
                long longVal = bigIntVal.longValueExact();
                setter.accept(entity, (int) longVal);
            } else {
                setter.accept(entity, (int) value);
            }
        }
    }

    @Override
    public Integer parseToObject(String str) throws StringParsingError {
        try {
            return Integer.parseInt(str);
        } catch(Exception e) {
            throw new StringParsingError(e);
        }
    }

}
