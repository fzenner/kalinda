package com.kewebsi.html.table;


import java.util.ArrayList;
import java.util.HashMap;

import com.kewebsi.controller.BaseVal;
import com.kewebsi.controller.ManagedEntity;
import com.kewebsi.controller.TableCellError;
import com.kewebsi.errorhandling.ErrorInfo;
import com.kewebsi.html.ClientDataOrError;
import org.springframework.core.codec.CodecException;

import com.kewebsi.controller.DtoAssistant;

/**
 * 
 * @author Frank Zenner
 * This abstract model is agnostic of the type of the entity hold in this model.
 * Implementing models will have to overwrite method where the specific type
 * of the entity is required. Unfortunately also for creating an entity.
 *
 * @param <T> Payload of a model row in the sense of an entity.
 */
public abstract class PowerTableModelImpl<T> implements PowerTableModel<T> {
	
	protected  DtoAssistant<T> dtoAssistant;


	protected ArrayList<PowerTableModelRow<T>> rows;

	protected int rowIdxForDetailDisplay;
	
	int modificationCount = 0;

	protected boolean rowsInsertedOrDeleted = false;
	
	
	public PowerTableModelImpl() {

	}

	protected void setData(Iterable<T> dataList) {
		if (dataList == null) {
			rows = new ArrayList<PowerTableModelRow<T>>(0);
		} else {
			rows = new ArrayList<PowerTableModelRow<T>>();
			for (var dataRun : dataList) {
				rows.add(new PowerTableModelRow<T>(dataRun, dtoAssistant));
			}
		}
		touch();
		notifyRowsInsertedOrDeleted();
	}


	@Override
	public String getValueForHtml(int rowIdx, Enum<?> colIn) {

		var row = rows.get(rowIdx);

		TableCellError error = row.getError(colIn);
		if (row.getError(colIn)!= null) {
			return error.getBadValue();
		}

		T entity = row.getEntity();
		
		if (entity == null) {
			return null;
		}
		
		return dtoAssistant.getHtmlValue(entity, colIn);
	}


	@Override
	public ClientDataOrError getClientDataOrError(int rowIdx, Enum<?> colIn) {

		var row = rows.get(rowIdx);

		TableCellError error = row.getError(colIn);
		if (row.getError(colIn)!= null) {
			return new ClientDataOrError(new ErrorInfo(error.getMessage()));
		}

		T entity = row.getEntity();

		if (entity == null) {
			return null;
		}

		BaseVal baseVal = dtoAssistant.getValueAsBaseType(entity, colIn);
		return new ClientDataOrError(baseVal);
	}


	@Override
	public TableCellError getError(int row, Enum<?> col) {
		var tableRow = rows.get(row);
		return tableRow.getError(col);
	}

	@Override
	public TableCellError getErrorCheckingForeignUpdates(int row, Enum<?> col) {
		var tableRow = rows.get(row);
		return tableRow.getErrorCheckingForeignUpdates(col);
	}

	/**
	 * Returns the list of errors for a row after removing outdated errors.
	 * @param row Table row to be used
	 * @return List of errors. Null or empty list if no errors are present.
	 */
	public ArrayList<TableCellError> getErrorsCheckingForeignUpdates(int row) {
		var tableRow = rows.get(row);
		tableRow.removeOutdatedErrors();
		HashMap<Enum, TableCellError> fieldErrors = tableRow.getFieldErrors();
		if (fieldErrors == null || fieldErrors.isEmpty()) {
			return null;
		}
		return new ArrayList<>(fieldErrors.values());
	}


	/**
	 * Returns the list of errors in the table
	 * @return List of errors. Null or empty list if no errors are present.
	 */
	@Override
	public ArrayList<TableCellError> getErrorsCheckingForeignUpdates() {
		ArrayList<TableCellError> result = null;
		for (int rowIdx = 0; rowIdx < rows.size(); rowIdx++) {
			var tableRow = rows.get(rowIdx);
			ArrayList<TableCellError> errors = getErrorsCheckingForeignUpdates(rowIdx);
			if (errors != null) {
				if (result == null) {
					result = new ArrayList<>();
				}
				result.addAll(errors);
			}
		}
		return result;
	}


	@Override
	public ArrayList<TableCellDisplayError> getDisplayErrorsCheckingForeignUpdates() {


		ArrayList<TableCellDisplayError> result = null;
		for (int rowIdx = 0; rowIdx < rows.size(); rowIdx++) {
			var displayErrors = getDisplayErrorsCheckingForeignUpdates(rowIdx);
			if (result == null) {
				result = new ArrayList<>(displayErrors);
			} else {
				result.addAll(displayErrors);
			}
		}
		return result;
	}

	@Override
	public ArrayList<TableCellDisplayError> getDisplayErrorsCheckingForeignUpdates(int rowIdx) {
		ArrayList<TableCellDisplayError> result = null;
		var tableRow = rows.get(rowIdx);
		ArrayList<TableCellError> errors = getErrorsCheckingForeignUpdates(rowIdx);
		if (errors != null) {
			if (result == null) {
				result = new ArrayList<>();
			}
			for (var cellError: errors) {
				TableCellDisplayError tcde = new TableCellDisplayError(cellError.getErrornousField(), cellError.getMessage());
				result.add(tcde);
			}

		}
		return result;
	}


	public T getData(int rowIdx) {
		return rows.get(rowIdx).getEntity();
	}
	
	public ArrayList<PowerTableModelRow<T>> getRows() {
		return rows;
	}
	
	
	public PowerTableModelRow<T> getRow(int rowIdx) {
		return rows.get(rowIdx);
	}
	
	


	@Override
	public void insertRowWithNewEntity(int rowIdx, T entity) {
		var newRow = new PowerTableModelRow<T>(ManagedEntity.EntityState.NEW, entity, dtoAssistant);
		if (rows == null) {
			rows = new ArrayList<>();
		}
		rows.add(rowIdx , newRow);
		touch();
		notifyRowsInsertedOrDeleted();
	}

	@Override
	public T insertRowWithBlankEntity(int rowIdx) {
		var newEntity = dtoAssistant.createEntity();
		insertRowWithNewEntity(rowIdx, newEntity);
		notifyRowsInsertedOrDeleted();
		return newEntity;
	}
	
	@Override
	public void removeRow(int rowIdx) {
		rows.remove(rowIdx);
		notifyRowsInsertedOrDeleted();
		touch();
	}
	
	@Override
	public void setRowIsSelected(int rowIdx, boolean isSelected) {
		getRow(rowIdx).setSelected(isSelected);
		touch();
	}
	
	@Override
	public void toggleRowIsSelected(int rowIdx) {
		var row = getRow(rowIdx);
		row.setSelected(! row.isSelected());
		touch();
	}
	
	

	public int getModificationCount() {
		return modificationCount;
	}
	
	public void touch() {
		modificationCount++;
	}
	
	
	public boolean isModified() {
		if (modificationCount != 0) {
			return true;
		} else {
			for (var row : rows) {
				if (row.isModified()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void setNotModified() {
		modificationCount = 0;
		rowsInsertedOrDeleted = false;
		setRowsNotModified();
	}

	public void setRowsNotModified() {
		for (var row: rows) {
			row.setNotModified();
		}
	}
	
	
	@Override
	public DtoAssistant<T> getDtoAssistant() {
		return dtoAssistant;
	}
	
	
	@Override
	public Enum<?>[] getFieldNames() {
		return dtoAssistant.getFieldNames();
	}
	
	
	public int getFieldNameOrdinalFor(Enum<?> fieldName) {
		for (Enum<?> run : getFieldNames()) {
			if (run == fieldName) {
				return run.ordinal();
			}
		}
		throw new CodecException("No enum found for " + fieldName);
	}
	

	@Override
	public ManagedEntity.EntityState getRowState(int row) {
		return rows.get(row).getEntityState();
	}

	
	@Override
	public boolean getRowIsSelected(int row) {
		return rows.get(row).isSelected();
	}


	@Override
	public int getRowCount() {
		if (rows == null) return 0;
		return rows.size();
	}

	@Override
	public int getColCount() {
		return getDtoAssistant().getColCount();
	}
	
	@Override
	public int getSelectedRowsCount() {
		int result = 0;
		for (var rowRun: rows) {
			if (rowRun.isSelected()) {
				result++;
			}
		}
		return result;
	}

	public void setRowIdxForDetailDisplay(int rowIdxForDetailDisplay) {
		this.rowIdxForDetailDisplay = rowIdxForDetailDisplay;
	}

	@Override
	public int getRowIdxForDetailDisplay() {
		return rowIdxForDetailDisplay;
	}

	public ManagedEntity<T> getManagedEntityForDetailDisplay() {
		if (rowIdxForDetailDisplay >= 0 && rowIdxForDetailDisplay < getRowCount()) {
			return getRow(rowIdxForDetailDisplay);
		} else {
			return null;
		}
	}

	public T getEntityForDetailDisplay() {
		if (rowIdxForDetailDisplay >= 0 && rowIdxForDetailDisplay < getRowCount()) {
			return getData(rowIdxForDetailDisplay);
		} else {
			return null;
		}
	}

	public void notifyRowsInsertedOrDeleted() {
		rowsInsertedOrDeleted = true;
	}

	public boolean isRowsInsertedOrDeleted() {
		return rowsInsertedOrDeleted;
	}

	/**
	 * Returns the last selected row idx.
	 * @return -1 if no row is selected
	 */
	public int getLastSelectedRowIdx() {
		int result = -1;
		int rowCount = rows.size();

		for (int rowIdx = rowCount - 1; rowIdx >= 0; rowIdx--) {
			if (rows.get(rowIdx).isSelected) {
				result = rowIdx;
				break;
			}
		}
		return result;
	}


	/**
	 * Returns the first selected row idx.
	 * @return -1 if no row is selected
	 */
	public int getFirstSelectedRowIdx() {
		int result = -1;
		int rowCount = rows.size();

		for (int rowIdx = 0; rowIdx <= rowCount; rowIdx++) {
			if (rows.get(rowIdx).isSelected) {
				result = rowIdx;
				break;
			}
		}
		return result;
	}
}
