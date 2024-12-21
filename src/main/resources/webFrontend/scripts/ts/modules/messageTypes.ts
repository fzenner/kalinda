import { LocalTime } from "./dateTime";
import { DateTimeWire, DateTimeWireOrNull, TimeWire, TimeWireOrNull } from "./jointTypes";

export type MsgAjaxResponse = { 
	msgName: string,
	htmlUpdates: MsgClientUpdate[];
	// clientEventHandlers: ClientEventHandler[]
	modalDialogToShow: ModalStandardDialogDef;
	// htmlTagToFocus: string;
	focusDef: FocusDef
	showPopupUnderTag: string;
	nonTransparentTag: string;
	editPageId: number;

}

export type FocusDef = {
	focusFunction: string;
	tagIdToFocus: string;
}


export type TableFocusDef = FocusDef & {
	fieldName: string
    rowIdx: number
}

export type  InputFieldModifications = {
	id: string
	visibility: string
	errorMsg: string
	value: string
}

export type MsgClientUpdate = {
	msgName: string
	updateOperation: string
	idToUpdateOrParent: string
	newHtmlCode: string
	newHtmlNode: any
	guiDef: GuiDef
	attributeKey: string
	attributeValue: string
	visibilityOrDisplay: string
	
	// inputFieldModifications: InputFieldModifications

	clientElementData: any;
	ps0: string
	ps1: string
	ps2: string
	ps3: string
	ps4: string
	ps5: string
	ps6: string
	ps7: string
	ps8: string
	ps9: string
}


export type GuiDef = {
    tag: string
    id: string

	/**
	 * Possible values: {REPLACE, FULL, MODIFICATIONS_ONLY };
	 */
	updateMode: string 
    text: string
    cssClasses: string[] 
	attrs: object  // Java: LinkedHashMap<String, String> attrs;
	state: object  // For complex objects like tables.
	errorInfo: ErrorInfo
	tagSpecificData: any
	visibility: string // Options: {VISIBILITY_HIDDEN, VISIBILITY_VISIBLE, DISPLAY_NONE, DISPLAY_BLOCK}
    children: GuiDef[]
    serverCallbackId: string
	clientEventHandler: string
	customUpdateOperation: string
	relatedHtmlElementId: string
    relatedPlacing: string
}

export type InputFieldGuiDef = {
	typeOfValue: String
	size: number
	name: string
	placeholder: string
	value: string
	required: boolean
	disabled: boolean,
}

export type CheckBoxGuiDef = {
	value: boolean
	disabled: boolean
}

export type WireNull = {
	isNull: boolean
}

export type DateTimeFieldGuiDef = {
	dateTimeWireOrNull: DateTimeWireOrNull
	required : boolean
	disabled: boolean
	defaultTimeWireOrNull: TimeWireOrNull;
}

export type ErrorInfo = {
	wireNull: string
	errorText: string
	errorCode: string
}

export type ModalStandardDialogDef = GuiDef & {
    mainText: string
    cancelButton: GuiDef
    confirmButton: GuiDef
    cancelXButton: GuiDef
}


export type PlacedPopupDef = GuiDef & {
	relatedHtmlElementId: string;
}


export type CalendarGuiDef = {
    month: number;
    year: number;
    dayToFocus: number;
	days: CalendarGuiDay[][];
}

export type CalendarGuiDay = {
    year: number;
    month: number;
    day: number;
}


export const MSG_HANDLER_HANDLE_POWERTABLE_ACTION = "handlePowertableAction";

export const UPDATE_OPERATION_NEW = "NEW";
export const UPDATE_OPERATION_MODIFIED = "MODIFIED";
export const UPDATE_OPERATION_REMOVED = "REMOVED";

export const UPDATE_OPERATION_ATTRIBUTE_NEW = "ATTRIBUTE_NEW";
export const UPDATE_OPERATION_ATTRIBUTE_MODIFIED = "ATTRIBUTE_MODIFIED";
export const UPDATE_OPERATION_ATTRIBUTE_REMOVED = "ATTRIBUTE_REMOVED";
// const INVOKE_CLIENT_CALLBACK = "INVOKE_CLIENT_CALLBACK";
export const UPDATE_OPERATION_CSS_CLASSES_CHANGED = "CSS_CLASSES_CHANGED";
export const UPDATE_OPERATION_VISIBILITY_OR_DISPLAY_CHANGED = "VISIBILITY_OR_DISPLAY_CHANGED";
export const UPDATE_BY_DEF          = "UPDATE_BY_DEF";
export const UPDATE_BY_DEF_NEW      = "UPDATE_BY_DEF_NEW";
export const UPDATE_BY_DEF_REMOVED  = "UPDATE_BY_DEF_REMOVED";
export const UPDATE_BY_DEF_MODIFIED = "UPDATE_BY_DEF_MODIFIED";
export const UPDATE_BY_DEF_CUSTOM   = "UPDATE_BY_DEF_CUSTOM";
