package com.fzenner.datademo.web.outmsg;

import com.kewebsi.errorhandling.CodingErrorException;

import java.time.LocalTime;

/**
 * Records of the form XyzOrNull are required as a wire object for the GUI.
 * The problem it solves is the distinction between no change of a newValue and a change to null.
 * If there is no change, null will be set as overall newValue and the JSON will not contain a newValue field.
 * If there is a change to null, the JSON object of the form {newValue: null} will be set over the wire.
 *
 * @param value
 */
public record TimeWireOrNull(TimeWire value) {

    public TimeWireOrNull(Integer h, int mi, int s, int n) {
        this(new TimeWire(h, mi,s, n));
    }

    /**
     *
     * @param localTime must be not null
     */
    public TimeWireOrNull(LocalTime localTime) {
        this(TimeWire.from(localTime));
    }

    public static TimeWire from(LocalTime localTime) {
        if (localTime == null) {
            return null;
        }
        return new TimeWire(localTime.getHour(), localTime.getMinute(), localTime.getSecond(), localTime.getNano());
    }


}

