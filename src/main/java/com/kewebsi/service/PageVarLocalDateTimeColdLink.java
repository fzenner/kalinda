package com.kewebsi.service;

import com.kewebsi.controller.*;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.errorhandling.StringParsingError;
import com.kewebsi.html.PageState;
import com.kewebsi.html.PageStateVarDateTimeIntf;
import com.kewebsi.util.KewebsiDateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PageVarLocalDateTimeColdLink
        extends    PageStateVarColdLink<LocalDateTime>
        implements PageStateVarDateTimeIntf {

    protected String unparsedStringValueDate;
    protected LocalDate dateBuffer;
    protected String unparsedStringValueTime;
    protected LocalTime timeBuffer;

    protected PageVarError dateError;
    protected PageVarError timeError;

//    protected LocalTime defaultTimeWhenDateIsSet;

    public PageVarLocalDateTimeColdLink(PageState pageState) {
        super(pageState);
    }

    public PageVarLocalDateTimeColdLink(PageState pageState, FieldAssistant fieldAssistant) {
        super(pageState, fieldAssistant);
    }

    public PageVarLocalDateTimeColdLink(PageState pageState, SimpleFieldAssistant fieldAssistant, String htmlIdPrefix) {
        super(pageState, fieldAssistant, htmlIdPrefix);
    }



    public LocalDateTime getVal() {
//        if (hasError()) {
//            throw new CodingErrorException("Illegal attempt to read the value of a PageVar with an error. Field: " + fieldAssistant.getFieldLabel() + " Error: " + getError().getErrorMsg());
//        }
        return val;
    }


    public LocalDateTime getValOrThrowError() throws PageVarError {
        if (hasError()) {
            throw getError();
        }
        return val;
    }


    public void setValueFromBackend(BaseVal baseVal) {
        if (baseVal != null) {
            assert (baseVal.getType() == FieldAssistant.FieldType.LOCALDATETIME);
            val = ((BaseValLocalDateTime) baseVal).getVal();
        } else {
            val = null;
        }
        unparsedStringValue = null;
    }

    @Override
    public String getValAsString() {
        return KewebsiDateUtils.printGermanDateTime(val);
    }

    @Override
    public void setDateFromClientValidateAndSetValueOrError(String userInputString, LocalDate userInputObject) {

        try {
            if (userInputObject != null) {
                setDateBuffer(userInputObject);
            } else {
                setDateBuffer(KewebsiDateUtils.parseGermanDate(userInputString));
            }

            LocalDateTime currentValue = getValCore();
            LocalTime currentTime;

            if (currentValue != null) {
                currentTime = currentValue.toLocalTime();
            } else {
                currentTime = timeBuffer;
            }

            //We fill in the default time exactly when the date is set. Can be null though.
            if (currentTime == null) {
               //  currentTime = getDefaultTimeWhenDateIsSet();
            }


            if (currentTime != null) {  // We transfer the value to the entity only if date and time are OK.
                LocalDateTime newVal = LocalDateTime.of(dateBuffer, currentTime);
                val = newVal;
                clearDateError();
                unparsedStringValueDate = null;
                setDateBuffer(null);
            }
        } catch (StringParsingError spe) {
            unparsedStringValueDate = userInputString;
            setDateBuffer(null);
            setDateError(new PageVarError(this, spe.getMessage(), false));
        }
    }


    @Override
    public void setTimeFromClientValidateAndSetValueOrError(String userInputString, LocalTime userInputObject) {
        try {
            if (userInputObject != null) {
                setTimeBuffer(userInputObject);
            } else {
                setTimeBuffer(KewebsiDateUtils.parseTime(userInputString));
            }

            LocalDateTime currentValue = getValCore();
            LocalDate currentDate;
            if (currentValue != null) {
                currentDate = currentValue.toLocalDate();
            } else {
                currentDate = dateBuffer;
            }

            if (currentDate != null) { // We transfer the value to the entity only if date and time are OK.
                LocalDateTime newVal = LocalDateTime.of(currentDate, timeBuffer);
                val = newVal;
                clearTimeError();
                unparsedStringValueTime = null;
                setTimeBuffer(null);
            }
        } catch (StringParsingError spe) {
            unparsedStringValueTime = userInputString;
            setTimeBuffer(null);
            setTimeError(new PageVarError(this, spe.getMessage(),false));
        }
    }

    @Override
    public void setUnparsedStringValueDate(String str) {
        unparsedStringValueDate = str;
    }

    @Override
    public void setUnparsedStringValueTime(String str) {
        unparsedStringValueTime = str;
    }

    @Override
    public String getUnparsedStringValueDate() {
        return unparsedStringValueDate;
    }

    @Override
    public String getUnparsedStringValueTime() {
        return unparsedStringValueTime;
    }

    @Override
    public LocalDate getDateBuffer() {
        return dateBuffer;
    }

    @Override
    public LocalTime getTimeBuffer() {
        return timeBuffer;
    }

    public void setDateBuffer(LocalDate dateBuffer) {
        this.dateBuffer = dateBuffer;
        if (dateBuffer != null) {
            unparsedStringValueDate = null;
        }
        clearDateError();
        }

    public void setTimeBuffer(LocalTime timeBuffer) {
        this.timeBuffer = timeBuffer;
        if (timeBuffer != null) {
            unparsedStringValueTime = null;
        }
        clearTimeError();
    }

    public void setDateError(PageVarError error) {
        this.dateError = error;
    }

    public void clearDateError() {
        dateError = null;
    }


    public void setTimeError(PageVarError error) {
        this.timeError = error;
    }

    public void clearTimeError() {
        timeError = null;

    }

    @Override
    public PageVarError getDateError() {
        return dateError;
    }

    @Override
    public PageVarError getTimeError() {
        return timeError;
    }

    @Override
    public PageVarError getDateTimeError() {
        return error;
    }


    @Override
    public void clearError() {
        super.clearError();
        dateError = null;
        timeError = null;
    }

//    public LocalTime getDefaultTimeWhenDateIsSet() {
//        return defaultTimeWhenDateIsSet;
//    }


//    public void setDefaultTimeWhenDateIsSet(LocalTime defaultTimeWhenDateIsSet) {
//        this.defaultTimeWhenDateIsSet = defaultTimeWhenDateIsSet;
//    }

}
