package com.kewebsi.service;

import com.kewebsi.controller.DtoAssistant;
import com.kewebsi.controller.FieldAssistant;
import com.kewebsi.controller.ManagedEntity;
import com.kewebsi.errorhandling.ExpectedClientDataError;
import com.kewebsi.errorhandling.StringParsingError;
import com.kewebsi.html.PageState;
import com.kewebsi.html.PageStateVarDateTimeIntf;
import com.kewebsi.html.table.EntityForDetailDisplayProvider;
import com.kewebsi.util.KewebsiDateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * This page var handles the complex user-interactions and optimizes them so that the user experiences less hassles.
 * @param <T>
 */
public class PageVarEntityProviderBackedDateTime<T>
        extends PageVarEntityProviderBacked<T, LocalDateTime>
        implements PageStateVarDateTimeIntf {

    /*
     * When a value can successfully set, that is {@link PageStateVarIntf#getValCore()} reflects correctly the
     * last edit, then the unparsedStringValues as well as dateBuffer are null.
     * If a value could not successfully be set by setting the date or time, we distinguish two cases:
     * a) The date or time by itself was valid. Then the respective buffer is set to the parsed value
     * but the unparsedStringValue is set to null (since the user input string was successfully parsed to a date or time).
     * b) The date or time could not be parsed. Then the respective buffer is set to null and the unparsedStringValue
     * will be set to the errorneous input vale.
     */

    protected String unparsedStringValueDate;
    protected LocalDate dateBuffer;
    protected String unparsedStringValueTime;
    protected LocalTime timeBuffer;

    protected PageVarError dateError;
    protected PageVarError timeError;

    public PageVarEntityProviderBackedDateTime(PageState pageState, EntityForDetailDisplayProvider entityProvider, DtoAssistant dtoAssistant, FieldAssistant fieldAssistant) {
        super(pageState, entityProvider, dtoAssistant, fieldAssistant);
    }

    @Override
    public void setDateFromClientValidateAndSetValueOrError(String userInputString, LocalDate userInputObject) {
        checkAccidentalOverwriting();
        try {
            if (userInputObject != null) {
                setDateBuffer(userInputObject);
            } else {
                setDateBuffer(KewebsiDateUtils.parseGermanDate(userInputString));
            }

            clearDateError();
            LocalDateTime currentValue = getValCore();

            LocalTime currentTime;
            if (currentValue != null) {
                currentTime = currentValue.toLocalTime();
            } else {
                currentTime = timeBuffer;
            }

            if (currentTime != null) {  // We transfer the value to the entity only if date and time are OK.
                LocalDateTime newVal = LocalDateTime.of(dateBuffer, currentTime);
                FieldAssistant fa = (FieldAssistant) fieldAssistant;
                ManagedEntity<T> managedEntity = getManagedEntity();
                // The case that we have no managed entity should be avoided. Here wie opt to ignore the edit.
                // The value will be lost and nothing will be displayed.
                if (managedEntity != null) {
                    dtoAssistant.setValueByFieldAssistant(fa, newVal, managedEntity);
                    unparsedStringValueDate = null;
                    setDateBuffer(null);
                }

                clearDateError();
            }

        } catch (StringParsingError | ExpectedClientDataError ex) {
            unparsedStringValueDate = userInputString;
            setDateBuffer(null);
            setDateError(new PageVarError(this, ex.getMessage(), false));
        }
    }


    @Override
    public void setTimeFromClientValidateAndSetValueOrError(String userInputString, LocalTime userInputObject) {
        checkAccidentalOverwriting();
        try {
            if (userInputObject != null) {
                setTimeBuffer(userInputObject);
            } else {
                setTimeBuffer(KewebsiDateUtils.parseTime(userInputString));
            }

            // If the date has an error, we leave o
            if (getDateError() != null) {
                return;
            }

            LocalDateTime currentValue = getValCore();
            LocalDate currentDate;
            if (currentValue != null) { // We transfer the value to the entity only if date and time are OK.
                currentDate = currentValue.toLocalDate();
            } else {
                currentDate = getDateBuffer();
            }

            if (currentDate != null) {
                LocalDateTime newVal = LocalDateTime.of(currentDate, timeBuffer);
                dtoAssistant.setValueByFieldAssistant((FieldAssistant) fieldAssistant, newVal, getManagedEntity());

                clearTimeError();
                unparsedStringValueTime = null;
                setTimeBuffer(null);
            }
        } catch (StringParsingError | ExpectedClientDataError ex) {
            unparsedStringValueTime = userInputString;
            setTimeBuffer(null);
            setTimeError(new PageVarError(this, ex.getMessage(), false));
        }
    }

    public void setDateError(PageVarError error) {
        this.dateError = error;
        entityWhenErrorWasSet = getManagedEntity();
        valWhenErrorWasSet = getValCore();
    }

    public void clearDateError() {
        dateError = null;
        entityWhenErrorWasSet = null;
        valWhenErrorWasSet = null;
    }


    public void setTimeError(PageVarError error) {
        this.timeError = error;
        entityWhenErrorWasSet = getManagedEntity();
        valWhenErrorWasSet = getValCore();

    }

    public void clearTimeError() {
        timeError = null;
        entityWhenErrorWasSet = null;
        valWhenErrorWasSet = null;
    }

    @Override
    public PageVarError getDateError() {
        updateErrorIfBackingValueChanged();
        return dateError;
    }

    @Override
    public PageVarError getTimeError() {
        updateErrorIfBackingValueChanged();
        return timeError;
    }

    @Override
    public PageVarError getDateTimeError() {
        updateErrorIfBackingValueChanged();
        return error;
    }



    @Override
    public PageVarError getError() {
        updateErrorIfBackingValueChanged();
        return getErrorCore();
    }


    @Override
    protected PageVarError getErrorCore() {
        if (dateError != null) {
            return dateError;
        }

        if (timeError != null) {
            return timeError;
        }

        if (error != null) {
            return error;
        }

        return null;
    }


    @Override
    public void clearError() {
        super.clearError();
        dateError = null;
        timeError = null;
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

    public LocalDate getDateBuffer() {
        return dateBuffer;
    }

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


}
