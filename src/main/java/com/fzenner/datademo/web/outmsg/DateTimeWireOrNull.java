package com.fzenner.datademo.web.outmsg;

public record DateTimeWireOrNull(DateTimeWire value) {

    public DateTimeWireOrNull(Integer y, Integer mo, Integer d, Integer h, Integer mi, Integer s, Integer n) {
        this(new DateTimeWire(y, mo, d, h, mi, s, n));
    }


}
