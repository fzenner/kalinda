package com.kewebsi.html;

import com.fzenner.datademo.web.outmsg.GuiDef;
import com.fzenner.datademo.web.outmsg.ValueChange;
import com.kewebsi.errorhandling.ErrorUpdate;
import com.kewebsi.service.PageVarError;
import com.kewebsi.service.PageVarErrorCore;

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
    protected PageVarErrorCore errorOld = null;
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
        // setClientIsSynced();  XXX we we wrongly assume here that the client is in sync

    }


    public void setOldValuesToCurrentValues() {
        setValueOld(getValue());
        setErrorOld(getEffectivePageVarError());
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

    public ErrorUpdate calculateModificationOfErrorInfoToSendToClientAndUpdateClientSyncState() {
        var errorOld = getErrorOld();
        var errorNew = getEffectivePageVarError();
        return calculateModificationOfErrorInfoToSendToClientAndUpdateClientSyncState(errorOld, errorNew);
    };


    public void getLatestEditingField() {

    }

    public static void log(String msg) {
        System.out.println(msg);
    }

    public ErrorUpdate calculateModificationOfErrorInfoToSendToClientAndUpdateClientSyncState(PageVarErrorCore oldError, PageVarErrorCore newError) {

        log("calculateModificationOfErrorInfoToSendToClientAndUpdateClientSyncState:" + this.id);

        if (oldError == null && newError == null) {
            log("pos001");
            return null;
        }

        log("pos002");
        if (oldError == null) {
            log("pos003");
            if (newError.isClientSideError()) {
                log("pos004");
                if (pageStateVar.getLastUpdatingEditor() == this) {
                    log("pos005");
                    // When here, the client has created a client side error and we are the field that has created it.
                    // We do not need to inform the client about an error that it has created itself.
                    return null;
                }
                log("pos006");
                // When here, we have a client editor that not the last updating editor.
                // But now another field has created a client side error on the same page var.
                // (No error existed before at all.)
                // This means, that the client is not in sync anymore (due to the other field).
                // We need to inform the client about this error, since it might have a valid and synced looking value
                // which we must assume does not reflect the users intention anymore because he has been edited
                // (so far without successfully syncing) another field that is connected to the same page var.
                setClientIsNotSynced(ClientSyncState.CLIENT_NOT_SYNCED_BY_OTHER_FIELD);
                return new ErrorUpdate(ErrorUpdate.StandardErrorCodes.NOT_SYNCED_BY_OTHER_FIELD, "Sync error");
            }

            log("pos007");

            // When here, we have a new client side error replacing no error at all.
            return newError.getErrorInfo();
        }

        log("pos008");

        if (oldError.isClientSideError()) {
            log("pos009");
            if (newError == null) {
                log("pos010");
                // If there is only one field monitoring the page var connected to this field, then we would actually
                // not need to return any error info here and hence return null.
                // But a second connected field might still be showing a server side error, which it should remove now.
                return ErrorUpdate.wireNull();
            }
            if (newError.isClientSideError()) {
                log("pos011");
                log("Last updating editor: " + pageStateVar.getLastUpdatingEditor());
                if (pageStateVar.getLastUpdatingEditor() == this) {
                    log("pos012");
                    // When here, the client has created a (new) client side error (replacing an old client side error)
                    // and we are the field that has created it.
                    // We do not need to inform the client about an error that it has created itself.
                    return null;
                }
                log("pos013");
                // When here, we have a client editor that not the last updating editor.
                // But now another field has created a client side error on the same page var.
                // This means, that the client is not in sync anymore (due to the other field).
                // We need to inform the client about this error, since it might have a valid and synced looking value
                // which we must assume does not reflect the users intention anymore because he has been edited
                // (so far without successfully syncing) another field that is connected to the same page var.
                // If the editor had an older client side error, we will replace that error by the new one.
                setClientIsNotSynced(ClientSyncState.CLIENT_NOT_SYNCED_BY_OTHER_FIELD);
                return new ErrorUpdate(ErrorUpdate.StandardErrorCodes.NOT_SYNCED_BY_OTHER_FIELD, "Sync error");
            }
            log("pos014");
            // When here, we have a new server side error replacing a client side error.
            return newError.getErrorInfo();
        }
        log("pos015");

        // When here, oldError is a server side error.

        if (newError == null) {
            log("pos016");
            return ErrorUpdate.wireNull();  // Server error display on the client side should be removed.
        }
        log("pos017");

        if (newError.isClientSideError()) {
            log("pos018");
            // return ErrorInfo.wireNull();  // Server error display on the client side should be removed.
            return null;  // The client is solely responsible for handling client side errors.
        }

        return newError.getErrorInfo();
    }


    public ValueChange<T> calculateModificationOfValue() {
        if (! valueModified()) return null;
        T newValue = getValue();
        if (newValue == null) {
            return new ValueChange<>(null);
        }
        return new ValueChange<>(newValue);
    }


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
            guiDef.errorUpdate = new ErrorUpdate(pageStateVar.getEffectiveError().errorMsg());
        }
    }


    public boolean reEnabled() {
        boolean result = isDisabledModified() && !isDisabled();
        return result;
    }

    public ErrorUpdate calcErrorInfo() {
        if (pageStateVar.hasEffectiveServerSideError()) {
            return new ErrorUpdate(pageStateVar.getEffectiveError().errorMsg());
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

        T oldValue = getValueOld();
        T currentValue = getValue();
        // Only when the client is supposed to by in sync, we update the data.
        if (! getClientIsSynced()) {  // We need to distinguish between not synced by this field and not synced by other fields. switch on clientSyncState
            var lue = pageStateVar.getLastUpdatingEditor();
            if (lue != this && lue.getClientSyncState() == ClientSyncState.CLIENT_IS_SYNCED) {
                // When here, we are not the last editor that has updated the page var and we
                // are out of sync due to an earlier bad input.
                // If the value has changed, we do not want to propagate this change to the client, since it must
                // have been changed by another field that successfully updated the page var.
                // Hence we do not update our value, since it might be out of date.
                // We do even update when the old value is the same as the new value, since the last editing field
                // might have switched from unparseable to parseable input without changing the value in the page var
                // while we had an unparseable string in the field ourselves.
                // Consider the following sequence of events:
                // - Field A and Field B are connected to the same page var P.
                // - The user enters "1" in field A. The value 1 is stored in P and also displaxyed in field B.
                // - The user enters "yyy" in field B. This is unparseable. No value change happens. Error info is updated on the fields.
                // - The users entes "xxx" in field A. This is unparseable. No value change happens. Error info is updated on the fields.
                // - The user enters "1" again in field A. This is parseable and the value 1 is stored in P again but the value has
                //   not changed. But field B must be updated to display "1" again instead of "yyy".

                return true;
            }

            // When here, we are the last editor that has updated the page var and we
            // are out of sync due to our own bad input. There is no new value to propagate to the client.
            return false;
        }


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

    public PageVarErrorCore getErrorOld() {
        return errorOld;
    }

    public void setErrorOld(PageVarErrorCore errorOld) {
        this.errorOld = errorOld;
    }



    public boolean errorToDisplayToClientIsModified() {

        PageVarErrorCore oldError = getErrorOld();
        PageVarErrorCore newError = getEffectivePageVarError();

        if (Objects.equals(oldError, newError)) {
            return false;
        }

        return true;
//
//        if (Objects.equals(getErrorOld(), getEffectiveError())) {
//            return false;
//        }
//
//        if (oldError == null) {
//            return true;   // When here, newError is not null.
//        }
//
//        if (newError == null) {
//            return true;   // When here, oldError is not null.
//        }
//
//        // When here, oldError and newError are both not null and not exactly the same.
//        // When old and new error are clientSideErrors, we really consider them equal
//        if (oldError.isClientSideError() && newError.isClientSideError()) {
//            return false;
//        }
//
//        return true;


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

    public PageVarErrorCore getEffectivePageVarError() {
        return getPageStateVar().getEffectiveError();
    }

    public PageVarErrorCore getRawError() {
        return getPageStateVar().getError();
    }
}
