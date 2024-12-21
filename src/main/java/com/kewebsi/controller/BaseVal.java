package com.kewebsi.controller;

import com.kewebsi.errorhandling.CodingErrorException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * We use BaseVal in order to constrain parameter to allowed and expected types. Without the help of such a class,
 * we would have to accept type Object in places where we would accept in essence Long, Float and DateTime for example.
 * The internal values of BaseVal should never be null. That is because we allow scalars like int to be BaseVal.
 * If there is a null value to deal with, a BaseVal itself should be null.
 */
public abstract class BaseVal<T> {


    protected T val;
    public T getVal() {
        return val;
    }

    public static BaseVal of(String val) {
        if (val == null) {
            return null;
        }
        return new BaseValString(val);
    }

    public static BaseVal of(Enum val) {
        if (val == null) {
            return null;
        }
        return new BaseValEnm(val);
    }

    public static BaseVal of(int val) {
        return new BaseValInt(val);
    }

    public static BaseVal of(Integer val) {
        if (val == null) {
            return null;
        }
        return new BaseValInt(val);
    }

    public static BaseVal of(long val) {
        return new BaseValLong(val);
    }

    public static BaseVal of(Long val) {
        if (val == null) {
            return null;
        }
        return new BaseValLong(val);
    }

    public static BaseVal of(float val) {
        return new BaseValFloat(val);
    }

    public static BaseVal of(Float val) {
        if (val == null) {
            return null;
        }
        return new BaseValFloat(val);
    }

    public static BaseVal of(LocalDateTime val) {
        if (val == null) {
            return null;
        }
        return new BaseValLocalDateTime(val);
    }


    public static BaseVal of(Date val) {

        if (val == null) {
            return null;
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(val);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        LocalDateTime localDate = LocalDateTime.of(year, month, day, hours, minutes, seconds);
        return new BaseValLocalDateTime(localDate);
    }


    public static BaseVal of(Object obj) {

        if (obj == null) {
            return null;
        }

        if (obj instanceof Long) {
            return BaseVal.of((Long) obj);
        }

        if (obj instanceof Integer) {
            return BaseVal.of((Integer) obj);
        }

        if (obj instanceof Float) {
            return BaseVal.of((Float) obj);
        }

        if (obj instanceof String) {
            return BaseVal.of((String) obj);
        }

        if (obj instanceof Long) {
            return BaseVal.of((Long) obj);
        }

        if (obj instanceof Enum) {
            return BaseVal.of((Enum) obj);
        }

        if (obj instanceof LocalDate) {
            return BaseVal.of((LocalDate) obj);
        }

        if (obj instanceof LocalDateTime) {
            return BaseVal.of((LocalDateTime) obj);
        }

        throw new CodingErrorException("Missing type handling for: " + obj.getClass().getSimpleName());
    }

    public abstract FieldAssistant.FieldType getType();

    public abstract String toHtml();

    public String toJsonString() {
        return toHtml(); // TODO: Check the conversion of all types.
    }

    public String toDbFormat() {
        return val.toString();
    }

    @Override
    public boolean equals(Object obj) {
        BaseVal<T> other = (BaseVal<T>) obj;
        if (this.val == null) {
            if (other.val == null) {
                return true;
            } else {
                return false;
            }
        } else {
            if (other.getVal() == null) {
                return false;
            } else {
                boolean result = this.val.equals(other.getVal());
                return result;
            }
        }
    }
}
