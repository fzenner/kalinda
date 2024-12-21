package com.fzenner.datademo.web.outmsg;

public record DateTimeFieldGuiDef(
        DateTimeWireOrNull dateTimeWireOrNull,
        Boolean required,
        Boolean disabled,
        TimeWireOrNull defaultTimeWireOrNull
) {
}
