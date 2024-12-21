package com.fzenner.datademo.web.outmsg;

public record InputFieldGuiDef(
        String typeOfValue,
        Integer size,
        String name,
        String placeholder,
        String value,
        Boolean required,
        Boolean disabled
        ) {
}
