package com.kewebsi.controller;

import com.kewebsi.errorhandling.StringParsingError;
import com.kewebsi.html.ImplementationErrorException;
import com.kewebsi.util.CommonUtils;

import java.util.EnumSet;

public class SimpleFieldAssistantEnum<E> extends SimpleFieldAssistant<E> implements FieldAssistantEnumIntf {

    public Class<? extends Enum> enumClass = null;

    public SimpleFieldAssistantEnum(Enum<?> fieldName, FieldAssistant.FieldType fieldType, Class<? extends Enum> enumClass) {
        super(fieldName, fieldType, true);
        this.enumClass = enumClass;
    }

    public SimpleFieldAssistantEnum(FieldAssistantEnum bluePrint) {
        super(bluePrint);
        this.enumClass = bluePrint.enumClass;

    }

    @Override
    public SimpleFieldAssistantEnum<E> setEnumClass(Class<? extends Enum> enumClass) {
        this.enumClass = enumClass;
        return this;
    }

    public Class<? extends Enum> getEnumClass() {
        return enumClass;
    }

    public EnumSet<?> getEnumSet() {
        return EnumSet.allOf(enumClass);
    }

    @Override
    public E parseToObject(String str) throws StringParsingError {
        if (! CommonUtils.hasInfo(str)) {
            if (!canBeNull) {
                throw new ImplementationErrorException("Field cannot be set to null:");
            }
            return null;
        }
        return (E) Enum.valueOf(enumClass, str);
    }


}
