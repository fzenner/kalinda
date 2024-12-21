package com.kewebsi.service;

import com.kewebsi.controller.FieldAssistant;
import com.kewebsi.controller.SimpleFieldAssistant;
import com.kewebsi.html.PageState;
import com.kewebsi.html.PageStateVarIntf;
import com.kewebsi.util.CommonUtils;
import org.springframework.core.codec.CodecException;

import java.util.function.Function;

public abstract class PageStateVarBase<F> implements PageStateVarIntf<F> {



    protected PageState pageState;
    protected String pageVarId;

    protected String unparsedStringValue;  // TODO: THis is only relevant for scalar variables. Push into a scalar subclass or rename it to a more general unstorable thing.

    protected SimpleFieldAssistant<F> fieldAssistant;

    protected PageVarError error;

    protected boolean isInSyncWithGui = true;

    public boolean getIsInSyncWithGui() {
        return isInSyncWithGui;
    }

    public void setIsInSyncWithGuiToFalse() {
        isInSyncWithGui = false;
    }

    public void setIsInSyncWithGuiToTrue() {
        isInSyncWithGui = true;
    }

    // protected ClientSyncState clientSyncState;


    // We need to store the fact that a parsing error has occured, because we might have cleared the error.
    // That might be the case when the user has clicked away all error indiciations or if data is meant to be entered
    // from scratch without distracting error displays of an earlier validation sweep.
    // We still might want to display the last entered verbatim string instead of the core value (being null).
    // So we need to be able to detect the situation: No error present (had been clicked away), no value present (parsing had failed) but last
    // entered string is not null and should be displayed.
    // protected boolean hasParsingError = false;
    // Why do we need a parsing error in addition to a normal error? If we have a parsing error, we return the unparsed
    // string value. If we have a normal error, we return the validated and formatted string representation of the
    // real value.
    // protected PageVarError parsingError;

    protected Function<PageStateVarIntf, Boolean> checkRelevance;

    public PageStateVarBase() {

    }


    public void initPageStateVar(PageState pageState, String pageVarId, SimpleFieldAssistant fieldAssistant) {
        this.pageState = pageState;
        this.pageVarId = pageVarId;
        this.unparsedStringValue = null;
        this.fieldAssistant = fieldAssistant;
        this.isInSyncWithGui = true;
        pageState.registerPageStateVar(this);
    }

    public <E extends Enum<E>> PageStateVarBase(E pageVarId) {
        this.pageVarId = pageVarId.name();
        this.unparsedStringValue = null;
    }




    /**
     * Returns the current value as last edited by the client.
     * If the client has not edited the field variable yet, it returns null.
     * @return
     */
    public String getUnparsedStringValue() {
        return unparsedStringValue;
    }

    public void setUnparsedStringValue(String unparsedStringValue) {
        this.unparsedStringValue = unparsedStringValue;
    }


//    public String getUnparsedStringValue() {
//        return unparsedStringValue;
//    }

    /**
     *
     * @return The value of the variable as string, not yet HTML-escaped.
     * If the value is null, then return the empty String.
     */
    // public abstract String getStringValue();

    /**
     * Sets the string value of this page variable and applies validating and formatting for optimal user experience.
     * Validates the string value.
     * If it is a valid value, the the following will be done:
     *   - The typed "real" value will be set.
     * 	 - The string value will be set to a nicely formatted (for strings: truncated) version. This will trigger an
     * 	   update to the client.
     * 	If the value is invalid, the faulty string value remains unchanged (so that the user can correct it) and the
     * 	typed "real" value will remain unchanged.
     */
    @Override
    public void setStringValueFromClient(String userInputString)  /* throws StringParsingError  */{
        unparsedStringValue = userInputString;
        validateUnparsedStringValueAndSetValueOrErrorAllowNull();
        // setClientSyncState(ClientSyncState.SYNCHRONIZED);
    }

    public void setStringValueFromClientAllowNull(String userInputString) {
        unparsedStringValue = userInputString;
        if (CommonUtils.hasNoInfo(userInputString)) {
            setValueCoreAndSync(null);
            unparsedStringValue = null;
            clearError();
        } else {
            validateUnparsedStringValueAndSetValueOrErrorAllowNull();
        }
        // setClientSyncState(ClientSyncState.SYNCHRONIZED);
    }


    public void setValueFromClient(F valueObject) {
        validateObjectAndSetValueAndError(valueObject);
        // setClientSyncState(ClientSyncState.SYNCHRONIZED);
    }

//    /**
//     * This method is typically called by client components containing multiple input fields (vectors).
//     * A single string should be typically transferred as a whole and evaluated on the server side.
//     */
//    public void notifyClientInputIncomplete() {
//        setValueCore(null);
//        unparsedStringValue = null;
//        clearError();
//        setClientSyncState(ClientSyncState.CLIENT_INPUT_INCOMPLETE);
//    }
//
//    /**
//     * This method is typically called by client components that transfer parsed elements.
//     */
//    public void notifyClientInputUnparseable() {
//        setValueCore(null);
//        unparsedStringValue = null;
//        clearError();
//        setClientSyncState(ClientSyncState.CLIENT_INPUT_UNPARSEABLE);
//    }

    /**
     * Removes value. (Set to null / empty string / intial string=
     * Removes errors.
     * Set state to ClientState.INITIAL
     */
    public void clear() {
        clearValue();
        clearStringValueAndError();
        // setClientSyncState(ClientSyncState.SYNCHRONIZED);
    }

    public abstract void clearValue();

    public void clearStringValueAndError() {
        unparsedStringValue = null;
        clearError();
    }

    protected void setStringCopiesFromBackend(String newStringValue) {   // TODO: Rename
        this.unparsedStringValue = null;
        // this.initialStringValue = newStringValue;
    }







    @Override
    public boolean isNull() {
        if (getValCore() == null) {
            return true;
        }
        return false;
    }


    public abstract void setValueCore(F value);

    public void setValueCoreAndSync(F value) {
        setValueCore(value);
        setIsInSyncWithGuiToTrue();
    }

    protected FieldError validate(F value) {
        return fieldAssistant.validate(value);
    }


//    public PageVarError validate() {
//
//        FieldError error = validate(this.getVal());
//        if (error != null) {
//            var pve = new PageVarError(this, error.getMessage());
//            setError(pve);
//            return pve;
//        } else {
//            clearError();
//            unparsedStringValue = null;
//            return null;
//        }
//    }


    /**
     * Checks whether the sting input can be mapped to typed value.
     * If yes, the typed "real" value will will be set.
     * If no, three things happen
     * a) The error is set (and returned).
     * b) The typed value remains as before (last valid input)
     * c) The string input stays the same invalid string.
     *
     * Handling of empty strings / null values:
     * An empty string is NOT a parsing error, because it differs in the user experience:
     * When the user enters a not-parseable value, he appreciates immediate feedback in form of an error.
     * When the user enters an empty field, he knows that the field is empty. An error message would irritate, since
     * he can come back later and fill the field.
     *
     * THERE IS A CONCEPTIUAL PROBLEM:
     * A parsing error hangs on the input field. The value is not stored in the page variable.
     * A validation error hangs on the page variable
     * We have two steps: parsing and validation.
     * This function here only parses. No validation is done
     * !!! Other implementation (EntityBacked stuff) does actuall also validate
     *
     *
     * @return
     */


    // XXXXXXXXXXXXXXXXXX1 TODO: Implement Handling of Parsing Errors on PageVarFields

//    - We do error out if we set an empty field. We should not do that here but in a save phase validation. (fixed...)
//    - setValueCore validates against null, although it should not
//    - again: handle parsing errors on fields separate from errors on variables.
//    Parsing Error --> Variable Error --> Save-Phase-Object-Error

    public PageVarError validateUnparsedStringValueAndSetValueOrErrorAllowNull() {

        String strVal = getUnparsedStringValue();

        PageVarError pagerVarError;
        if (CommonUtils.hasNoInfo(strVal)) {
            setValueCoreAndSync(null);
            pagerVarError = null;
        } else {
            var parseResult = fieldAssistant.parse(strVal);
            if (parseResult.isOk()) {
                validateObjectAndSetValueAndErrorAllowNull(parseResult.value);
                setUnparsedStringValue(null);
                pagerVarError = null;
            } else {
                setValueCore(null);  // We set the null value, although it is not a valid value. Otherwise the old (valid but by the user unintended) value stays in the field.
                setIsInSyncWithGuiToFalse();
                pagerVarError = new PageVarError(this, parseResult.fieldError.getMessage(), false);
                // pagerVarError.setIsParsingError(true);
                pagerVarError.setErrorType(PageVarError.ErrorType.SERVER_SIDE_PARSING);
            }
        }

        setError(pagerVarError);
        return pagerVarError;

    }


    /**
     * Wir erlauben leere Felder, also das leeren von Feldern, aber keine falsch formatierten Felder.
     * The value will always be written into the variable. If an error is found, th respective error is attached.
     * Bei falsch formatierten feldern zeigen wir den Fehler direkt.
     * Dadurch,dass wir leer felder erlauben, müssen wir vor dem ausführen der Aktion, für die das
     * Eingabefeld relevant ist, nochmals prüfen, ob ein Feld leer ist.
     * Das müssen wir sowieso machen, da wir ja ggf. auch noch gar nichts eingegeben haben.
     * D.h., unberührt felder müssen geprüft werden, ob sie leer (oder mit default-value) verbleiben dürfen.
     * D.h., wir brauchen zwei Phasen:
     * 1. Die Dateneingabe: Hier sind leere Felder nicht zwingend ein Fehler.
     * 2. Die Kontrolle vor dem Funktionsaufruf: Hier werden die Felder auf Null geprüfet.
     *    Weiterhin werden andere ggf, Mehrfeld-Constraints geprüft.
     * 2, Der Funktionsaufruf
     */

    public PageVarError validateObjectAndSetValueAndError(F value) {
        FieldError error = fieldAssistant.validate(value);
        if (error != null) {
            var pve = new PageVarError(this, error.getMessage());
            setError(pve);
            setValueCoreAndSync(value);
            return pve;
        } else {
            clearError();   // TODO: Check if we can establish a clear(er) state transition model for error, parsingError, unparsedStringValue, value
            unparsedStringValue = null;
            setValueCoreAndSync(value);
            return null;
        }
    };


    /**
     * The method accepts null values even if the final value of the field does not allow null values.
     * That is because a field can be edited as empty or skipped until the final validation of all relevant fields.
     * The value will always be set, even if it has an error. (Tte value was parseable.)
     * @param value
     * @return
     */
    public PageVarError validateObjectAndSetValueAndErrorAllowNull(F value) {
        if (value == null) {
            clearError();
            unparsedStringValue = null;
            setValueCoreAndSync(value);
            return null;
        } else {
            return validateObjectAndSetValueAndError(value);
        }
    }

    /**
     * This method is meant to be called as a final scalar validation of an input field.
     * @return
     */
    public PageVarError validateAndCeckIfInvalidNull() {   // TODO PRIO1 VALIDATING: Handle synchronized state

        PageVarError result;

        if (!isRelevant()) {
            result = null;
        } else {
            if (hasError()) {
                switch (getError().getErrorType()) {
                    case SERVER_SIDE_VALUE -> {
                        if (isNull()) {
                            if (!fieldAssistant.canBeNull) {
                                result = new PageVarError(this, "Field must not be empty");
                            } else {
                                clearError();
                                unparsedStringValue = null;
                                result = null;
                            }
                        } else {
                            // We have an error. But it might be not valid anymore due to the context.
                            // Hence we revalidate.
                            result = revalidateNonNullValue();
                        }
                    }
                    case SERVER_SIDE_PARSING -> result = error;
                    case CLIENT_DATA_NOT_TRANSMISSABLE -> result = error;
                    default -> throw new CodecException("Unexpected enum found: " + getError().getErrorType());
                }
            } else {
                // We have no error. But there might be one now due to the context.
                result = revalidateNonNullValue();
            }
        }
        return result;
    }


    public PageVarError validateAndCeckIfInvalidNullOld() {   // TODO PRIO1 VALIDATING: Handle synchronized state

        PageVarError result;

        if (!isRelevant()) {
            result = null;
        } else {
            if (hasError()) {
                // A parsing error cannot be resolved by re-validating.
                if (getError().getErrorType() == PageVarError.ErrorType.SERVER_SIDE_PARSING) {
                    result = error;
                } else {
                    if (isNull()) {
                        if (!fieldAssistant.canBeNull) {
                            result = new PageVarError(this, "Field must not be empty");
                        } else {
                            clearError();
                            unparsedStringValue = null;
                            result = null;
                        }
                    } else {
                        // We have an error. But it might be not valid anymore due to the context.
                        // Hence we revalidate.
                        result = revalidateNonNullValue();
                    }
                }
            } else {
                // We have no error. But there might be one now due to the context.
                result = revalidateNonNullValue();
            }
        }
        return result;
    }

    private PageVarError revalidateNonNullValue() {
        assert(getVal() != null);
        PageVarError result;
        FieldError errorAfterRevalidation = fieldAssistant.validate(this.getValCore());
        if (errorAfterRevalidation != null) {
            var pve = new PageVarError(this, errorAfterRevalidation.getMessage());
            setError(pve);
            result = pve;
        } else {
            clearError();
            unparsedStringValue = null;
            result = null;
        }
        return result;
    }


    public String getPageVarId() {
        return pageVarId;
    }

    public void setPageVarId(String pageVarId) {
        this.pageVarId = pageVarId;
    }


    public void setError(PageVarError error) {
        this.error = error;
    }

    public void setError(String errorMsg) {
        new PageVarError(this, errorMsg, true);
    }

    @Override
    public void setClientSideIncompleteOrParsingError() {
        var newError = PageVarError.createClientSideIncompleteOrParsingError(this);
        setError(newError);
    }

    @Override
    public void setServerSideParsingError(String errorMsg) {
        var newError = PageVarError.createServerSideParsingError(this, errorMsg);
        setError(newError);
    }

    public PageVarError getError() {
        return error;
    }

    public PageVarError getEffectiveError() {
        if (! isRelevant()) {
            return null;
        }
        return error;
    }

    public void clearError() {
        error = null;
    }

    public boolean hasError() {
        return getError() != null;
    }

//    public boolean hasEffectiveServerSideError() {
//        return getEffectiveError() != null;
//    }


    public SimpleFieldAssistant<F> getFieldAssistant() {
        return fieldAssistant;
    }

    public PageStateVarBase setFieldAssistant(FieldAssistant fieldAssistant) {
        this.fieldAssistant = fieldAssistant;
        return this;
    }

    public static PageStateVarColdLink createPageStateVar(PageState pageState, SimpleFieldAssistant fieldAssistant) {
        return createPageStateVar(pageState, fieldAssistant, null);
    }

    public static PageStateVarColdLink createPageStateVar(PageState pageState, SimpleFieldAssistant fieldAssistant, String fieldPrefix) {
        var fieldType = fieldAssistant.getFieldType();
        PageStateVarColdLink result =
        switch (fieldAssistant.getFieldType()) {
            case STR -> new PageVarStringColdLink(pageState, fieldAssistant, fieldPrefix);
            case INT -> new PageVarIntColdLink(pageState, fieldAssistant, fieldPrefix);
            case LONG -> new PageVarLongColdLink(pageState, fieldAssistant, fieldPrefix);
            case FLOAT -> new PageVarFloatColdLink(pageState, fieldAssistant, fieldPrefix);
            case BOOL -> new PageVarBoolColdLink(pageState, fieldAssistant, fieldPrefix);
            case LOCALDATETIME ->  new PageVarLocalDateTimeColdLink(pageState, fieldAssistant, fieldPrefix);
            case LOCALDATE ->   new PageVarLocalDateColdLink(pageState, fieldAssistant, fieldPrefix);
            case LOCALTIME -> new PageVarLocalTimeColdLink(pageState, fieldAssistant, fieldPrefix);
            case ENM -> new PageVarEnumColdLink(pageState, fieldAssistant, fieldPrefix);
        };

        return result;
    }

    public String generateHtmlId(PageState pageState, FieldAssistant fieldAssistant) {
        return generateHtmlId(pageState, fieldAssistant, null);
    }
    public String generateHtmlId(PageState pageState, SimpleFieldAssistant fieldAssistant, String fieldPrefix) {

        String pageStateIdPrefix = pageState.getPageStateId() != null ? pageState.getPageStateId() + "-" : "";
        String fieldPrefixPart = fieldPrefix != null ? fieldPrefix + "-" : "";

        return pageStateIdPrefix + fieldPrefixPart + fieldAssistant.getFieldName().name();
    }

    public PageState getPageState() {
        return pageState;
    }

    public boolean stringValueWasEditied() {
        if (unparsedStringValue != null) {
            // TODO: Compare the parsed values for equality
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isMeaningless() {
        return ! isRelevant();
    }

    @Override
    public boolean isRelevant() {
        if (checkRelevance == null) {
            return true;
        } else {
            return checkRelevance.apply(this);
        }
    }

    public void setCheckRelevance(Function<PageStateVarIntf, Boolean> checkRelevance) {
        this.checkRelevance = checkRelevance;
    }




}
