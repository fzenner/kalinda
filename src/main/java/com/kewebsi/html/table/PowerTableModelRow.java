package com.kewebsi.html.table;

import com.kewebsi.controller.*;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.errorhandling.ExpectedClientDataError;
import com.kewebsi.service.FieldError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PowerTableModelRow<T> implements ManagedEntity {
	
	protected T payload;
	protected DtoAssistant entityAssistant;

	public HashMap<Enum, TableCellError> fieldErrors;

	// public ArrayList<FieldUpdateInfo> updatedFields;
	public HashMap<Enum, FieldUpdateInfo> updatedFields;


	/**
	 * Modified in the sense of a diffence between the value in the browser (last emitted HTML value)
	 * and the cell values in the model. Not to confuse with RowState.
	 */
	private boolean modified;

	protected EntityState entityState;
	protected EntityState oldEntityState;
	protected boolean isSelected;
	protected boolean isSelectedOld;

	protected ChangeTracking changeTracking = ChangeTracking.PER_FLAG;


	public PowerTableModelRow(EntityState entityState, T payload, DtoAssistant entityAssistant) {
		this.entityState = entityState;
		this.payload = payload;
		this.entityAssistant = entityAssistant;
		resetOldEntityState();
	}
	
	public PowerTableModelRow(T payload, DtoAssistant entityAssistant) {
		this.entityState = ManagedEntity.EntityState.UNMODIFIED;
		this.payload = payload;
		this.entityAssistant = entityAssistant;
		resetOldEntityState();
	}
	
	public T getEntity() {
		return payload;
	}



	public EntityState getEntityState() {
		return entityState;
	}
	public void setEntityState(EntityState entityState) {
		this.entityState = entityState;
		markAsModified();
	}

	public void resetOldEntityState() {
		oldEntityState = entityState;
	}

	public boolean stateIsModified() {
		return entityState != oldEntityState;
	}


	boolean selectedIsModified() {
		return isSelected != isSelectedOld;
	}

	@Override
	public void notifyFieldUpdatedFrontToBack(Enum fieldName) {
		switch (entityState) {
			case UNMODIFIED: setEntityState(EntityState.UPDATED);
				break;
			case NEW: // Do nothing
				break;
			case UPDATED: // Do nothing
				break;
			case DELETED: //
				throw new CodingErrorException("Attempt to update a deleted entity:" + payload);
			default:
				throw new CodingErrorException("Unhandled case");
		}

		addUpdatedField(fieldName, FieldUpdateInfo.frontToBack(fieldName));
		markAsModified();
	}

	public void notifyFieldUpdatedFrontToBack(FieldAssistant fieldAssistant) {
		notifyFieldUpdatedFrontToBack(fieldAssistant.getFieldName());
	}

	/**
	 * Front-to-back modifications are not going to be stored in the DB, but connected displays need to be updated.
	 * @param fieldName
	 */
	public void notifyFieldUpdatedBackToFront(Enum fieldName) {
		addUpdatedField(fieldName, FieldUpdateInfo.backToFront(fieldName));
		markAsModified();
	}

	@Override
	public void notifyFieldUpdatedBackToFront(FieldAssistant fieldAssistant) {
		var fieldName = fieldAssistant.getFieldName();
		addUpdatedField(fieldName, FieldUpdateInfo.backToFront(fieldName));
		markAsModified();
	}


//	public void addUpdatedFieldFrontToBack(Enum fieldName) {
//		addUpdatedField(fieldName, FieldUpdateInfo.frontToBack(fieldName));
//	}
//
//	public void addUpdatedFieldBackToFront(Enum fieldName) {
//		addUpdatedField(fieldName, FieldUpdateInfo.backToFront(fieldName));
//	}

	public void addUpdatedField(Enum fieldName, FieldUpdateInfo.UpdateDirection updateInfo) {
		switch(updateInfo) {
			case BACK_TO_FRONT -> addUpdatedField(fieldName, FieldUpdateInfo.backToFront(fieldName));
			case FRONT_TO_BACK -> addUpdatedField(fieldName, FieldUpdateInfo.frontToBack(fieldName));
			case ERROR_INFO -> addUpdatedField(fieldName, FieldUpdateInfo.errorInfo(fieldName));
		}
	}


	public void addUpdatedField(Enum fieldName, FieldUpdateInfo newFieldUpdateInfo) {

		// FieldUpdateInfo newFieldUpdateInfo = new FieldUpdateInfo(fieldName, updateDirection);
		if (updatedFields == null) {
			updatedFields = new HashMap<>(this.entityAssistant.getColCount());
			updatedFields.put(fieldName, newFieldUpdateInfo);
		} else {
			if (!updatedFields.containsKey(fieldName)) {
				updatedFields.put(fieldName, newFieldUpdateInfo);
			} else {
				var currentFieldUpdateInfo = updatedFields.get(fieldName);
				var mergedFieldUpdateInfo = currentFieldUpdateInfo.createMerge(newFieldUpdateInfo);
				updatedFields.replace(fieldName, mergedFieldUpdateInfo);
			}
		}
		markAsModified();
	}


	public void removeUpdatedField(Enum fieldName) {

		if (updatedFields != null) {
			updatedFields.remove(fieldName);
		}
	}


	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		markAsModified();
	}


	public void putError(Enum fieldName, FieldError fieldError) {
		var entity = getEntity();
		var tce = new TableCellError(entity, fieldName, entityAssistant.getValueAsBaseType(entity, fieldName), fieldError.getBadValue(), fieldError.getMessage());
		putError(fieldName, tce);
	}

	public void putError(Enum fieldName, TableCellError fieldError) {
		if (fieldErrors == null) {
			fieldErrors = new HashMap<>(3);
		}
		fieldErrors.put(fieldName, fieldError);
		addUpdatedField(fieldName, FieldUpdateInfo.UpdateDirection.ERROR_INFO);
	}

	public TableCellError getError(Enum fieldName)  {
		TableCellError result = null;

		if (fieldErrors != null) {
			result = fieldErrors.get(fieldName);
		}
		return result;
	}


	public void clearError(Enum fieldName) {
		if (fieldErrors != null) {
			var removedError = fieldErrors.remove(fieldName);
			if (removedError != null) {
				addUpdatedField(fieldName, FieldUpdateInfo.UpdateDirection.ERROR_INFO);
			}
		}
	}

	public HashMap<Enum, TableCellError> getFieldErrors() {
		return fieldErrors;
	}


	public void removeOutdatedErrors() {
		if (fieldErrors == null) {
			return;
		}

		ArrayList<Enum> outdatedErrors = null;


		var entrySet = fieldErrors.entrySet();

		// Short version, but hard to observe:
		// entrySet.removeIf(entry -> errorIsStillValid(entry.getValue()));

		Iterator<HashMap.Entry<Enum, TableCellError>> iter = entrySet.iterator();
		while (iter.hasNext()) {
			var entry = iter.next();
			var error = entry.getValue();
			if (!errorIsStillValid(error)) {
				iter.remove();
			}
		}

	}


	public boolean errorIsStillValid(TableCellError cellErrorCurrentlyDisplayed) {
		T entityWhenErrorWasSet = (T) cellErrorCurrentlyDisplayed.getEntityWhenErrorWasSet();

		boolean errorStillValid = false;
		if (entityWhenErrorWasSet == null) {
			if (payload == null) {
				errorStillValid = true;
			}
		} else {
			if (payload == null) {
				errorStillValid = true;
			} else {
				if (entityWhenErrorWasSet == payload) {  // Same entity
					Enum col = cellErrorCurrentlyDisplayed.getErrornousField();
					BaseVal currentEntityVal = entityAssistant.getValueAsBaseType(payload, col);
					BaseVal whenErrorWasSetVal = (BaseVal) cellErrorCurrentlyDisplayed.getValWhenErrorWasSet();
					// Check if it is the same value of the entity. That is, the value of the entity
					// has not been changed via a process that circumvented this PageVarEntityBacked.
					// Not that the comparison is at runtime on BaseVal.
					if (whenErrorWasSetVal.equals(currentEntityVal)) {
						errorStillValid = true;
					}
				}
			}
		}
		return errorStillValid;
	}


	// TODO: Rewrite using errorIsStillValid()
	public TableCellError getErrorCheckingForeignUpdates(Enum col) {
		TableCellError cellErrorCurrentlyDisplayed = getError(col);
		boolean errorIsCurrentlyDisplayed  = (cellErrorCurrentlyDisplayed != null);

		// When there is no set error, we are done.
		// (We validate during setting of variables, not during this error check).
		if (! errorIsCurrentlyDisplayed) {
			return null;
		}

		// When here, we are currently displaying an error. But this error is only of interest, if the underlying value
		// from the entity has not
		// changed, for example because the value was updated in another input field on the page or via business logic.
		// We clear then the error so that a) the error info is not displayed anymore and b) the last (errornous)
		// user input is replaced by the new value.
		// Note that the error is attached to the PageVar, not the attribute of the entity. In other words, with regards
		// to this error handling mechanics, the value in the entity are always error-free.

		boolean errorStillValid = false;

		T entityWhenErrorWasSet = (T) cellErrorCurrentlyDisplayed.getEntityWhenErrorWasSet();

		if (entityWhenErrorWasSet == null) {
			if (payload == null) {
				errorStillValid = true;
			}
		} else {
			if (payload == null) {
				errorStillValid = true;
			} else {
				if (entityWhenErrorWasSet == payload) {  // Same entity
					BaseVal currentEntityVal = entityAssistant.getValueAsBaseType(payload, col);
					BaseVal whenErrorWasSetVal = (BaseVal) cellErrorCurrentlyDisplayed.getValWhenErrorWasSet();
					// Check if it is the same value of the entity. That is, the value of the entity
					// has not been changed via a process that circumvented this PageVarEntityBacked.
					// Not that the comparison is at runtime on BaseVal.
					if (whenErrorWasSetVal.equals(currentEntityVal)) {
						errorStillValid = true;
					}
				}
			}
		}

		TableCellError result;
		if (! errorStillValid) {
			var removedError = fieldErrors.remove(col);
			assert(removedError!=null);
			result = null;
		} else {
			result = cellErrorCurrentlyDisplayed;
		}

		return result;

	}


	public void markAsModified() {
		modified = true;
	}

	public boolean isModified() {
		if (modified) {
			// There was explicetly set the modified flag.
			return true;
		}
		if (updatedFields != null && ! updatedFields.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public void setNotModified() {
		modified = false;
		updatedFields = null;
		oldEntityState = entityState;
		isSelectedOld = isSelected;
	}

	public DtoAssistant<T> getEntityAssistant() {
		return entityAssistant;
	}


	public void setValue(Enum field, String strVal) throws ExpectedClientDataError {
		entityAssistant.setValueStr(field, strVal,getEntity());
		notifyFieldUpdatedFrontToBack(field);
	}

	@Override
	public boolean fieldIsUpdatedFrontToBackOrErrorState(Enum fieldName) {
		// if (updatedFields != null && updatedFields.contains(fieldName)) {
		if (updatedFields == null) {
			return false;
		}

		var fieldUpdateInfo = updatedFields.get(fieldName);

		if (fieldUpdateInfo.frontToBack() || fieldUpdateInfo.errorInfo()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean fieldIsUpdatedFrontToBackOrErrorState(FieldAssistant fieldAssistant) {
		return fieldIsUpdatedFrontToBackOrErrorState(fieldAssistant.getFieldName());
	}


	public HashMap<Enum, FieldUpdateInfo> getModifiedFields() {
		return this.updatedFields;
	}
}
