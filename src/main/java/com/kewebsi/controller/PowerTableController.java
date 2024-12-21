package com.kewebsi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.StaticContextAccessor;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.inmsg.*;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.fzenner.datademo.web.outmsg.table.TableFocusDef;
import com.kewebsi.errorhandling.ErrorInClientServerLinkingException;
import com.kewebsi.errorhandling.ExpectedClientDataError;
import com.kewebsi.errorhandling.MalformedClientDataException;
import com.kewebsi.html.*;
import com.kewebsi.html.dateeditor.CalendarPopupHelper;
import com.kewebsi.html.popup.HtmlModalDialog;
import com.kewebsi.html.table.*;
import com.kewebsi.service.PageVarEntityProviderBackedDateTime;
import com.kewebsi.service.PowertablePersistService;
import com.kewebsi.util.CommonUtils;
import com.kewebsi.util.JsonUtils;
import org.apache.commons.lang3.NotImplementedException;

public abstract class PowerTableController implements CommandWithParamHandler {

	public static final StaticGuiDelegate handlePowertableAction  = new StaticGuiDelegate(PowerTableController::handlePowertableAction, "handlePowertableAction", GuiCallbackRegistrar.getInstance());
	public static final StaticGuiDelegate handleCommandWithParam  = new StaticGuiDelegate(PowerTableController::handleCommandWithParam, "handleCommandWithParam", GuiCallbackRegistrar.getInstance());


	public static <S extends PageState, T> MsgAjaxResponse handlePowertableAction(JsonNode rootNode , UserSession userSession, HtmlPage page) {
		
		MsgPowertableAction msg = JsonUtils.parseJsonFromClient(rootNode, MsgPowertableAction.class);
		HtmlPowerTable<T> powerTable = (HtmlPowerTable<T>) page.getElementById(msg.tableId);
		
		if (powerTable == null) {
			throw new ErrorInClientServerLinkingException("No html element found for table id: " + msg.tableId);
		}
		
		String subCommandStr = msg.subCommand;
		
		if (!CommonUtils.hasInfo(subCommandStr)) {
			throw new ErrorInClientServerLinkingException("Could not retrieve subCommand from message:" + rootNode.toPrettyString());
		}
	
		MsgPowertableAction.SubCommands subCommand;
		try {
			subCommand = MsgPowertableAction.SubCommands.valueOf(subCommandStr);
		} catch( Exception e) {
			throw new ErrorInClientServerLinkingException("Illegal subcommand:" + subCommandStr);
		}
		
	
		MsgAjaxResponse response =null;;
		
		try {
			switch(subCommand) {
				
				case SYNC_CELL_DATA -> response = syncCellData(page, powerTable, rootNode, userSession);
				case SAVE_TABLE_DATA -> response = saveTableData(powerTable);

				case TOGGLE_CHECK_BOX -> {
					MsgPowertableAction_OnRow msgClicked = JsonUtils.parseJsonFromClient(rootNode, MsgPowertableAction_OnRow.class);
					powerTable.toggleCheckBox(msgClicked.rowIdx);
					response = MsgAjaxResponse.createSuccessMsg();
				}

				case OPEN_MODAL_DELETE_SELECTED_ROWS -> response = openModalDeleteSelectedRows(powerTable);
				case DELETE_SELECTED_ROWS -> response = PowertablePersistService.deleteSelectedRows(powerTable);
				case UPDATE_TABLE_COLUMN_WIDTHS -> response = changeTableLayout(rootNode, userSession, page);
				case CREATE_NEW_ENTITY_FOR_TABLE_EDIT -> response = createEntityForTableEdit(rootNode, userSession, page);
				case REQUEST_FOCUS_BY_CLICK_ON_TD -> response = handleRequestFocusByClickOnTd(rootNode, userSession, page);
				case NOTIFY_FOCUS_RECEIVED_ON_TD -> response = handleFocusReceivedOnTd(rootNode, userSession, page);
				case CALENDAR_POPUP_CREATE -> response = createCalendarPopup(rootNode);
				case CALENDAR_POPUP_UPDATE -> response = CalendarPopupHelper.updateCalendarPopup(rootNode);

			}
		
		} catch(Exception e) {
			throw new MalformedClientDataException(e);
		}
		
		return response;
	}



	



	public static <S extends PageState, T> MsgAjaxResponse syncCellData(HtmlPage page, HtmlPowerTable<T> powerTable, JsonNode rootNode, UserSession userSession) throws JsonProcessingException {
		MsgPowerTableAction_SaveCell msg = JsonUtils.parseJson(rootNode, MsgPowerTableAction_SaveCell.class);
		var tableModel = powerTable.getModel();
		var dtoAssistant = tableModel.getDtoAssistant();
		var tableModelRow = tableModel.getRow(msg.rowIdx);
		T entity = tableModelRow.getEntity();
		
		Enum fieldName = dtoAssistant.getFieldEnum(msg.fieldName);

		String oldStringValue = dtoAssistant.getHtmlValue(entity, fieldName);
		String newStringValue = msg.value;
		
		if (! oldStringValue.equals(newStringValue)) {

			try {
				dtoAssistant.setValueStr(fieldName, msg.value, tableModelRow, FieldUpdateInfo.UpdateDirection.FRONT_TO_BACK);
				tableModelRow.clearError(fieldName);  // Clear old error if exists.
			} catch (ExpectedClientDataError ex) {
				BaseVal currentValue = dtoAssistant.getValueAsBaseType(entity, fieldName);
				TableCellError tce = new TableCellError(entity, fieldName, currentValue, newStringValue, ex.getMessage());

				tableModelRow.putError(fieldName, tce);
				return MsgAjaxResponse.createErrorMsg(ex.getMessage());
			}

			if (tableModelRow.getEntityState().equals(ManagedEntity.EntityState.UNMODIFIED)) {
				tableModelRow.setEntityState(ManagedEntity.EntityState.UPDATED);
			}
		}
		return MsgAjaxResponse.createSuccessMsg();
	}
	
	
	public static <T> MsgAjaxResponse saveTableData(HtmlPowerTable<T> powerTable) {
		var tableModel = powerTable.getModel();
		PowertablePersistService ptps = StaticContextAccessor.getBean(PowertablePersistService.class);
		try {
			ptps.persist(tableModel);
		} catch (ExpectedClientDataError error) {
			return MsgAjaxResponse.createErrorMsg(error.getMessage());
		}
		return MsgAjaxResponse.createSuccessMsg();
	}
	
	
	public static MsgAjaxResponse createEntityForTableEdit(JsonNode rootNode , UserSession userSession, HtmlPage page) {
		
		MsgInsertEntityForTableEdit msg;
		try {
			msg = JsonUtils.parseJson(rootNode, MsgInsertEntityForTableEdit.class);
		} catch(Exception e) {
			throw new MalformedClientDataException(e);
		}
		var pageName = msg.pageName;
		var tableId = msg.tableId;
		var powerTable = (HtmlPowerTable<?>) page.getDescendent(tableId);
		var tablePosition = msg.tablePosition;

		return createEntityForTableEditCore(powerTable, tablePosition);
	}

	public static MsgAjaxResponse createEntityForTableEditCore(HtmlPowerTable<?> powerTable, int tablePosition) {
		var model = powerTable.getModel();

		if (model instanceof ChildTableModel<?>) {
			ChildTableModel cmodel = (ChildTableModel) model;
			LinkAssistant pla = cmodel.getParentLinkAssistant();
			if (pla!=null) {
				if (pla instanceof ManyToOneLinkAssistant) {
					ManyToOneLinkAssistant m2mpla = (ManyToOneLinkAssistant) pla;
					// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
				}


			}

		}




		model.insertRowWithBlankEntity(tablePosition);
		String cellId = powerTable.getTdId(tablePosition, 0);

		var fieldNames = powerTable.getFieldNames();
		var firstFieldName = fieldNames[0];

		var focusDef = new TableFocusDef(powerTable, tablePosition,  firstFieldName.name());



		// var successMsg = MsgAjaxResponse.createSuccessMsg().setHtmlTagToFocus(cellId);
		var successMsg = MsgAjaxResponse.createSuccessMsg().setFocusDef(focusDef);
		return successMsg;
	}


//	public static MsgAjaxResponse createChildEntityForTableEditCore(HtmlPowerTable<?> powerTable, int tablePosition) {
//		var model = powerTable.getModel();
//		var newEntity = model.insertRowWithBlankEntity(tablePosition);
//		String cellId = powerTable.getTdId(tablePosition, 0);
//		var successMsg = MsgAjaxResponse.createSuccessMsg().setHtmlTagToFocus(cellId);
//		return successMsg;
//	}

	public static MsgAjaxResponse handleRequestFocusByClickOnTd(JsonNode rootNode , UserSession userSession, HtmlPage page) {
		MsgPowerTableAction_OnCell msg = JsonUtils.parseJsonFromClient(rootNode, MsgPowerTableAction_OnCell.class);
		var tableId = msg.tableId;
		var powerTable = (HtmlPowerTable<?>) page.getDescendent(tableId);
		PowerTableModelImpl model = (PowerTableModelImpl) powerTable.getModel();

		model.setRowIdxForDetailDisplay(msg.rowIdx);
		var msgOut = MsgAjaxResponse.createSuccessMsg();
		///msgOut.setHtmlTagToFocus(msg.tdId);
		return msgOut;
	}


	public static MsgAjaxResponse handleFocusReceivedOnTd(JsonNode rootNode , UserSession userSession, HtmlPage page) {
		MsgPowerTableAction_OnCell msg = JsonUtils.parseJsonFromClient(rootNode, MsgPowerTableAction_OnCell.class);
		var tableId = msg.tableId;
		var powerTable = (HtmlPowerTable<?>) page.getDescendent(tableId);
		PowerTableModelImpl model = (PowerTableModelImpl) powerTable.getModel();

		model.setRowIdxForDetailDisplay(msg.rowIdx);
		var msgOut = MsgAjaxResponse.createSuccessMsg();
		return msgOut;
	}



	public static MsgAjaxResponse openModalDeleteSelectedRows(HtmlPowerTable<?> table) {
		var dataDemoBody =  table.getBody();

		var guiCallback = new GuiAction() {
			@Override
			public MsgAjaxResponse execute() {
				return PowertablePersistService.deleteSelectedRows(table);
			}
		};


		int nbrOfSelectedRows = table.getSelectedRowsCount();
		DtoAssistant<?> assist = table.getModel().getDtoAssistant();
		String entityName = nbrOfSelectedRows == 1 ? assist.getEntityNameSingular() : assist.getEntityNamePlural();
		var dialogText = String.format("Delete %d %s?", nbrOfSelectedRows, entityName);



		var modalDialog = new HtmlModalDialog("modaldialog", dialogText, "Ok", guiCallback);
		table.getBody().addChild(modalDialog);
		return MsgAjaxResponse.createSuccessMsg();
	}



	public static MsgAjaxResponse changeTableLayout(JsonNode rootNode, UserSession userSession, HtmlPage page) throws JsonProcessingException {
		MsgUpdateTableColumnWidth msgUpdateColWidth = JsonUtils.parseJson(rootNode, MsgUpdateTableColumnWidth.class);
		
		HtmlTag powerTableAsTag = page.getDescendent(msgUpdateColWidth.tableId);
		HtmlPowerTable<?> powerTable = (HtmlPowerTable<?>) powerTableAsTag;
		
		String symbolColId = msgUpdateColWidth.symbolColId;
		
		String newColWidthStr = msgUpdateColWidth.newColWidth.strip().toLowerCase();
		
		powerTable.setColWidth(symbolColId, newColWidthStr);
		return MsgAjaxResponse.createSuccessMsg();
		
	}


	public static <S extends PageState, T> MsgAjaxResponse handleCommandWithParam(JsonNode rootNode , UserSession userSession, HtmlPage page) {
		MsgClientActionAndTag msg = JsonUtils.parseJsonFromClient(rootNode, MsgClientActionAndTag.class);
		HtmlTag target = page.getDescendent(msg.tagId);


		HtmlPowerTable table = (HtmlPowerTable) target;
		var commandStr = msg.command;
		var param = msg.param;


		if (!CommonUtils.hasInfo(commandStr)) {
			throw new ErrorInClientServerLinkingException("Could not retrieve command from message:" + rootNode.toPrettyString());
		}

		MsgPowertableAction.SubCommands subCommandEnum;
		try {
			subCommandEnum = MsgPowertableAction.SubCommands.valueOf(commandStr);
		} catch( Exception e) {
			throw new ErrorInClientServerLinkingException("Illegal subcommand:" + commandStr);
		}


		MsgAjaxResponse response;
		switch(subCommandEnum) {
			case DELETE_SELECTED_ROWS: response = PowertablePersistService.deleteSelectedRows(table); break;
			default: throw new NotImplementedException("Command not implemented:" + commandStr);
		};
		return response;

	}


	public static MsgAjaxResponse createCalendarPopup(JsonNode rootNode ) {
		MsgPowerTable_CalendarPopupCreate msg = JsonUtils.parseJsonFromClient(rootNode, MsgPowerTable_CalendarPopupCreate.class);

		int year = msg.year;
		int month = msg.month;

		var guiDef = CalendarPopupHelper.getCalendarGuiDef(year, month);
		var result = MsgAjaxResponse.createSuccessMsg();
		result.setCustomCallbackData(guiDef);

		return result;
	}


}
