package com.kewebsi.controller;

import com.kewebsi.errorhandling.StringParsingError;

import java.util.EnumSet;


public class FieldAssistantEnum<E, N> extends FieldAssistant<E, N> implements FieldAssistantEnumIntf {

    public Class<? extends Enum> enumClass = null;

    public FieldAssistantEnum(Enum<?> fieldName) {
        super(fieldName, FieldType.ENM);
    }

    public FieldAssistantEnum<E, N> setEnumClass(Class<? extends Enum> enumClass) {
        this.enumClass = enumClass;
        return this;
    }

    public Class<? extends Enum> getEnumClass() {
        return enumClass;
    }

    public EnumSet<?> getEnumSet() {
        return EnumSet.allOf(enumClass);
    }


    public void setUnconvertedValue(E entity, Object value) {

        if (value instanceof String) {
            String strVal = (String) value;
            N convertedVal = (N) Enum.valueOf(enumClass, strVal);
            setter.accept(entity, convertedVal);
        } else {
            setter.accept(entity, (N) value);
        }
    }

    @Override
    public N parseToObject(String str) throws StringParsingError {
        return (N) Enum.valueOf(enumClass, str);
    }


}
