package com.fzenner.datademo.web.inmsg;

public class MsgPowertableAction {   
	public enum SubCommands {
		SAVE_TABLE_DATA, 
		TOGGLE_CHECK_BOX, 
		OPEN_MODAL_DELETE_SELECTED_ROWS, 
		DELETE_SELECTED_ROWS,
		CREATE_NEW_ENTITY_FOR_TABLE_EDIT,
		UPDATE_TABLE_COLUMN_WIDTHS,
		SYNC_CELL_DATA,
		REQUEST_FOCUS_BY_CLICK_ON_TD,
		NOTIFY_FOCUS_RECEIVED_ON_TD,
		ESTABLISH_DATE_TIME_EDITOR,
		NOTIFY_EDITOR_HAS_LOST_FOCUS,
		CALENDAR_POPUP_CREATE,
		CALENDAR_POPUP_UPDATE
	}
	public String msgName;
	public String module;
	public String serverMsgHandler;
	// public String msgType;
	public String pageName;
	public String tableId;
	// public String tdId;
	public String command;
	public String subCommand;
}
