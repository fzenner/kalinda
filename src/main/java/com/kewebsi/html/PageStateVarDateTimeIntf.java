package com.kewebsi.html;

import com.kewebsi.errorhandling.ErrorInfo;
import com.kewebsi.service.PageVarError;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 *
 * @param <F> Type of the variable hold by the PageStateVar
 */
public interface PageStateVarDateTimeIntf extends PageStateVarIntf<LocalDateTime>  {

    //
    // Setter
    //
    public void setDateFromClientValidateAndSetValueOrError(String userInputString, LocalDate userInputObject);
    public void setTimeFromClientValidateAndSetValueOrError(String userInputString, LocalTime userInputObject);

    public void setUnparsedStringValueDate(String str);
    public void setUnparsedStringValueTime(String str);

    /**
     * This unparsed date string is relevant when there is an error in such a way that the client provided
     * string could not be parsed and the date buffer( see {@link #getDateBuffer() } ) could not
     * be filled.
     * @return
     */
    public String getUnparsedStringValueDate();
    public String getUnparsedStringValueTime();

    /**
     * Contains the last successfully parsed date value or the last date value set via a LocalDate object.
     * If the effective value of the PageState variable (see {@link PageStateVarIntf#getValCore()}  ) is not null, this date buffer is null.
     * That is to make clear that the buffer should only be accessed when there is no real value.
     * The date buffer works similar to the unparsed string value (see {@link #getUnparsedStringValueDate()} )
     * In addition to the unparsedStringValue, the date buffer provides a formatted string even though the
     * overall datetime object could not be populated.
     * @return The buffered date from the last user input. null, if there is last user input or no last user input
     * that could be successfully parsed into a LocaDate. Returns also null, if the value of this PageStateVar is
     * non-null.
     *
     */
    public LocalDate getDateBuffer();

    /**
     * Analoguous to {@link #getDateBuffer()}.
     * @return As explained above
     */
    public LocalTime getTimeBuffer();


    public PageVarError getDateError();

    public PageVarError getTimeError();

    /**
     * An error that is not solely confined to the date or the time (Confined errors are typically syntax errors.
     */
    public PageVarError getDateTimeError();


}

