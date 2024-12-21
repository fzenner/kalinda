
/**
 * Indicates a functional (not technical) client-server message.
 */
export const CS_MESSAGE = "CS_MESSAGE"; 
// export const EVENT_HANDLER_ATTR_NAME = "data-eventhandler";  // TODO: depricated, remove
export const CLIENT_EVENT_HANDLER_ATTR_NAME_1 = "data-clienteventhandler_1";
export const CLIENT_EVENT_HANDLER_ATTR_NAME_2 = "data-clienteventhandler_2";
export const CLIENT_EVENT_HANDLER_ATTR_NAME_3 = "data-clienteventhandler_3";
export const CLIENT_EVENT_HANDLER_INSERT_ENTITY_FOR_TABLE_EDIT = "CLIENT_EVENT_HANDLER_INSERT_ENTITY_FOR_TABLE_EDIT";
export const SERVER_MSG_HANDLER_INSERT_ENTITY_FOR_TABLE_EDIT = "SERVER_MSG_HANDLER_INSERT_ENTITY_FOR_TABLE_EDIT";
export const SERVER_MSG_HANDLER_SELECT_TABLE_ROWS = "SERVER_MSG_HANDLER_SELECT_TABLE_ROWS";
export const EVENT_TO_HANDLE_ATTR_NAME_1 = "data-eventtohandle_1";
export const EVENT_TO_HANDLE_ATTR_NAME_2 = "data-eventtohandle_2";
export const EVENT_TO_HANDLE_ATTR_NAME_3 = "data-eventtohandle_3";
// export const CLIENT_EVENT_HANDLER_GENERIC_DATA_SUBMIT = "CLIENT_EVENT_HANDLER_GENERIC_DATA_SUBMIT";
export const CLIENT_EVENT_HANDLER_DELETE_SELECTED_TABLE_ROWS = "CLIENT_EVENT_HANDLER_DELETE_SELECTED_TABLE_ROWS";

export const SERVER_MSG_HANDLER_GENERIC_DATA_SUBMIT = "SERVER_MSG_HANDLER_GENERIC_DATA_SUBMIT";
export const SERVER_MSG_HANDLER_ATTR_NAME = "data-servermsghandler";
export const SERVER_MSG_HANDLER_MODAL_CLOSE = "data-servermsghandlermodalclose";  
export const SERVER_SUB_COMMAND_ATTR_NAME = "data-serversubcmd";
export const SERVER_SIDE_ID_ATTR_NAME = "data-serversideid";
export const HTML_FIELDS_TO_SEND_DTO_ATTR_NAME = "data-htmlfieldstosenddto"
export const FIXED_PARAMS_TO_SEND_DTO_ATTR_NAME = "data-fixedparamstosenddto";
export const SPECIAL_DATA_TO_SEND_DTO_ATTR_NAME = "data-specialdatatosenddto";


export const CMD_GENERATE_CALENDAR = "CMD_GENERATE_CALENDAR";

// export const SERVER_MSG_HANDLER_REMOVE_TAG = "SERVER_MSG_HANDLER_REMOVE_TAG";
// export const SERVER_MSG_HANDLER_SHOW_CALENDAR = "SERVER_MSG_HANDLER_SHOW_CALENDAR";

// export const ATTR_HTML_FIELD_DATA = "htmlFieldData";
// export const ATTR_FIXED_PARAMS = "fixedParams";
// export const ATTR_SPECIAL_DATA = "specialData";


/********************************************************
 * For powerTable
 */
export const ENTITY_STATE_UNMODIFIED = "UNMODIFIED";
export const ENTITY_STATE_NEW = "NEW";
export const ENTITY_STATE_UPDATED = "UPDATED";
export const ENTITY_STATE_DELETED = "DELETED";


export const ROWSTATE_DISPLAY_TEXT_UNMODIFIED = ' ';     // TODO: Move to powerTable.ts
export const ROWSTATE_DISPLAY_TEXT_NEW        = 'N'
export const ROWSTATE_DISPLAY_TEXT_DELETED    = 'D'
export const ROWSTATE_DISPLAY_TEXT_UPDATED    = 'U';

export const TABLE_ID_ATTR_NAME = "data-tableid";

export const TD_ROW_STATE_ATTR_NAME = "data-rowstate";
export const TD_ROW_STATE_INSERTED = 'I';
export const TD_ROW_STATE_UPDATED = 'U';
export const TD_ROW_STATE_DELETED = 'D';
export const TD_ROW_STATE_UNMODIFIED = 'X';

export const CUD_INSERT = 'I';
export const CUD_UPDATE = 'U';
export const CUD_DELETE = 'D';

/******************************************************** */


/********************************************************
 * For modal dialogs
 */



export const CLIENT_EVENT_HANDLER_DELEGATE_ATTR_NAME = "data-clienteventhandlerdelegate";
export const CLIENT_EVENT_HANDLER_DIALOG_ID_ATTR_NAME ='data-dialogtoopen'
export const DO_NOTHING_JUST_CANCEL = "DO_NOTHING_JUST_CANCEL";
// export const CLIENT_EVENT_HANDLER_MODAL_BUTTON_CLICK = "CLIENT_EVENT_HANDLER_MODAL_BUTTON_CLICK";

// export const CLIENT_EVENT_HANDLER_MODAL_CONFIRM = "CLIENT_EVENT_HANDLER_MODAL_CONFIRM";
// export const CLIENT_EVENT_HANDLER_MODAL_CANCEL = "CLIENT_EVENT_HANDLER_MODAL_CANCEL";
export const CLIENT_EVENT_HANDLER_DISPLAY_MODAL_DIALOG = "CLIENT_EVENT_HANDLER_DISPLAY_MODAL_DIALOG";



/******************************************************** */

export type SendInputFieldDataMsg = {
	msgName: string,
	pageName: string,   // The name of the sending page. Necessary for the server to route to the correct handler, since the same service can potentially invoked from multiple pages.
	serviceName: string,
	inputFieldDto: object;
}

/**
 * Client-Server-Message
 */
export type MsgClientAction = {
	msgName: string,   // Technical qualifier.
	module?: string,    // Currently not used. Will enable to provide independent modules.
	serverMsgHandler: string,  // Defines the handler of the module to invoke
	pageName?: string  // The page from which the message was sent.
	command?: string
	subCommand?: string
	param?: string
}


export type MsgInvokeServiceWithParams = MsgClientAction & {
	htmlFieldData?: any
	fixedParams?: any
	specialData?: any
}

export type MsgInvokeServiceWithSimpleParams = MsgClientAction & {
	p1?: string;
	p2?: string;
	p3?: string;
}


export type MsgClientActionAndTag = MsgClientAction & { 
	tagId: string
}

export type MsgFocusEvent = MsgClientAction & { 
	eventName: string
	eventReceiverId: string
	targetId: string
	relatedTargetId: string
}

export type MsgFieldDataEntered = MsgClientAction & {
	tagId: string
	value: string
	clientInputState: String // {EMPTY, INCOMPLETE, UNPARSEABLE, OK};
}

export type MsgDateTimeEntered = MsgClientAction & {
	tagId: string
	clientInputState: String // {EMPTY, INCOMPLETE, UNPARSEABLE, OK};
	value: DateTimeWireOrNull
}

export type DateTimeWire = {
	y: number
	mo: number
	d: number
	h: number
	mi: number
	s: number
	n: number
}

export type DateTimeWireOrNull = {
	value: DateTimeWire;
}

export type DateWire = {
	y: number
	mo: number
	d: number
}

export type TimeWire = {
	h: number
	mi: number
	s: number
	n: number
	wireNull: string
}

export type TimeWireOrNull = {
   value: TimeWire;
}


export type MsgRadioButtonClicked = MsgClientAction & {
	tagId: string
	name: string
	value: string
}


export type MsgPowerTableAction = MsgClientAction & { 
	// Inherited parameter "command" should always be POWERTABLE_COMMAND
	tableId: string
}


export type MsgPowertableAction_SaveCell =  MsgPowerTableAction & {
	rowIdx: number,
	fieldName: string
	value: string
}

export type MsgPowerTableAction_OnRow = MsgPowerTableAction & {
	rowIdx: number;
}

export type MsgPowerTableAction_OnCell = MsgPowerTableAction & {
	tdId: string;
	rowIdx?: number;
	colType?: string;
	symbolColId?: string;
	colIdx?: number;
}

export type MsgPowerTable_CalendarPopupCreate = MsgPowerTableAction_OnCell & {
    year: number;
    month: number;
    day: number;
}

export type MsgCalendarEditorPopupCreate = MsgClientActionAndTag & {
    year: number;
    month: number;
    day: number;
}

export type MsgCalendarEditorPopupChange = MsgClientActionAndTag & {
	currentYear: number;
	setYear: number;
	diffYear: number;
	currentMonth: number;
	diffMonth: number;
}


export type MsgPowerTable_CalendarPopupChange = MsgPowerTableAction_OnCell & {
	currentYear: number;
	setYear: number;
	diffYear: number;
	currentMonth: number;
	diffMonth: number;
}


export type MsgPowerTableAction_NotifyTdFocusEvent = MsgPowerTableAction & {
	eventName: string;
	tdId: string
	eventReceiverId: string;
	targetId: string;
	relatedTargetId: string;
}

export type MsgPowerTableAction_MsgUpdateTableColumnWidth = MsgPowerTableAction & {
	symbolColId: string
	newColWidth: number
}

export type MsgInsertEntityForTableEdit = MsgPowerTableAction & {
	tableId: string;
	tablePosition: number;
}




export type MsgToggleCheckbox = {
	msgName: string;      // Allways CS_MESSAGE
	module: string,    // Currently not used. Will enable to provide independent modules.
	serverMsgHandler: string,  // Defines the handler of the module to invoke
	pageName: string  // The page from which the message was sent.
	tableId: string;
	rowIdx: number;
}


export type MsgOpenModalDialogForTable = {
	msgName: string,      // Allways CS_MESSAGE
	module: string,    // Currently not used. Will enable to provide independent modules.
	serverMsgHandler: string,  // Defines the handler of the module to invoke
	pageName: string  // The page from which the message was sent.
	tableId: string;
}


export type CudAndDto = {
	rowIdx: number
	cudOperation: string,  // One of the constants CRUD_INSERT, CRUD_UPDATE or CRUD_DELETE
	payloadDto: object;
}

