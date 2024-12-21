package com.kewebsi.controller;

import com.kewebsi.service.FieldError;

public class ParseResult<F> {
    public final F value;
    public final FieldError fieldError;

    public ParseResult(FieldError fieldError) {
        this.value = null;
        this.fieldError = fieldError;
    }

    public ParseResult(F value) {
        this.value = value;
        this.fieldError = null;
    }

    public static <F> ParseResult onFieldError(SimpleFieldAssistant<F> fieldAssistant, String errorMsg, String badValue) {
        return new ParseResult<F>(new FieldError(fieldAssistant, errorMsg, badValue));
    }

    public boolean hasError() {
        return fieldError!=null;
    }

    public boolean isOk() {
        return fieldError==null;
    }


}
