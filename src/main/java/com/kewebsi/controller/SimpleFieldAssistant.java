package com.kewebsi.controller;

import com.kewebsi.errorhandling.StringParsingError;
import com.kewebsi.service.FieldError;
import com.kewebsi.util.CommonUtils;

public abstract class SimpleFieldAssistant<F> {

    protected final Enum <?> fieldName;
    protected String fieldLabel;
    protected final FieldAssistant.FieldType fieldType;

    public boolean editable;

    public boolean canBeNull;


//    public SimpleFieldAssistant(Enum<?> fieldName, FieldAssistant.FieldType fieldType) {
//        this.fieldName = fieldName;
//        this.fieldType = fieldType;
//    }

    public SimpleFieldAssistant(Enum<?> fieldName, FieldAssistant.FieldType fieldType, boolean editable) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.editable = editable;
    }


    public SimpleFieldAssistant(FieldAssistant bluePrint) {
        fieldName = bluePrint.fieldName;
        fieldLabel = bluePrint.fieldLabel;
        fieldType = bluePrint.fieldType;
        editable = bluePrint.editable;
        canBeNull = bluePrint.canBeNull;

    }

    public Enum<?> getFieldName() {
        return fieldName;
    }


    public FieldAssistant.FieldType getFieldType() {
        return fieldType;
    }


    public String getFieldLabel() {
        if (fieldLabel == null) {
            return fieldName.name();
        }
        return fieldLabel;
    }

    public SimpleFieldAssistant setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
        return this;
    }


    public boolean isEditable() {
        return editable;
    }

    public SimpleFieldAssistant setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }

    public boolean canBeEmpty() {
        return canBeNull;
    }

    public SimpleFieldAssistant<F> setCanBeNull(boolean canBeNull) {
        this.canBeNull = canBeNull;
        return this;
    }


    public FieldError validateStr(String val) {
        if (!CommonUtils.hasInfo(val)) {
            if (!canBeNull) {
                return new FieldError(this, "Field " + getFieldLabel() + " must be filled.", val);
            }
        }
        try {
            F obj = parseToObject(val);
            return validate(obj);
        } catch(StringParsingError spe) {
            return new FieldError(this, spe.getMessage(), spe.getBadValue());
        }
    }


    public ParseResult<F> parse(String val) {
        if (!CommonUtils.hasInfo(val)) {
            if (!canBeNull) {
                ParseResult.onFieldError(this, "Field " + getFieldLabel() + " must be filled.", val);
            }
        }
        try {
            F obj = parseToObject(val);
            return new ParseResult<>(obj);
        } catch(StringParsingError spe) {
            return ParseResult.onFieldError(this, spe.getMessage(), spe.getBadValue());
        }
    };


    public FieldError validate(F val) {
        if (val == null) {
            if (!canBeNull) {
                return new FieldError(this, "Field " + getFieldLabel() + " must be non-null.", "null");
            }
        }
        return null;
    }


    public abstract F parseToObject(String str) throws StringParsingError;

//    public abstract F parseToObject(String str) throws StringParsingError {
//        try {
//            F result =
//                    switch (fieldType) {
//                        case ENM -> (F) Enum.valueOf(((FieldAssistantEnum) this).getEnumClass(), str);  // TODO: Move to subclass.
//                        case LONG -> Long.parseLong(str);
//                        case INT -> Integer.parseInt(str);
//                        case FLOAT -> Float.parseFloat(str);
//                        case STR -> str;
//                        case LOCALDATETIME -> KewebsiDateUtils.parseDateTimeIgnoresHoursAndMinutesXXX(str, true, false, CalendarController.YEAR_DIGITS.Required);
//                        case LOCALDATE -> KewebsiDateUtils.parseDate(str, true, true, CalendarController.YEAR_DIGITS.Required);
//
//                    };
//            return result;
//        } catch(Exception e) {
//            throw new StringParsingError(e);
//        }
//    }



}
