package com.fzenner.datademo.web.outmsg;

import com.kewebsi.errorhandling.CodingErrorException;

import java.time.LocalTime;

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

