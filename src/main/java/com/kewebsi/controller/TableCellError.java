package com.kewebsi.controller;

import com.fzenner.datademo.web.outmsg.ErrorInfoExtern;
import com.kewebsi.errorhandling.ErrorInfo;

public class TableCellError<T> {

    protected T entityWhenErrorWasSet;
    protected Enum errornousField;

    protected BaseVal valWhenErrorWasSet;
    protected String badValue;

    protected String message;


    public TableCellError(T entityWhenErrorWasSet, Enum errornousField, BaseVal valWhenErrorWasSet, String badValue, String message) {
        this.entityWhenErrorWasSet = entityWhenErrorWasSet;
        this.errornousField = errornousField;
        this.valWhenErrorWasSet = valWhenErrorWasSet;
        this.badValue = badValue;
        this.message = message;
    }


    public T getEntityWhenErrorWasSet() {
        return entityWhenErrorWasSet;
    }

    public Enum getErrornousField() {
        return errornousField;
    }

    public BaseVal getValWhenErrorWasSet() {
        return valWhenErrorWasSet;
    }

    public String getBadValue() {
        return badValue;
    }

    public String getMessage() {
        return message;
    }

    public ErrorInfo getErrorInfo() {
        return new ErrorInfo(message);
    }
}
