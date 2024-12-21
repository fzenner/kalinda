package com.kewebsi.html;

import com.fzenner.datademo.web.outmsg.GuiDef;
import com.kewebsi.errorhandling.ErrorInfo;
import com.kewebsi.service.PageVarError;

import java.util.Objects;

public abstract class AbstractPageVarField<T>extends HtmlTag implements PageVarEditor<T> {



    protected PageStateVarIntf<T> pageStateVar;

    /**
     * A field is not synced, when the gui has delivered a value that could not be stored in the PageStateVar.
     * The initial value is true, since we create a field with the value from the
     * Example case: Error when the 31.2.2022 is entered as date.
     */
    protected boolean clientIsSynced = true;

    protected ClientSyncState clientSyncState;

    protected T valueOld;
    protected PageVarError errorOld = null;
    protected boolean requiredOld;
    protected boolean disabledOld;

    @Override
    public boolean isContentOrGuiDefModified() {
        if (valueModified()) {
            return true;
        }
        if (clientNeedsRefresh()) {
            return true;
        }
        if (errorToDisplayToClientIsModified()) {
            return true;
        }
        if (isRequiredModified()) {
            return true;
        }
        if (isDisabledModified()) {
            return true;
        }
        if (isVisibilityModified()) {
            return true;
        }
        return false;
    }


    public void setNotModified() {
        setContentOrGuiDefNotModified();
        setErrorNotModified();
        setDisabledNotModified();
        setCssClassesNotModified();
        setVisibilityNotModified();
    }

    // We do not distinguish between content and attribute modification. Since we send a similar GuiUpdate (shrinkwrapped) in both cases.
    @Override
    public void setContentOrGuiDefNotModified() {
        setGuiDoesNotNeedRefresh();
        setOldValuesToCurrentValues();
        setClientIsSynced();

    }


    public void setOldValuesToCurrentValues() {
        setValueOld(getValue());
        setErrorOld(getEffectiveError());
        setDisabledOld(isDisabled());
        setRequiredOld(isRequired());
    }

    // When a formerly disabled element is enabled again, we push the current server-side value to the client,
    // since the client might have some junk text in the field when he disabled it. We do not re-evaluate this
    // old junk, set error state etc. The clean solution here is to set the server side value.
    // WHen
    // Even if the server side value had an error that wie did ignore when the field was disabled, we
    // set the value here.
    // Even if we deleted the error when we disabled a field and re-set now a bad value, ee rely on the
    // re-evaluation of all relevant fields on the server side at the
    // beginning of a transaction. (Other options are thinkable, e.g. to re-establish the error from before
    // the field was disabled, but that could be considered unexpected behaviour by the user.)
    // TODO: We need to figure out whether we should keep the server error when disabling. (An error on a disabeld
    // field is a disabled error!!!
//    public void setErrorInfoIfErrorInfoModified(GuiDef guiDef) {
//        if (isErrorModified()) {
//            if (getErrorInfo() == null) {
//                guiDef.errorInfo = ErrorInfo.wireNull();  // We need to be able to erase the error message.
//            } else {
//                guiDef.errorInfo = getErrorInfo();
//            }
//        }
//    }

    public ErrorInfo calculateModificationOfErrorInfoToSendToClient() {
        var errorOld = getErrorOld();
        var errorNew = getEffectiveError();
        return calculateModificationOfErrorInfoToSendToClient(errorOld, errorNew);
    };

    public static ErrorInfo calculateModificationOfErrorInfoToSendToClient(PageVarError oldError, PageVarError newError) {

        if (Objects.equals(oldError, newError)) {
            return null;
        }

        if (oldError == null) {

            if (newError.isClientSideError()) {
                return null;
            }
            return newError.getErrorInfo();
        }

        if (oldError.isClientSideError()) {
            if (newError == null) {
                // If there is only one field monitoring the page var connected to this field, then we would actually
                // not need to return any error info here and hence return null.
                // But a second connected field might still be showing a server side error, which it should remove now.
                return ErrorInfo.wireNull();
            }
            if (newError.isClientSideError()) {
                return null;
            }

            return newError.getErrorInfo();
        }

        // When here, oldError is a server side error.

        if (newError == null) {
            return ErrorInfo.wireNull();  // Server error display on the client side should be removed.
        }

        if (newError.isClientSideError()) {
            // return ErrorInfo.wireNull();  // Server error display on the client side should be removed.
            return null;  // The client is solely responsible for handling client side errors.
        }

        return newError.getErrorInfo();
    }



    public T calculateModificationOfValue() {
        T valueModification = null;
        if (valueModified()) {
            valueModification = getValue();
        }
        return valueModification;
    };

    public Boolean calculateModificationOfRequired() {
        Boolean requiredModification = null;
        if (isRequiredModified()) {
            requiredModification = !pageStateVar.getFieldAssistant().canBeEmpty();
        }
        return requiredModification;
    }

    /**
     *
     * @return null, when there was no mudification
     *         Thee disabled value when there was a modification.
     *
     */
    public Boolean calculateModificationOfDisabled() {
        Boolean modificationOfDisabled = null;
        if (isDisabledModified()) {
            modificationOfDisabled = isDisabled();
        }
        return modificationOfDisabled;
    }

    public void setVisiblityAndError(GuiDef guiDef) {
        if (getVisibility() != null) {
            guiDef.visibility = getVisibility();
        }

        if (pageStateVar.hasEffectiveServerSideError()) {
            guiDef.errorInfo = new ErrorInfo(pageStateVar.getEffectiveError().getErrorMsg());
        }
    }


    public boolean reEnabled() {
        boolean result = isDisabledModified() && !isDisabled();
        return result;
    }

    public ErrorInfo calcErrorInfo() {
        if (pageStateVar.hasEffectiveServerSideError()) {
            return new ErrorInfo(pageStateVar.getEffectiveError().getErrorMsg());
        }
        return null;
    }



    @Override
    public boolean isAttributesModified() {
        boolean result =  isDisabledOld() != isDisabled() || isRequiredOld() != isRequired();
        return result;
    }

    public boolean hasError() {
        return getPageStateVar().hasEffectiveError();
    }


    public void setErrorNotModified() {
        setErrorOld( getPageStateVar().getEffectiveError());
    }

    public void setDisabledNotModified() {
        setDisabledOld(isDisabled());
    }

    protected boolean valueModified() {

        // Only when the client is supposed to by in sync, we update the data.
        if (! getClientIsSynced()) {
            return false;
        }

        T currentValue = getValue();
        if (! java.util.Objects.equals(currentValue, getValueOld())) {
            return true;
        }

        return false;
    }

    public abstract T getValue();


    public T getValueOld() {
        return valueOld;
    }


    public void setValueOld(T valueOld) {
         this.valueOld = valueOld;
    }


    public boolean isRequired() {
        return ! getPageStateVar().getFieldAssistant().canBeEmpty();
    }

    abstract public boolean clientNeedsRefresh();

    public void setGuiDoesNotNeedRefresh() {

    }

    public PageVarError getErrorOld() {
        return errorOld;
    }

    public void setErrorOld(PageVarError errorOld) {
        this.errorOld = errorOld;
    }



    public boolean errorToDisplayToClientIsModified() {

        PageVarError oldError = getErrorOld();
        PageVarError newError = getEffectiveError();

        if (Objects.equals(getErrorOld(), getEffectiveError())) {
            return false;
        }

        if (oldError == null) {
            return true;   // When here, newError is not null.
        }

        if (newError == null) {
            return true;   // When here, oldError is not null.
        }

        // When here, oldError and newError are both not null and not exactly the same.
        // When old and new error are clientSideErrors, we really consider them equal
        if (oldError.isClientSideError() && newError.isClientSideError()) {
            return false;
        }

        return true;


    }

    public boolean isRequiredModified() {
        return isRequired() != isRequiredOld();
    }

    public boolean isDisabled() {
        return ! pageStateVar.getFieldAssistant().isEditable() || ! pageStateVar.isRelevant();
    }

    @Override
    public PageStateVarIntf getPageStateVar() {
        return pageStateVar;
    }

    public boolean isRequiredOld() {
        return requiredOld;
    }

    public void setRequiredOld(boolean requiredOld) {
        this.requiredOld = requiredOld;
    }


    public boolean isDisabledModified() {
        return disabledOld != isDisabled();
    }



    public boolean isDisabledOld() {
        return disabledOld;
    }

    public void setDisabledOld(boolean disabledOld) {
        this.disabledOld = disabledOld;
    }

    public ClientSyncState getClientSyncState() {
        return clientSyncState;
    }


    public boolean getClientIsSynced() {
        return clientIsSynced;
    }

    public void setClientIsSynced() {
        this.clientIsSynced = true;
        this.clientSyncState = ClientSyncState.CLIENT_IS_SYNCED;
    }

    public void setClientIsNotSynced(ClientSyncState clientSyncState) {
        this.clientIsSynced = false;
        this.clientSyncState = clientSyncState;
    }

    @Override
    public String toString() {
        return "id: " + id + " value:" + getValue() + " valueOld:" + valueOld
                + " error: " + hasError() + " hasErrorOld " + errorOld;
    }

    public PageVarError getEffectiveError() {
        return getPageStateVar().getEffectiveError();
    }

    public PageVarError getRawError() {
        return getPageStateVar().getError();
    }
}
