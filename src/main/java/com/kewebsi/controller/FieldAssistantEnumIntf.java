package com.kewebsi.controller;

import java.util.EnumSet;

public interface FieldAssistantEnumIntf {

    public FieldAssistantEnumIntf setEnumClass(Class<? extends Enum> enumClass);

    public Class<? extends Enum> getEnumClass();

    public EnumSet<?> getEnumSet();

}
