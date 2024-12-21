package com.kewebsi.service;

import com.kewebsi.controller.FieldAssistant;
import com.kewebsi.controller.SimpleFieldAssistant;
import com.kewebsi.errorhandling.ExpectedClientDataError;

public class FieldError extends ExpectedClientDataError {
    SimpleFieldAssistant fieldAssistant;

    public FieldError(SimpleFieldAssistant fieldAssistant, String errorMsg, String badValue) {
        super(errorMsg, badValue);
        assert(fieldAssistant != null);
        this.fieldAssistant = fieldAssistant;
    }

    public SimpleFieldAssistant getFieldAssistant() {
        return fieldAssistant;
    }
}
