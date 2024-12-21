package com.kewebsi.service;

import com.kewebsi.controller.*;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.errorhandling.ExpectedClientDataError;
import com.kewebsi.html.PageState;
import com.kewebsi.html.table.EntityForDetailDisplayProvider;
import com.kewebsi.util.DebugUtils;

/**
 *
 * @param <T> Type of the entity
 * @param <F> Type of the field
 */
public class PageVarEntityProviderBacked<T, F> extends PageStateVarBase<F> {

    protected EntityForDetailDisplayProvider<T> entityProvider;

    protected DtoAssistant<T> dtoAssistant;

    protected ManagedEntity<T> entityWhenErrorWasSet;
    protected ManagedEntity<T> lastDisplayedEntity;
    protected Object valWhenErrorWasSet;


    public PageVarEntityProviderBacked(PageState pageState, EntityForDetailDisplayProvider<T> entityProvider, DtoAssistant dtoAssistant, FieldAssistant fieldAssistant) {
        initPageStateVar(pageState,  generateHtmlId(pageState, fieldAssistant), fieldAssistant );
        this.entityProvider = entityProvider;
        this.dtoAssistant = dtoAssistant;
    }


//    public void initPageStateVar(PageState pageState, String htmlId, FieldAssistant fieldAssistant) {
//        this.pageState = pageState;
//        this.pageVarId = htmlId;
//        this.unparsedStringValue = null;
//        this.fieldAssistant = fieldAssistant;
//        pageState.registerPageStateVar(this);
//    }



    @Override
    public void clearValue() {
      dtoAssistant.clearValue(fieldAssistant.getFieldName(), getManagedEntity());
    }


    @Override
    public void setStringValueFromClient(String userInputString) {
        unparsedStringValue = userInputString;
        var error = validateUnparsedStringValueAndSetValueOrErrorAllowNull();
        if (error == null) {
            unparsedStringValue = null;
        }
    }

    @Override
    public PageVarError validateUnparsedStringValueAndSetValueOrErrorAllowNull() {
        checkAccidentalOverwriting();

        try {
            dtoAssistant.setValueStr(fieldAssistant.getFieldName(), unparsedStringValue, getManagedEntity(), FieldUpdateInfo.UpdateDirection.FRONT_TO_BACK);
            clearError();
            unparsedStringValue = null;
        } catch (ExpectedClientDataError e) {
            setError(new PageVarError(this, e.getMessage()));
        }
        return getError();
    }

    @Override
    public void setValueCore(F value) {
        if (getManagedEntity() != null) {  // TODO: Disable entitybacked fields, if there is no entity
            checkAccidentalOverwriting();
            dtoAssistant.setValueObjectCore((FieldAssistant) fieldAssistant, value, getManagedEntity());
        }
    }

    @Override
    public void setError(PageVarError error) {
        super.setError(error);
        entityWhenErrorWasSet = getManagedEntity();
        valWhenErrorWasSet = getValCore();
    }

    @Override
    public void clearError() {
        super.clearError();
        entityWhenErrorWasSet = null;
        valWhenErrorWasSet = null;
    }


    @Override
    public PageVarError getError() {
        updateErrorIfBackingValueChanged();
        return getErrorCore();
    }

    @Override
    public boolean hasError() {   // TODO: Consider renaming to hasErrorCheckingForeignUpdates
        updateErrorIfBackingValueChanged();
        return hasErrorCore();
    }


    protected void updateErrorIfBackingValueChanged() {
        boolean errorIsCurrentlyDisplayed  = (getErrorCore() != null);

        // When there is no set error, we are done.
        // (We validate during setting of variables, not during this error check).
        if (! errorIsCurrentlyDisplayed) {
            return;
        }

        // When here, we are currently displaying an error. But this error is only of interest, if the underlying value has not
        // changed, for example because the value was updated in another input field on the page or via business logic.
        // We clear then the error so that a) the error info is not displayed anymore and b) the last (errornous)
        // user input is replaced by the new value.
        // Note that the error is attached to the PageVar, not the attribute of the entity. In other words, with regards
        // to this error handling mechanics, the values in the entity are always error-free.

        boolean errorStillValid = false;
        if (entityWhenErrorWasSet == getManagedEntity()) {  // Same entity
            if (valWhenErrorWasSet.equals(getValCore())) {  // Same value of entity. That is, the value of the entity has not been changed circumventing this PageVarEntityBacked.
                errorStillValid = true;
            }
        }

        if (! errorStillValid) {
            clearError();
        }
    }

    protected PageVarError getErrorCore() {
        return error;
    }

    protected boolean hasErrorCore() {
        return getErrorCore() != null;
    }


    public ManagedEntity<T> getManagedEntity() {
        return entityProvider.getManagedEntityForDetailDisplay();
    }

    public T getEntity() {
        ManagedEntity<T> me = getManagedEntity();
        if (me == null) {
            return null;
        } else {
            return me.getEntity();
        }
    }


    @Override
    public String getDisplayString() {  // TODO: Consider renaming to getHtmlString
        var entity = getManagedEntity();
        String result;
        if (hasError()) {
            if (entity == entityWhenErrorWasSet) {  // Normal error case.
                result = unparsedStringValue != null ? unparsedStringValue : "";
            } else {   // Entity was changed. The error stems from the old displayed entity
                clearError();  // The error of the old instance has been detected and will be cleared here.
                result = getStringValFromEntityHandlingNulls(entity);

            }
        } else {
            result = getStringValFromEntityHandlingNulls(entity);
        }
        entityWhenErrorWasSet = entity;
        lastDisplayedEntity = entity;
        return result;
    }

    /**
     * Do not call from outside. Does not update lastDisplayedEntity, which should be done by the calling method.
     * @param entity
     * @return
     */
    protected String getStringValFromEntityHandlingNulls(ManagedEntity<T> entity) {
        if (entity == null) {
            return "";
        } else {
            var val = dtoAssistant.getValueAsBaseType(entity, fieldAssistant.getFieldName());
            return val != null ? val.toHtml() : "";
        }
    }



    public void resetToEntity() {   // use in mapEntityToPageState XXXXXXXXXXXXXXXXXXX
        var entity = getManagedEntity();
        if (entity == null) {
            unparsedStringValue = null;
        } else {
            unparsedStringValue = dtoAssistant.getValueAsBaseType(entity, fieldAssistant.getFieldName()).toHtml();
        }
    }

    @Override
    public String toString() {
        String result = String.format("PageVarEntityBacked: dtoAssistant:%s, fieldAssistant%s, stringValue:%s", this.dtoAssistant.getSymbol(), this.fieldAssistant.getFieldName(), this.getValCore());
        return result;
    }

    @Override
    public F getVal() {
        if (hasError()) {
            throw new CodingErrorException("Attempt to read a value of an PageVar with an error:" + this);
        }
        return getValCore();
    }

    @Override
    public F getValCore() {
        var entity = getManagedEntity();
        lastDisplayedEntity = entity;
        if (entity == null) {
            return null;
        }
        BaseVal<F> baseVal = dtoAssistant.getValueAsBaseType(entity, fieldAssistant.getFieldName());
        if (baseVal== null) {
            return null;
        }
        return baseVal.getVal();
    }


    @Override
    public boolean isMeaningless() {
        if (getManagedEntity() == null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isRelevant() {
        return ! isMeaningless();
    }


    public void checkAccidentalOverwriting() {
        if (DebugUtils.DEBUG_CHECKS_ON) {
            if (lastDisplayedEntity != getManagedEntity()) {
                throw new CodingErrorException("Attempt to overwrite an unseen value by the client.");
            }
        }
    }


}
