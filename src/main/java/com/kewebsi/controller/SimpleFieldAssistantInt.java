package com.kewebsi.controller;

import com.kewebsi.errorhandling.StringParsingError;
import com.kewebsi.html.ImplementationErrorException;
import com.kewebsi.util.CommonUtils;

import java.util.EnumSet;

public class SimpleFieldAssistantInt extends SimpleFieldAssistant<Integer>  {

    public SimpleFieldAssistantInt(Enum<?> fieldName) {
        super(fieldName, FieldAssistant.FieldType.INT, true);
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
