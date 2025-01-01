package com.fzenner.datademo.web.outmsg;

/**
 * Records of the form XyzOrNull are required as a wire object for the GUI.
 * The problem it solves is the distinction between no change of a newValue and a change to null.
 * If there is no change, null will be set as overall newValue and the JSON will not contain a newValue field.
 * If there is a change to null, the JSON object of the form {newValue: null} will be set over the wire.
 *
 * @param value
 */
public record DateTimeWireOrNull(DateTimeWire value) {

    public DateTimeWireOrNull(Integer y, Integer mo, Integer d, Integer h, Integer mi, Integer s, Integer n) {
        this(new DateTimeWire(y, mo, d, h, mi, s, n));
    }


}
