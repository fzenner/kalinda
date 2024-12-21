package com.kewebsi.controller;

import com.kewebsi.errorhandling.StringParsingError;
import jdk.jshell.spi.ExecutionControl;
import org.apache.commons.lang3.NotImplementedException;

public class SimpleFieldAssistantBool extends SimpleFieldAssistant<Boolean>  {

    public SimpleFieldAssistantBool(Enum<?> fieldName) {
        super(fieldName, FieldAssistant.FieldType.INT, true);
    }

    @Override
    public Boolean parseToObject(String str) throws StringParsingError {
        throw new NotImplementedException("Not implemented");
    }

}
