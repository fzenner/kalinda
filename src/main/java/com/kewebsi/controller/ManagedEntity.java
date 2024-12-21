package com.kewebsi.controller;

import com.kewebsi.service.FieldError;

public interface ManagedEntity<T> {
        public enum EntityState {UNMODIFIED, NEW, UPDATED, DELETED}

    public enum ChangeTracking {PER_FLAG, PER_BACKUP};

    /**
     *
     * @return Always NOTNULL
     */
    public T getEntity();

    public EntityState getEntityState();

    public void setEntityState(EntityState entityState);

    public void notifyFieldUpdatedFrontToBack(Enum fieldName);

    public void notifyFieldUpdatedFrontToBack(FieldAssistant fieldAssistant);

    public void notifyFieldUpdatedBackToFront(Enum fieldName);

    public void notifyFieldUpdatedBackToFront(FieldAssistant fieldAssistant);

    public boolean fieldIsUpdatedFrontToBackOrErrorState(Enum fieldName);
    public boolean fieldIsUpdatedFrontToBackOrErrorState(FieldAssistant fieldAssistant);

    public void putError(Enum fieldName, FieldError fieldError);

}
