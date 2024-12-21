package com.kewebsi.html.table;

import java.util.ArrayList;

import com.kewebsi.controller.DtoAssistant;
import com.kewebsi.controller.ManagedEntity;
import com.kewebsi.controller.TableCellError;
import com.kewebsi.html.ClientDataOrError;

/**
 * 
 * @author A4328059
 *
 * @param <T> Entity
 */
public interface PowerTableModel<T> extends EntityForDetailDisplayProvider<T> {
	
	public int getRowCount();
	public int getColCount();
	public int getSelectedRowsCount();
	/**
	 * Returns the first selected row idx.
	 * @return -1 if no row is selected
	 */
	public int getFirstSelectedRowIdx();
	/**
	 * Returns the first selected row idx.
	 * @return -1 if no row is selected
	 */
	public int getLastSelectedRowIdx();

	/**
	 * The row for which detail data is displayed somewhere else on the page.
	 * This row is typically focused.
	 * - 1 if no such row exists
	 * @return
	 */
	public int getRowIdxForDetailDisplay();

	public DtoAssistant<T> getDtoAssistant();
	
	public Enum<?>[] getFieldNames();

	// public void setParentLinkAssistant(LinkAssistant parentLinkAssistant);

	// public LinkAssistant getParentLinkAssistant();

	// public Object getParentEntity();

	// public void setParentEntity(Object parentEntity);

	public String getValueForHtml(int row, Enum<?> col);

	public ClientDataOrError getClientDataOrError(int rowIdx, Enum<?> colIn);

	public TableCellError getError(int row, Enum<?> col);

	public TableCellError getErrorCheckingForeignUpdates(int row, Enum<?> col);
	public ArrayList<TableCellError> getErrorsCheckingForeignUpdates();
	public ArrayList<TableCellDisplayError> getDisplayErrorsCheckingForeignUpdates();
	public ArrayList<TableCellDisplayError> getDisplayErrorsCheckingForeignUpdates(int rowIdx);
	
	public ManagedEntity.EntityState getRowState(int row);
	public void setRowIsSelected(int rowIdx, boolean isSelected);
	public void toggleRowIsSelected(int rowIdx);
	
	public boolean getRowIsSelected(int row);
	
	public void insertRowWithNewEntity(int rowIdx, T entity);
	
	public T insertRowWithBlankEntity(int rowIdx);
	
	public void removeRow(int rowIdx);
	
	public void touch();

	public boolean isRowsInsertedOrDeleted();


	public PowerTableModelRow<T> getRow(int rowIdx);
	public ArrayList<PowerTableModelRow<T>> getRows();
	

	
	public boolean isModified();	
	public void setNotModified();
	
}