package com.kewebsi.controller;

import com.kewebsi.errorhandling.StringParsingError;
import com.kewebsi.service.FieldError;

public class FieldAssistantStr<E> extends FieldAssistant<E, String> {
    protected int minLength;

    protected int maxLength = -1;


    public FieldAssistantStr(Enum<?> fieldName) {
        super(fieldName, FieldType.STR);
    }

    public FieldAssistantStr(Enum<?> fieldName, boolean editable) {
        super(fieldName, FieldType.STR, editable);
    }



    public int getMinLength() {
        return minLength;
    }

    public FieldAssistantStr setMinLength(int minLength) {
        assert(minLength >= 0);
        this.minLength = minLength;
        if (minLength > 0) {
            setCanBeNull(false);
        }
        return this;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public FieldAssistantStr setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    @Override
    public FieldError validateStr(String val) {
        FieldError result = null;
        int length =  val.trim().length();
        if (val.length() < minLength) {
            return new FieldError(this,"Field " + fieldLabel + " must have at least " + minLength + " characters.", val);
        }
        if (maxLength>=0) {
            if (val.length() > maxLength) {
                return new FieldError(this, "Field " + fieldLabel + " can have at most " + maxLength + " characters.", val);
            }
        }
        return null;
    }

    @Override
    public String parseToObject(String str) throws StringParsingError {
        return str.trim();
    }

    /**
     *  Overrides in order to enable fluent setter style.
     *  START
     */

    @Override
    public FieldAssistantStr setFieldLabel(String fieldLabel) {
        super.setFieldLabel(fieldLabel);
        return this;
    }

    @Override
    public FieldAssistantStr setEditable(boolean editable) {
        super.setEditable(editable);
        return this;
    }

    @Override
    public FieldAssistantStr setDbColName(String dbColName) {
        super.setDbColName(dbColName);
        return this;
    }

    @Override
    public FieldAssistantStr setDefaultWidthInChar(int defaultWidthInChar) {
        super.setDefaultWidthInChar(defaultWidthInChar);
        return this;
    }

    @Override
    public FieldAssistantStr setCanBeNull(boolean canBeNull) {
        super.setCanBeNull(canBeNull);
        return this;
    }

    public void clear(E entity) {
        setter.accept(entity, "");
    }

    /**
     *  Overrides in order to enable fluent setter style.
     *  END
     */


}
