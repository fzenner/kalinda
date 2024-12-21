package com.fzenner.datademo.web.outmsg;


import com.kewebsi.errorhandling.CodingErrorException;
import org.springframework.core.codec.CodecException;

import java.time.LocalTime;

public record TimeWire(Integer h, int mi, int s, int n) {

    public static TimeWire from(LocalTime localTime) {
        if (localTime == null) {
            return null;
        }
        return new TimeWire(localTime.getHour(), localTime.getMinute(), localTime.getSecond(), localTime.getNano());
    }


}
