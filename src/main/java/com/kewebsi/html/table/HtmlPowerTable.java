package com.kewebsi.html.table;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fzenner.datademo.web.inmsg.ClientEventHandlerConsts;
import com.fzenner.datademo.web.inmsg.ServerMessageHandler;
import com.fzenner.datademo.web.outmsg.FieldInfo;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.fzenner.datademo.web.outmsg.table.*;
import com.kewebsi.controller.*;
import com.kewebsi.controller.ManagedEntity.EntityState;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.html.HtmlTag;
import com.kewebsi.util.DebugUtils;
import com.kewebsi.util.EnumUtils;
import com.kewebsi.util.JsonUtils;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static com.kewebsi.html.table.HtmlPowerTable.UpdateCheck.CHECK_FOR_MODIFICATION_BY_CLIENT;

public class HtmlPowerTable<T> extends HtmlTag {

	protected boolean layoutModified;
	
	protected PowerTableModel<T> model;
	
	protected StaticGuiDelegate serverMsgHandler;
	
	private PowerTableModel<T> oldModel;
	
	/*
	 * The text indicators in the HTML table for the status of a table row. 
	 */
	static final String ROWSTATE_DISPLAY_TEXT_UNMODIFIED = " ";
	static final String ROWSTATE_DISPLAY_TEXT_NEW = "N";
	static final String ROWSTATE_DISPLAY_TEXT_DELETED = "D";
	static final String ROWSTATE_DISPLAY_TEXT_UPDATED = "U";
	
	
	/**
	 * Create-update-delete table row operation indicators.
	 */
	static final String TD_ROW_STATE_ATTR_NAME = "data-rowstate";
	static final char TD_ROW_STATE_INSERTED = 'I';
	static final char TD_ROW_STATE_UPDATED = 'U';
	static final char TD_ROW_STATE_DELETED = 'D';
	static final char TD_ROW_STATE_UNMODIFIED = 'X';

	
	/**
	 * The symbolic ID for columns versus a numeric ID based on coordinates
	 */
	static final String SYMBOL_COL_ID_ATTR_NAME = "data-symbolcolid";
	
	static final String TH_ID_INFIX = "thid";
	
	
	protected ArrayList<HtmlTableColumn> columns;

	private static Logger LOG = LoggerFactory.getLogger(HtmlPowerTable.class);
	
	public HtmlPowerTable(PowerTableModel<T> model, String id, StaticGuiDelegate serverMsgHandler) {
		init(model, id, serverMsgHandler);
	}	
	
	public  HtmlPowerTable(PowerTableModel<T> model, Enum<?> id, StaticGuiDelegate serverMsgHandler) {
		init(model, id.name(), serverMsgHandler);
	}	
	


	public void init( PowerTableModel<T> model, String id, StaticGuiDelegate serverMsgHandler) {
		this.model = model;
		this.id = id;
		this.serverMsgHandler = serverMsgHandler;
		initHtmlTableCols();
		// createBody();
	}


	@Override
	public GuiDef getGuiDef() {
		GuiDef guiDef = new GuiDef("powertable", getId());

		TableStructureAndDataForGui tableData = new TableStructureAndDataForGui();
		tableData.headInfo = generateHeadInfo();
		tableData.rowDataAndErrors = generateRows();
		guiDef.state = tableData;
		tableData.detailDisplaysLinked = detailDisplaysAreLinked();
		return guiDef;
	}



	/**
	 * In the future this can be extended with funcionality to hide columns.
	 * @return
	 */
	public Enum<?>[] getFieldNamesToDisplay() {
		return model.getFieldNames();
	}



	protected ArrayList<FieldInfo> generateHeadInfo() {
		ArrayList<FieldInfo> headInfo = new ArrayList<>(model.getColCount());
		var columnHeaders = model.getFieldNames();
		if (columnHeaders != null) {
			for (Enum<?> columnHeaderRun : columnHeaders) {
				FieldAssistant fa = model.getDtoAssistant().getFieldAssistant(columnHeaderRun);
				String name = columnHeaderRun.name();
				String label = fa.getFieldLabel();
				var fieldType = fa.getFieldType();
				var editable = fa.isEditable();
				FieldInfo fieldInfo = new FieldInfo(name, label, fieldType, editable, getColWidth(columnHeaderRun));


				if (fieldType.equals(FieldAssistant.FieldType.ENM)) {
					var faEnum = (FieldAssistantEnum) fa;
					if (faEnum.canBeNull) {
						fieldInfo.addOption("");
					}
					fieldInfo.addOptions(EnumUtils.enumToStringArrayList(faEnum.getEnumClass()));
				}


				headInfo.add(fieldInfo);
			}
		}
		return headInfo;
	}



	protected ArrayList<TableRowDataForGui> generateRows() {
		int rowCount = model.getRowCount();
		ArrayList<TableRowDataForGui> dataAllRows = new ArrayList<>(rowCount);

		ArrayList<PowerTableModelRow<T>> rows = model.getRows();
		var columnHeaders = model.getFieldNames();
		DtoAssistant dtoAssistant = model.getDtoAssistant();

		for (int rowIdx = 0; rowIdx < rowCount; rowIdx++) {
			PowerTableModelRow<T> row = rows.get(rowIdx);
			var tableRowData = new TableRowDataForGui();
			dataAllRows.add(tableRowData);
			ArrayList<String> dataOneRow = new ArrayList<>(dtoAssistant.getColCount());
			tableRowData.state = getRowStateIndicator(row.getEntityState());
			tableRowData.selected = row.isSelected();
			tableRowData.payload = dataOneRow;
			for (var colHeader : columnHeaders) {
				var baseVal = dtoAssistant.getValueAsBaseType(row, colHeader);
				String displayString = baseVal != null ? baseVal.toJsonString() : "";
				dataOneRow.add(displayString);
			}

			tableRowData.errors = model.getDisplayErrorsCheckingForeignUpdates(rowIdx);
		}
		return dataAllRows;
	}


	public static String getRowStateIndicator(EntityState rowState) {
		String result =
		switch (rowState) {
			case UNMODIFIED -> HtmlPowerTable.ROWSTATE_DISPLAY_TEXT_UNMODIFIED;
			case NEW ->        HtmlPowerTable.ROWSTATE_DISPLAY_TEXT_NEW;
			case DELETED ->    HtmlPowerTable.ROWSTATE_DISPLAY_TEXT_DELETED;
			case UPDATED ->    HtmlPowerTable.ROWSTATE_DISPLAY_TEXT_UPDATED;
		};
		return result;
	}


	/**
	 * Returns the list of errors.
	 * @return List of errors. Null or empty list if no errors are present.
	 */
	public ArrayList<TableCellDisplayError> generateErrors() {
		var errors = model.getDisplayErrorsCheckingForeignUpdates();
		return errors;
	}


	public Enum<?> [] getFieldNames() {
		// Note: This method will probably change when we allow column-reodering.
		PowerTableModel<T> model = getModel();
		return model.getFieldNames();
	}
	
	public String getTdId(int rowIdx, int colIdx) {   // TODO: Remove
		return getTableIdWithoutDash() + "-" +    String.valueOf(rowIdx) + "-" + String.valueOf(colIdx);
	}
	


	public static int getRowIdxFromTdId(String tdId) {
		String[] elements = tdId.split("-");
		String rowStr = elements[1];
		int rowInt = Integer.parseInt(elements[1]);
		return rowInt;
	}

	
	public int getColIdx(Enum<?> colName) {
		Enum<?>[] fieldNames = getFieldNames();
		int idx = 0;
		for (var fieldName : fieldNames) {
			if (fieldName == colName) {
				return idx; 
			}
			idx++; 
		}
		throw new CodingErrorException("No column found for Enum " + colName);
	}
	
	
	public String getTableIdWithoutDash() {
		return this.id.replace('-', 'D');
	}
	


	public boolean isEditable(Enum<?> en) {
		return getModel().getDtoAssistant().isEditable(en);
	}
	
	protected static char rowStateTrAttribute(EntityState entityState) {  // TODO: Remove
		char rowStateIndicator;
		
		switch (entityState) {
			case UNMODIFIED: rowStateIndicator = TD_ROW_STATE_UNMODIFIED; break;
			case NEW:        rowStateIndicator = TD_ROW_STATE_INSERTED; break;
			case DELETED:    rowStateIndicator = TD_ROW_STATE_DELETED; break;
			case UPDATED:    rowStateIndicator = TD_ROW_STATE_UPDATED; break;
			default: throw new RuntimeException("Unhandled RowState:" + entityState);
		}
		
		return rowStateIndicator; 
		
		
	}
	
	protected static String checkBoxTdHtml(String tableId, int row) {
		String tdId = tableId + "-tdcb-" + row;
		String checkBoxid =  tableId + "-cb-" + row;
		return "<td id='" + tdId + "'>" + "<input type='checkbox' id='" + checkBoxid + "' name='" + checkBoxid + "' value='X'>" + "<span></td>";
	}
	
	
	public void initHtmlTableCols() {
		PowerTableModel<T> model = getModel();
		columns = new ArrayList<HtmlTableColumn>(model.getColCount());

		var fieldAssistants = model.getDtoAssistant().getFieldAssistants();
		for (var fieldAssistant : fieldAssistants) {
			var newCol = new HtmlTableColumn(fieldAssistant.getFieldName(), fieldAssistant.getDefaultWidthInChar() + "em");
			columns.add(newCol);
		}
	}


	public PowerTableModel<T> getModel() {
		return model;
	}

	
	@Override
	public void setContentOrGuiDefNotModified() {
		setLayoutModified(false);   // We handle layout like content with regards to modification.
		oldModel = getModel();
		getModel().setNotModified();
	}
	
	
	@Override
	public boolean isContentOrGuiDefModified() {

		var currentModel = getModel();

		if (currentModel != oldModel) {
			return true;
		}

		if (getUpdateProcedure() == UpdateProcedure.GUI_DEF)
		if (currentModel != null) {
			if (currentModel.isModified()) {
				return true;
			}
		}


		if (isLayoutModified()) {
			return true;
		}

		return false;

	}

	
	public void setColWidth(Enum<?> en, String colWidth, boolean refreshTable) {
		var col = getCol(en);		
		col.setColWidth(colWidth);
		if (refreshTable) {
			setLayoutModified(true);
		}
	}
	
	public void setColWidth(Enum<?> en, String colWidth)
	{	
		setColWidth(en, colWidth, true);
	}
	
	public void setColWidth(String symbolColId, String newColWidth) {
		Enum<?>[] enums = getModel().getFieldNames();
		Enum<?> foundEnum = EnumUtils.getEnumFromArrByString(enums, symbolColId);
		setColWidth(foundEnum, newColWidth, false);
	}
		
	
	public String getColWidth(Enum<?> en) {
		return getCol(en).getColWidth();		
	}
	
	
	public HtmlTableColumn getCol(Enum<?> colId) {
		for (var run : columns) {
			if (run.fieldName.equals(colId)) {
				return run;
			}
		}
		throw new CodingErrorException("No column found for " + colId.name());
	}
		


	public boolean isLayoutModified() {
		return layoutModified;
	}

	public void setLayoutModified(boolean layoutModified) {
		this.layoutModified = layoutModified;
	}
	
	public void toggleCheckBox(int rowIdx) {
		getModel().toggleRowIsSelected(rowIdx);
	}
	
	public int getSelectedRowsCount() {
		return getModel().getSelectedRowsCount();
	}

	public StaticGuiDelegate getServerMsgHandler() {
		return serverMsgHandler;
	}

	public boolean detailDisplaysAreLinked() {
		return true;
	}

	@Override
	public UpdateProcedure getUpdateProcedure() {
		return UpdateProcedure.GUI_DEF;
	}


	/**
	 * Can return null if there is no update data.
	 * @return
	 */
	public TableDataUpdateForGui getUpdateData() {
		TableDataUpdateForGui tableDataUpdateForGui = null;
		if (model.isRowsInsertedOrDeleted()) {
			// If a row has been inserted or deleted, we update the whole table. Later on we can implement a smarter
			// method generating smaller updates, but then we need have or calculate which rows have been inserted or deleted.
			var rows = generateRows();
			tableDataUpdateForGui = new TableDataUpdateForGui(rows);
		} else {
			var updates = getCellUpdates(CHECK_FOR_MODIFICATION_BY_CLIENT);
			if (updates != null) {
				var cellUpdates = updates.tableCellUpdates;
				var rowStateChanges = updates.rowStateChanges;
				var selectionChanges = updates.rowSelectionChanges;
				tableDataUpdateForGui = new TableDataUpdateForGui(cellUpdates, rowStateChanges, selectionChanges);
			}

		}
		return tableDataUpdateForGui;
	}


	enum UpdateCheck {CHECK_FOR_MODIFICATION_BY_CLIENT, CHECK_FOR_MODIFICATION_BY_BUSINESSLOGIC}

	/**
	 * Returns null if there are no updates.
	 * @param updateCheck
	 * @return
	 */

	protected DetailedChanges getCellUpdates(UpdateCheck updateCheck) {



		// Find updated rows
		ArrayList<TableCellUpdate> cellUpdates = null;
		ArrayList<RowStateChange> rowStateChanges = null;
		ArrayList<RowSelectionChange> rowSelectionChanges = null;

		ArrayList<PowerTableModelRow<T>> rows = model.getRows();
		var columnHeaders = model.getFieldNames();
		DtoAssistant<T> dtoAssistant = model.getDtoAssistant();
		var rowCount = model.getRowCount();

		for (int rowIdx = 0; rowIdx < rowCount; rowIdx++) {
			PowerTableModelRow<T> row = rows.get(rowIdx);

			if (updateCheck == CHECK_FOR_MODIFICATION_BY_CLIENT) {
				if (row.isModified()) {
					var modifiedFields  = row.getModifiedFields();
					if (modifiedFields != null) {
						var fieldUpdateInfos = modifiedFields.values();
						for (var fieldUpdateInfo : fieldUpdateInfos) {
							// String fieldName = modifiedFieldEnum.fieldName().name();
							Enum fieldNameAsEnum = fieldUpdateInfo.fieldName();
							String cellContent = dtoAssistant.getGuiDefValue(row, fieldNameAsEnum);
							String errorString = "";
							if (row.fieldErrors != null) {
								var error = row.fieldErrors.get(fieldUpdateInfo);
								errorString = error != null ? error.getMessage() : null;
							}
							String fieldNameAsString = fieldNameAsEnum.name();
							TableCellUpdate tcu = new TableCellUpdate(rowIdx, fieldNameAsString, cellContent, errorString);
							if (cellUpdates == null) {
								cellUpdates = new ArrayList<>();
							}
							cellUpdates.add(tcu);
						}
					} else {
						if (DebugUtils.DEBUG_CHECKS_ON) {
							LOG.debug("WARN: Unexpected situation: Modified PowerTableModelRow with no modified fields.");
							}
					}

				}

				if (row.stateIsModified()) {
					RowStateChange rowStateChange = new RowStateChange(rowIdx, row.getEntityState().toString());
					if (rowStateChanges == null) {
						rowStateChanges = new ArrayList<>();
					}
					rowStateChanges.add(rowStateChange);
				}

				if (row.selectedIsModified()) {
					RowSelectionChange rowSelectionChange = new RowSelectionChange(rowIdx, row.isSelected());
					if (rowSelectionChanges == null) {
						rowSelectionChanges = new ArrayList<>();
					}
					rowSelectionChanges.add(rowSelectionChange);
				}

			} else {
				throw new NotImplementedException();
			}
		}



		DetailedChanges result = null;
		if (cellUpdates != null  || rowStateChanges != null || rowSelectionChanges != null) {
			result = new DetailedChanges(cellUpdates, rowStateChanges, rowSelectionChanges);
		}

		return result;
	}


}
