package com.kewebsi.html;

import com.kewebsi.controller.SimpleFieldAssistant;
import com.kewebsi.service.PageVarError;

/**
 *
 * A PageVariable has the following responsibilities:
 * - Value store: It holds a scalar value (e.g. String, Integer, DateTime) that is displayed by zero, one,
 *   or multiple fields on a page.
 *   (Typically, it is displayed by one, in some cases by two fields.)
 * - Input conversion: It converts the string representation as entered by a human user to the internal
 *   scalar representation.
 * - Output Conversion: It computes and provides the canonical, well formatted string representation to be displayed by the client,
 *    if a valid scalar value exists.
 * - Intermediat state handling: It holds and manages the intermediate string representation of the client entries if the client entries
 *   have not been successfully converted to the respective scalar value. Thus, unfinished entries are part
 *   of the server state and allow for comprehensive server-side handling. (No value loss at page refresh.) TODO: To be reconsidered because it is very complicate. / Not true for newer versions of this code.
 * Page variables can be standalone (having their own scalar variable) or use the variables of a server-side
 * entity (entity-backed PageVariables)  When using an entity-backed entity, the input conversion already uses the
 * validation of the respective FieldAssistant and more comprehensive validations involving the entity and its assistants
 * can be implemented. Entity-backed page variables participate in higher-level funcions for entitiy-editing, e.g.
 * linked table and detail editing.
 *
 * @param <F> Type of the variable hold by the PageStateVar
 *
 *
 */
public interface PageStateVarIntf<F>  {

    public F getVal();
    public F getValCore();

    public boolean isNull();

    public boolean getIsInSyncWithGui();

    //
    // Setter
    //
    public void setStringValueFromClient(String userInputString);

    public void setStringValueFromClientAllowNull(String userInputString);

    public void setValueFromClient(F valueObject);

    public void clear();

    public void clearError();


    //
    // Getter
    //

    /**
     *
     * @return The value of the variable as string, not yet HTML-escaped.
     * If the value is null, then return the empty String.
     * If the page has an error, the unparsedStringValue is returned.
     */
    abstract public String getDisplayString();

    public String getUnparsedStringValue();
    public String getPageVarId();

    //
    // Error Handling
    //

    public void setError(PageVarError error);

    public void setError(String errorMsg);

    public void setClientSideIncompleteOrParsingError();

    public void setServerSideParsingError(String errorMsg);

    public PageVarError getError();

    public default PageVarError getEffectiveError() {
        if (isMeaningless()) {
            return null;
        }
        return getError();
    }

    /**
     * Validates its own value (again) and checks if it is null, if null is allowed.
     *
     * If an error occurs, the error is attached to the page variable.
     *
     * This revalidation is necessary before finally using the value of a field for a transaction.
     * The validation step before executing a transaction is necessary, because the page variable might still be
     * in its initial null state (main case) of because the context has changed and with it the current value
     * is not valid anymore (edge case). An implementation could shortcut any potentially expensice validation
     * based on whether the input has changed since last validate. But this performance optimizatino should rarely be
     * necessary.
     * @return
     */
    public PageVarError validateAndCeckIfInvalidNull();




    public boolean hasError();   // Consider renaming to hasErrorCheckingForeignUpdates

    /**
     * This function is meant to be used to decide, whether we need to propagate an error from the server
     * to the client. That is not the case, if the error has been created on the client side.
     * An error is not effective, if this pagevar is meaningless or if the error is on the client side.
     * @return
     */
    public default boolean hasEffectiveServerSideError() {
        if (isMeaningless()) {
            return false;
        }
        if (hasError()) {
            if (getError().getErrorType() != PageVarError.ErrorType.CLIENT_DATA_NOT_TRANSMISSABLE) {
                return true;
            }
        }
        return false;
    };


    public default boolean hasEffectiveError() {
        if (isMeaningless()) {
            return false;
        }

        return hasError();
    };


    // public ClientSyncState getClientSyncState();

    // Status Handling
    public boolean stringValueWasEditied();

    //
    // Linking
    //
    public SimpleFieldAssistant<F> getFieldAssistant();
    public PageState getPageState();

    /**
     * A page state variable is meaningless if it cannot be accessed, expecially not be written in the current context
     * in a meaningful way. For example, when you edit an entity via this variable but the entity does not exist,
     * then editing this variable might not be meaningful.
     * @return
     */
    public boolean isMeaningless();

    /**
     * The opposite of isMeaningless.
     * @return
     */
    public boolean isRelevant();


//    void notifyClientInputIncomplete(); XXXXXX question: should this be on the field, not on the variable? What if we have multiple fields for one variable? Do we have really multiple fields for one variable at all?
//
//    void notifyClientInputUnparseable();
}
