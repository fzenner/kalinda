
import { createLocalDateTime, LocalDateTime } from "./dateTime.js";
import { StandardStringEditorCompanion, TableEditorCompanion } from "./tableEditor.js";
import { replaceElementByHtmlString, replaceElementByIdAndHtmlString } from "./kewebsiPageComposer.js";
import {isFocused, getPageName, getBooleanAttribute, modalWindowIsShown, setBooleanAttribute, setSmartFocus} from "./kewebsiUtils.js";
import {SessionHandling, getServerMsgHandler, findFirstChildWithAttribute, displayErrorAsModalWindow, warn} from "./jaccessEventHandling.js";
import {GuiDef, MSG_HANDLER_HANDLE_POWERTABLE_ACTION, UPDATE_BY_DEF_CUSTOM, TableFocusDef, CalendarGuiDef} from "./messageTypes.js";
import * as jointTypes from"./jointTypes.js";
import {createTableDateTimeEditorWithCompanion, setTdBaseData_string, setTdBaseData_LocalDateTime, TableDateTimeEditorCompanion} from "./TableDateTimeEditorCompanion.js";

import {printDateDDMMYYYY, printTime24h } from "./stringUtils.js";

import {constructRowStateSpanId, constructSelectTdId, constructSelectCheckboxId, constructTrId, constructRowStateTdId, constructStrippedId,
	constructPayloadTdIdFast, setStrippedTableId,
	constructPayloadTdId, getIndexOfFieldInFieldInfo, getRowIdxFromTr, parseTdCoordFromTd, parseTdCoordFromTdId, getFieldInfo, getFieldInfoFromTd,
	getTableFromChildElement, getTrFromChildElement, getTdFromChildElement, getHeadInfo, getPowertabeFromChildElement as getPowertableFromChildElement, getPowerTable
} from "./powerTableNavigation.js"

import { isParsingError, parseGermanDate, parseIsoDateTime, parseTime } from "./kewebsiDateUtils.js";

import { Companion, getCompanion} from "./Companion.js";
import { StyleManager } from "./StyleManager.js";

const TAB_EDIT_FIELD_ID = "tabEditField";  // The ID of the edit (input) field within a table cell. The editor always gets the same ID.
const TD_HAS_INPUT_FIELD_CHILD = "data-td-has-input-field-child";

export const TD_MAP_TARGET_ATTR_NAME = "data-maptarget";
const TABLE_ROWCOUNT_ATTR_NAME = "data-rowcount";


export const COLTYPE_ATTR_NAME = "data-coltype";  // One of COLTYPE_STATE, COLTYPE_SELECTOR, COLTYPE_PAYLOAD
export const ROW_IDX_ATTR_NAME = "data-rowidx";
export const COL_IDX_ATTR_NAME = "data-colidx";
const COLTYPE_STATE = "state";
const COLTYPE_SELECTOR = "selector";
const COLTYPE_PAYLOAD = "payload";
const COL_PAYLOAD_TYPE = "data-payloadtype";

export const POWERTABLE_COMMAND = "POWERTABLE_COMMAND";

const DETAIL_DISPLAYS_LINKED = "data-detail-displays-linked";

//
// SubCommands for POWERTABLE_COMMAND 
//
const SAVE_TABLE_DATA = "SAVE_TABLE_DATA";
const SYNC_CELL_DATA = "SYNC_CELL_DATA";
const TOGGLE_CHECK_BOX = "TOGGLE_CHECK_BOX";
const OPEN_MODAL_DELETE_SELECTED_ROWS = "OPEN_MODAL_DELETE_SELECTED_ROWS";
const DELETE_SELECTED_ROWS = "DELETE_SELECTED_ROWS";
const REQUEST_FOCUS_BY_CLICK_ON_TD = "REQUEST_FOCUS_BY_CLICK_ON_TD";
const NOTIFY_FOCUS_RECEIVED_ON_TD = "NOTIFY_FOCUS_RECEIVED_ON_TD";
const CREATE_NEW_ENTITY_FOR_TABLE_EDIT = "CREATE_NEW_ENTITY_FOR_TABLE_EDIT";
const UPDATE_TABLE_COLUMN_WIDTHS = "UPDATE_TABLE_COLUMN_WIDTHS";
const ESTABLISH_DATE_TIME_EDITOR = "ESTABLISH_DATE_TIME_EDITOR";
export const REMOVE_MODAL = "REMOVE_MODAL";


const PAGE_NAME_ATTR_NAME = "data-pagename";


const SYMBOL_COL_ID_ATTR_NAME = "data-symbolcolid"; 
const TH_ID_WITH_TYPE_INFO = "data-th-id-with-type-info";
const SELECTION_VALUES_ATTR_NAME = "data-selection-values";



/**
 * Coordinates of a table data field
 */
 export type TdCoord = {
	// tableId: string,
	row: number,
	col: number
}

type TablePropsAndState = {
    headInfo : FieldInfo[];
	rowDataAndErrors: TableRowDataForGui[];
	detailDisplaysAreLinked: boolean;
}

//  type TableRowDataAndErrors = {
//     rows: TableRowDataForGui[];
//     tableCellDisplayErrorsErrors : TableCellDisplayError[];
// }

export type FieldInfo = {
    symbolColId: string
    label: string
    dataType: string  // Possible values as defined in Java: public enum FieldType {STR, ENM, INT, LONG, FLOAT, LOCALDATETIME, LOCALDATE, LOCALTIME};
	colWidth: string
	editable: boolean
	options: string[]
}

/**
 * This type is used when 
 */
type TableDataUpdateForGui = {

	/**
	 * Is either ALL_DATA or PARTIAL_DATA. It is redundant, since the scope can be derived from which fields are filled. 
	 * But it makes things clearer.
	 */
	updateScope: String    
    /**
     * Populated (non-null), when ALL rows are updated.
     */
    completeTableDataForGui: TableRowDataForGui[];


    /**
     * Populated (non-null), when single cells are updated.
     */
    cellUpdates: TableCellUpdate[]

    /**
     * Populated (non-null), when single cells are updated.
     */
    rowStateChanges: RowStateChange[]
	rowSelectionChanges: RowSelectionChange[]

}

/**
 * DTO class to update a table completely. No partial updates.
 */
// type CompleteTableDataForGui = {
//     rows: TableRowDataForGui[]
//     tableCellErrors: TableCellDisplayError[]
// }

type TableRowDataForGui = {
    state: string;
	selected: boolean;
    payload: string[];
	errors?: TableCellDisplayError[];
}

type TableCellDisplayError = {
    rowIdx: number
    fieldName: string
    errorMsg: string
}


type TableCellUpdate = {
    rowIdx: number
    fieldName: string
    value: string
    errorMsg?: string
}


type RowStateChange = {
	rowIdx: number
	newState: string
}

type RowSelectionChange = {
	rowIdx: number
	isSelected: string
}

export function tdHandleMouseDown(eventOnTd: MouseEvent) { 

	let editorEstablished = false;
	if (eventOnTd.button == 0 ) {  // Button 0 is the left (main) mouse button
		let eventTarget = eventOnTd.currentTarget as HTMLElement;
		let powerTable = getPowertableFromChildElement(eventTarget);
		let detailDisplaysLinked = getBooleanAttribute(powerTable.root, DETAIL_DISPLAYS_LINKED);

		
		if (detailDisplaysLinked) {
			if (isFocused(eventTarget)) {
				editorEstablished = powerTable.establishEditorOnTdIfCellFocused(eventOnTd);
			} else {
				// We need to validate the data in the linked editor and then transfer focus
				// XXXXXXXXXXXXXXXXXX eventOnTd.preventDefault();
				// XXXXXXXXXXXXXXXXXX requestFocusOnCellOnServer(eventTarget);
			}
		} else { 
			editorEstablished = powerTable.establishEditorOnTdIfCellFocused(eventOnTd);
		}


		// This should not be necessary, but for some reason the focus style is not set if we do not explicitley focus here.
		if (! editorEstablished) {
			eventTarget.focus();
		}
	}

	
}


export function tdHandleKeyDown(eventOnTd: KeyboardEvent) {
	console.log("tdHandleKeyDown entered")

	let eventTarget:EventTarget = eventOnTd.target;
	let eventReceiver = eventOnTd.currentTarget;
	let tdElement : HTMLElement = eventTarget as HTMLElement;

	let table = getTableFromChildElement(eventTarget as HTMLElement);
	const powerTable = getPowerTable(table)
	// const shadowRoot = powerTable.shadowRoot;
	// let strippedTableId = constructStrippedId(table.id);

	// TODO: Make clear that wo ignore bubbled up events from the editor. (Use target in the names, not td.)
	// if (tdElement.id !== TAB_EDIT_FIELD_ID) {  // It is really weird that we get events from this child. (Events from editor bubble up.) But that's what happens.

	// Only events received by the TD are processed.
	if (eventReceiver !== eventTarget) { 
		return
	}
	if (eventOnTd.key==="F2" || eventOnTd.key==="Enter") {
		let newInputEl :HTMLElement  = establishCellEditorOnKeyboardEvent(eventOnTd);
		console.log("F2 pressed.")

		if (newInputEl.tagName === "SELECT") {
			let selectBox = newInputEl as HTMLSelectElement;
			selectBox.value = inputFieldContent;
		} else {
			if (newInputEl.tagName === "INPUT") {
				let inputField = newInputEl as HTMLInputElement;
				var inputFieldContent = inputField.value;
				inputField.value = inputFieldContent;
				inputField.setSelectionRange(0, inputFieldContent.length);
			} else {
				warn("Error in tdHandleKeyDown: Unhanlded input field type: " + newInputEl.tagName);
			}
		}
	} else if (eventOnTd.key == "ArrowUp") {
		let eventTarget:EventTarget = eventOnTd.target;
		let tdElement : HTMLElement = eventTarget as HTMLElement;
		let tdCoord = parseTdCoordFromTdId(tdElement.id);
		if (tdCoord.row > 0)  {
			let newRow = tdCoord.row-1;
			let newTdId = constructPayloadTdId(table, newRow, tdCoord.col);
			focusTdId(newTdId);
			eventOnTd.preventDefault();
		}
	} else if (eventOnTd.key == "ArrowDown") {
		let eventTarget:EventTarget = eventOnTd.target;
		let tdElement : HTMLElement = eventTarget as HTMLElement;
		let tdCoord = parseTdCoordFromTdId(tdElement.id);
		let newRow = tdCoord.row+1;
		let newTdId = constructPayloadTdId(table, newRow, tdCoord.col);
		let elementToFocus = document.getElementById(newTdId);
		if (elementToFocus !== null) {
			focusTd(elementToFocus);
		}
		eventOnTd.preventDefault();
	} else if (eventOnTd.key == "ArrowLeft") {
		let eventTarget:EventTarget = eventOnTd.target;
		let tdElement : HTMLElement = eventTarget as HTMLElement;
		let tdCoord = parseTdCoordFromTdId(tdElement.id);
		if (tdCoord.col > 0)  {
			let newCol = tdCoord.col-1;
			let newTdId = constructPayloadTdId(table, tdCoord.row, newCol);
			focusTdId(newTdId);
		}
		eventOnTd.preventDefault();
	} else if (eventOnTd.key == "ArrowRight") {
		let eventTarget:EventTarget = eventOnTd.target;
		let tdElement : HTMLElement = eventTarget as HTMLElement;
		let tdCoord = parseTdCoordFromTdId(tdElement.id);
		let newCol = tdCoord.col+1;
		let newTdId = constructPayloadTdId(table, tdCoord.row, newCol);
		let elementToFocus = document.getElementById(newTdId);
		if (elementToFocus !== null) {
			focusTd(elementToFocus);
		}
		eventOnTd.preventDefault();
	
	} else if (eventOnTd.key == "Shift" || eventOnTd.key == "Control" || eventOnTd.key == "Alt" || eventOnTd.key == "Shift" || eventOnTd.key == "Meta" || eventOnTd.key == "Escape") {
		// Do nothing, we skip the control keys.
	} else if (eventOnTd.key == "Tab") {
		// Do nothing, we skip the tab key.
	} else if (!(eventOnTd.ctrlKey || eventOnTd.metaKey )) {
		// We expect printable characters to be handled here.
		let eventTarget:EventTarget = eventOnTd.target;
		let tdElement : HTMLElement = eventTarget as HTMLElement;

		// if (tdElement === document.activeElement)  {  // Only create cell editor, if we have the focus. Otherwise we assume the editor already exists.
		const activeElementInWebComp = powerTable.getActiveElement()
		if (tdElement === activeElementInWebComp)  {  // Only create cell editor, if we have the focus. Otherwise we assume the editor already exists.
			let newInputEl :HTMLElement  = establishCellEditorOnKeyboardEvent(eventOnTd);
			if (newInputEl !== null) {
				if (newInputEl.tagName === "INPUT") {
					let inputField = newInputEl as HTMLInputElement;
					inputField.value = ""
				}
				let eventCopy = new KeyboardEvent(eventOnTd.type, {key: eventOnTd.key, bubbles: false});
				newInputEl.dispatchEvent(eventCopy);
			}
		}
	} 
}

 
export function tdHandleFocusReceived(eventOnTd: FocusEvent) {

	let eventTarget:EventTarget = eventOnTd.target;
	let tdElement : HTMLElement = eventTarget as HTMLElement;

	console.count("Focus on td received: " +  tdElement.id);

	let powerTable = getPowertableFromChildElement(tdElement);
	let serverMsgHandler = MSG_HANDLER_HANDLE_POWERTABLE_ACTION;
	let pageName = getPageName(powerTable);


	let colType = tdElement.getAttribute(COLTYPE_ATTR_NAME);
	let rowIdx = Number(tdElement.getAttribute(ROW_IDX_ATTR_NAME));


	let msg: jointTypes.MsgPowerTableAction_OnCell = {
		msgName: jointTypes.CS_MESSAGE, 
		module: null,
		serverMsgHandler: serverMsgHandler,
		pageName: pageName,
		command: POWERTABLE_COMMAND,
		subCommand: NOTIFY_FOCUS_RECEIVED_ON_TD,
		tableId: powerTable.id,
		tdId: tdElement.id,
		colType: colType,
		rowIdx: rowIdx
	}

	if (colType === COLTYPE_PAYLOAD) {
		let colIdx = Number(tdElement.getAttribute(COL_IDX_ATTR_NAME));
		msg.colIdx = colIdx;
	}


	SessionHandling.ajaxCallStandard(msg);	

}

export function tdHandleFocusLost(eventOnTd: FocusEvent) {
	const tdTarget = eventOnTd.target as HTMLTableCellElement;
	console.count("Focus on td lost: " +  tdTarget.id);
}


function focusLossIsPermanent(focusEvent: FocusEvent) {

	let eventReceiver = focusEvent.currentTarget as HTMLElement;
	let focusReceiver = focusEvent.relatedTarget as HTMLElement;

	let eventReceiverHasLostFocus = false;
	if (focusReceiver != null) {
		if (!eventReceiver.contains(focusReceiver)) {
			if (!(eventReceiver == focusReceiver)) {
				eventReceiverHasLostFocus = true;
			}
		}
	} else {
		if (!modalWindowIsShown()) {
			eventReceiverHasLostFocus = true;
		}
	}
	return eventReceiverHasLostFocus;
}

// In V2 we do consider any focus loss without focusReceiver as temporary.
function focusLossIsPermanentV2(focusLossEvent: FocusEvent) {


	let focusLoser = focusLossEvent.currentTarget as HTMLElement;
	let focusReceiver = focusLossEvent.relatedTarget as HTMLElement;
 
	let eventReceiverHasLostFocus = false;
	if (focusReceiver != null) {
		console.log("PPPP");
		if (!focusLoser.contains(focusReceiver)) {
			console.log("QQQQ");
			if (!(focusLoser == focusReceiver)) {
				console.log("RRRR");
				eventReceiverHasLostFocus = true;  // The focusReceiver exists and the focus is not a child of the focusLoser (nor itself, which should not be possible)
			}
		}
	} else {
		console.log("SSSS");
		if (!modalWindowIsShown()) {
			console.log("EEEEE");
			eventReceiverHasLostFocus = true;
		} else {
			eventReceiverHasLostFocus = false;
		}
	}
	return eventReceiverHasLostFocus;
}









 

function focusTdId(tdId: string) {
	let element = document.getElementById(tdId);
	focusTd(element);
}

function focusTd(tdElement : HTMLElement) {
	tdElement.focus();
	rememberLastFocusedCell(tdElement);
}

function requestFocusOnCellOnServer(tdElement : HTMLElement) {
	let coords = parseTdCoordFromTdId(tdElement.id);

	let powerTable = getPowertableFromChildElement(tdElement);
	let serverMsgHandler = MSG_HANDLER_HANDLE_POWERTABLE_ACTION;
	let pageName = getPageName(powerTable);


	let colType = tdElement.getAttribute(COLTYPE_ATTR_NAME);
	let rowIdx = Number(tdElement.getAttribute(ROW_IDX_ATTR_NAME));
	let colIdx = Number(tdElement.getAttribute(COL_IDX_ATTR_NAME));


	let msg: jointTypes.MsgPowerTableAction_OnCell = {
		msgName: jointTypes.CS_MESSAGE, 
		module: null,
		serverMsgHandler: serverMsgHandler,
		pageName: pageName,
		command: POWERTABLE_COMMAND,
		subCommand: REQUEST_FOCUS_BY_CLICK_ON_TD,
		tableId: powerTable.id,
		tdId: tdElement.id,
		rowIdx: rowIdx,
		colIdx: colIdx
	}

	if (colType === COLTYPE_PAYLOAD) {
		let colIdx = Number(tdElement.getAttribute(COL_IDX_ATTR_NAME));
		msg.colIdx = colIdx;
	}


	SessionHandling.ajaxCallStandard(msg);	
}


function rememberLastFocusedCell(tdElement : HTMLElement) {
	if (tdElement !== null) {
		let tdCoord = parseTdCoordFromTd(tdElement);
		let table = getTableFromChildElement(tdElement);
		table.setAttribute("data-lastfocusedcol", tdCoord.col.toString());
		table.setAttribute("data-lastfocusedrow", tdCoord.row.toString());
	}
}

function clearLastFocusedCell(tableChild : HTMLTableElement) {
	let table = getTableFromChildElement(tableChild);
	table.removeAttribute("data-lastfocusedcol");
	table.removeAttribute("data-lastfocusedrow");

}

function establishCellEditorOnKeyboardEvent(eventOnTd: KeyboardEvent) : HTMLElement {
	let tdElement:EventTarget = eventOnTd.target;
	return establishCellEditorCore(tdElement as HTMLTableCellElement);
}


function establishCellEditorOnClick(eventOnTd: MouseEvent) : HTMLElement {
	let tdElement:EventTarget = eventOnTd.currentTarget; // We handle here also clicks from descendents of a TD, especially the SPAN (We must ignore clicks in an editor, which should be stopped from propagation)
	return establishCellEditorCore(tdElement as HTMLTableCellElement);
}

/**
 * Establishes a cell editor for the given table data element, if it is editable
 * @param tdElement 
 * @returns The cell editor or null.
 */
function establishCellEditorCore(tdElement: HTMLTableCellElement) : HTMLElement {

	var isEditable = tdElement.getAttribute("data-editable");
	if (isEditable !== "true") {
		return null;
	}

	if (getHasEdtiorChild(tdElement)) {
		return null;
	}



	var isEditable = tdElement.getAttribute("data-editable");

	let isSelectBox = false;

	let thIdWithTypeInfo = tdElement.getAttribute(TH_ID_WITH_TYPE_INFO);
	let tdWithTypeInfo = (thIdWithTypeInfo !== null) ? document.getElementById(thIdWithTypeInfo) : null;
	// let payloadTypeInfo = tdWithTypeInfo !== null ? tdWithTypeInfo.getAttribute(COL_PAYLOAD_TYPE) : null;

	let fieldInfo = getFieldInfoFromTd(tdElement);
	if (fieldInfo.options) {
		isSelectBox = true;
	}

	let payloadTypeInfo = fieldInfo.dataType;

	// if (payloadTypeInfo === "selectbox") {
	// 	isSelectBox = true;
	// }

	

	// var spanFieldInTd = tdElement.children[0].children[0] as HTMLElement;
	var spanFieldInTd = tdElement.querySelector(".tableCellContentSpan") as HTMLElement;
	var spanFieldInTdText = spanFieldInTd.textContent;

	if (isSelectBox) {
		// let tdWithSelectionValues = document.getElementById(thIdWithTypeInfo);
		// let selectionValuesAsJsonString = tdWithSelectionValues.getAttribute(SELECTION_VALUES_ATTR_NAME);
		


		// var newInputStr = "<select name='cars' id='" + TAB_EDIT_FIELD_ID + "'> <option value='WRAP'>WRAP</option> 	<option value='PROTEIN'>PROTEIN</option> 	<option value='mercedes'>Mercedes</option> 	<option value='audi'>Audi</option>  </select>";
		let newInputEl = generateSelectField(TAB_EDIT_FIELD_ID, fieldInfo.options, spanFieldInTdText);
		spanFieldInTd.replaceWith(newInputEl);
		//   replaceElementByHtmlString(spanFieldInTd, newInputStr) as HTMLInputElement;
		setCellEditorEventHandlers(newInputEl);
		tdElement.setAttribute(TD_HAS_INPUT_FIELD_CHILD, "y")  // TODO: retire
		setHasEditorChild(tdElement, true);

		// createTableEditorCompanionForStandardStringEditor(newInputEl, spanFieldInTd);

		new StandardStringEditorCompanion(newInputEl,spanFieldInTd);

		
		newInputEl.focus();
		tdElement.style.backgroundColor = "darksalmon";
		//
		//Keep the focus outline appearance on the <td> although the contained input field within this td element has the focus.
		//
		tdElement.classList.add("forcedFocusOutline"); 	
		return newInputEl;
	
	
	} else {
		if (payloadTypeInfo === "LOCALDATETIME") {

			let newInputEl = createTableDateTimeEditorWithCompanion(tdElement, TAB_EDIT_FIELD_ID);
			spanFieldInTd.replaceWith(newInputEl);

			// setCompoundCellEditorEventHandlers(newInputEl);

			setHasEditorChild(tdElement, true);



			// newInputEl.setAttribute(OLD_VALUE_ATTR_NAME, spanFieldInTdText);
			// newInputEl.setAttribute(OLD_SPAN_ID_ATTR_NAME, spanFieldInTd.id);
			// linkTableEditorDataToDateTimeEditor( newInputEl, spanFieldInTd)
			setSmartFocus(newInputEl);
			// newInputEl.focus();

			return newInputEl;
		} else {
			let newInputElStr = "<input class='tdInput'; style='width: 100%; border:0px; padding:0px;' id='" + TAB_EDIT_FIELD_ID + "' value='" + spanFieldInTdText + "'	>"
			let newInputEl = replaceElementByHtmlString(spanFieldInTd, newInputElStr) as HTMLInputElement;
			setCellEditorEventHandlers(newInputEl);
			tdElement.setAttribute(TD_HAS_INPUT_FIELD_CHILD, "y")
			setHasEditorChild(tdElement, true);
			// newInputEl.setAttribute(OLD_VALUE_ATTR_NAME, spanFieldInTdText);
			// newInputEl.setAttribute(OLD_SPAN_ID_ATTR_NAME, spanFieldInTd.id);
			new StandardStringEditorCompanion(newInputEl, spanFieldInTd);
			// createTableEditorCompanionForStandardStringEditor(newInputEl, spanFieldInTd);
			newInputEl.focus();
			tdElement.style.backgroundColor = "darksalmon"; 
			//
			//Keep the focus outline appearance on the <td> although the contained input field within this td element has the focus.
			//
			tdElement.classList.add("forcedFocusOutline"); 	
			return newInputEl; 
		}
	}
	
}

function setHasEditorChild(tdElement: HTMLElement, val: boolean) {
	tdElement["hasEditorChild"] = val;
}


function getHasEdtiorChild(tdElement: HTMLElement) : boolean {
	var val = tdElement["hasEditorChild"];
	if (val === true) {
		return true;
	} 

	if (findFirstChildWithAttribute(tdElement, "data-is-cell-editor") !== null) {
		return true;
	}

	return false;


}

		
function generateSelectField(id: string, options : string[], currentValue: string) : HTMLSelectElement {
	let selectBox = document.createElement("select");
	selectBox.id = id;
	for(let optionStr of options) {
		let option = document.createElement("option");
    	option.value = optionStr;
    	option.text = optionStr;
		if (optionStr === currentValue) {
			option.setAttribute("selected", "true");
		}
		selectBox.appendChild(option);
	}
	return selectBox;
}




function setCellEditorEventHandlers(cellEditor: HTMLElement) {
	cellEditor.addEventListener("blur", inputFieldFocusLostHandler);
	cellEditor.addEventListener("keydown", keyDownInputField);
	cellEditor.addEventListener("mousedown", mouseDownInputField);
}


// function setCompoundCellEditorEventHandlers(cellEditor: HTMLElement) {
// 	cellEditor.addEventListener("focusout", compoundCellEditorFocusLostHandler);
// 	cellEditor.addEventListener("keydown", keyDownInputField);
// 	cellEditor.addEventListener("mousedown", mouseDownCompoundCellEditor);
// }


function inputFieldFocusLostHandler(event: FocusEvent) {
	console.log("inputFieldFocusLostHandler entered.");

	let eventTarget:EventTarget = event.target;
	let targetInputElement = eventTarget as HTMLInputElement;

	let table = getTableFromChildElement(targetInputElement);
	let strippedTableId = constructStrippedId(table.id);

	if (! focusLossIsPermanentV2(event)) {
		return;
	}

	console.log("inputFieldFocusLostHandler 1.");
	let parentTdElement : HTMLTableCellElement = getTdFromChildElement(targetInputElement) as HTMLTableCellElement;

	if (parentTdElement == null) {
		//
		// The input was lost because the cell editor has been programmatically removed. In this case, there is nothing to do.
		//
		console.log("inputFieldFocusLostHandler 2.");
		return;

	}
	console.log("inputFieldFocusLostHandler 3.");
	
	const ted = getCompanion(targetInputElement) as TableEditorCompanion<HTMLInputElement>;

	let oldVal : string = ted.oldValue;
	let oldId : string = ted.replacingSpanFieldId;
	
	let newVal : string;
	let valueWasModified: boolean = false;
	if (ted.restoreOldDataOnFocusLoss) {
		// Typically when Escape was pressed on the input field. 
		newVal = oldVal;  
	}  else {
		newVal = ted.getStringValue();
		valueWasModified = true;
	}

	unplaceCellEditor(oldId, newVal, parentTdElement, ted, valueWasModified);

	console.log("inputFieldFocusLostHandler leaving.");
}


export function unplaceCellEditor(oldId: string, newVal: string, parentTdElement: HTMLTableCellElement, ted: TableEditorCompanion<HTMLElement>, valueWasModified: boolean) {
	// let elementIdToReplace = TAB_EDIT_FIELD_ID;
	const elementIdToReplace = ted.topElement.id;

	const elementToReplace = ted.topElement;

	let newEl = document.createElement("span");
	newEl.id = oldId;
	newEl.classList.add("tableCellContentSpan");
	newEl.innerHTML = newVal;

	elementToReplace.replaceWith(newEl)

	// let newElementDef = "<span id='" + oldId + "' class='tableCellContentSpan'>" + newVal + "</span>";
	// 7let newElement = replaceElementByIdAndHtmlString(elementIdToReplace, newElementDef);
	
	parentTdElement.removeAttribute(TD_HAS_INPUT_FIELD_CHILD);
	setHasEditorChild(parentTdElement, false);
	let coords = parseTdCoordFromTd(parentTdElement);


	parentTdElement.style.backgroundColor = "white";
	parentTdElement.classList.remove("forcedFocusOutline");

	// This is the default value. Once the focus loss event is handled, it needs to be set to its default.
	// ted.restoreOldDataOnFocusLoss = false;

	if (valueWasModified) {
		//
		// We update the server side model with the data since it might get lost during a full refresh of the table with server-side data.
		//
		const powerTable = getPowertableFromChildElement(parentTdElement);
		const pageName = getPageName(powerTable);
		const serverMsgHandler = MSG_HANDLER_HANDLE_POWERTABLE_ACTION;
		const fieldName = parentTdElement.getAttribute(TD_MAP_TARGET_ATTR_NAME);
		const msg: jointTypes.MsgPowertableAction_SaveCell = {
			msgName: jointTypes.CS_MESSAGE,
			module: null,
			serverMsgHandler: serverMsgHandler,
			pageName: pageName,
			command: POWERTABLE_COMMAND,
			subCommand: SYNC_CELL_DATA,
			fieldName: fieldName,
			rowIdx: coords.row,
			tableId: powerTable.id,
			value: newVal
		};

		SessionHandling.ajaxCallStandard(msg);
	}
}

/**
 * Top element of the compound editor has lost the focus.
 * @param event 
 * @returns 
 */
export function compoundCellEditorFocusLostHandler(event: FocusEvent) {

	if (! focusLossIsPermanentV2(event)) {
		return;
	}
	
	let cellEditorTopDiv = event.currentTarget as HTMLDivElement;

	//
	// As long as there is a popup window open for this editor, we ignore the focus loss events.
	//
	const dateTimeEditorCompanion: TableDateTimeEditorCompanion = getCompanion(cellEditorTopDiv) as TableDateTimeEditorCompanion;
	if (dateTimeEditorCompanion.calendarPopup) {
		console.log("compoundCellEditorFocusLostHandler POS02.");
		return;
	}

	let leafElementThatWillReceiveFocus : HTMLElement = event.relatedTarget as HTMLElement;

	// When we change the focus within descendants, we do nothing here. (The focus of the compountElement is not considered lost)
	if (cellEditorTopDiv.contains(leafElementThatWillReceiveFocus)) {
		console.log("compoundCellEditorFocusLostHandler POS03!!!.");
		return;
	}

	let targetInputElement = cellEditorTopDiv as HTMLInputElement;

	let table = getTableFromChildElement(targetInputElement);
	let strippedTableId = constructStrippedId(table.id);


	console.log("compoundCellEditorFocusLostHandler 1.");
	let parentTdElement : HTMLTableCellElement = getTdFromChildElement(targetInputElement) as HTMLTableCellElement;

	if (parentTdElement == null) {
		//
		// The input was lost because the cell editor has been programmatically removed. In this case, there is nothing to do.
		//
		console.log("compoundCellEditorFocusLostHandler 2.");
		return;
	}
	console.log("compoundCellEditorFocusLostHandler 3.");
	let parentTrElement : HTMLTableRowElement = parentTdElement.parentElement as HTMLTableRowElement;

	let tableEditorCompanion = getCompanion(targetInputElement) as TableEditorCompanion<HTMLInputElement>;

	let oldVal : string = tableEditorCompanion.oldValue;
	let oldId : string = tableEditorCompanion.replacingSpanFieldId;
	
	let newVal : string;
	let valueWasModified: boolean = false;
	if (dateTimeEditorCompanion.restoreOldDataOnFocusLoss) {
		// Typically when Escape was pressed on the input field. 
		newVal = oldVal;  
	}  else {
		let parsingErrorOccurred = false;
		let parsedDateOrError = parseGermanDate(dateTimeEditorCompanion.dateEditor.value);
		if (isParsingError(parsedDateOrError)) {
			parsingErrorOccurred = true;
			dateTimeEditorCompanion.setErrorMessageDate(parsedDateOrError.errorMessage);
		} else {
			dateTimeEditorCompanion.removeErrorMessageDateIfExists();
		}

		let parsedTimeOrError = parseTime(dateTimeEditorCompanion.timeEditor.value);
		if (isParsingError(parsedTimeOrError)) {
			parsingErrorOccurred = true;
			dateTimeEditorCompanion.setErrorMessageTime(parsedTimeOrError.errorMessage);
		} else {
			dateTimeEditorCompanion.removeErrorMessageTimeIfExists();
		}


		//
		// If there is a formatting error, we do not set underlying variables.
		//
		if (parsingErrorOccurred) {
			return;
		}

		// Make type deduction magic obvious here.
		let date = parsedDateOrError;
		let time = parsedTimeOrError;

		newVal = tableEditorCompanion.getStringValue();

		valueWasModified = true;
	}

	unplaceCellEditor(oldId, newVal, parentTdElement, dateTimeEditorCompanion, valueWasModified);

	console.log("compoundCellEditorFocusLostHandler leaving.");
}




export function keyDownInputField(event: KeyboardEvent) {
	console.log("keyDownInputField: " + event.key);
	if (event.key === "Escape") {
		console.log("keyDownInputField entered 1."); 
		let eventTarget:EventTarget = event.target;
		let targetInputElement = eventTarget as HTMLInputElement;
		let parentTdElement = getTdFromChildElement(targetInputElement);
		console.log(parentTdElement)

		const companion = getCompanion(targetInputElement) as TableEditorCompanion<HTMLInputElement>; 

		companion.restoreOldDataOnFocusLoss = true;
		parentTdElement.focus();
	} else {
		if (event.key ==="Enter") {
			let eventTarget:EventTarget = event.target;
			let targetInputElement = eventTarget as HTMLInputElement;
			let parentTdElement = getTdFromChildElement(targetInputElement);
			parentTdElement.focus();
		}
	}
	console.log("keyDownInputField leaving.");
}


export function keyDownDateTimeEditorInputField(dateTimeEditorCompanion: TableDateTimeEditorCompanion, event: KeyboardEvent) {
	console.log("keyDownInputField: " + event.key);
	const compoundEdtitorTopElement = dateTimeEditorCompanion.topElement;
	let parentTdElement = getTdFromChildElement(compoundEdtitorTopElement);

	if (event.key === "Escape") {
		dateTimeEditorCompanion.restoreOldDataOnFocusLoss = true;
		parentTdElement.focus();
	} else {
		if (event.key ==="Enter") {
			parentTdElement.focus();
		}
	}
	console.log("keyDownInputField leaving.");
}


function mouseDownInputField(event: MouseEvent) {
	console.log("mouseDownInputField");
 	event.stopPropagation();
}






// export function saveTableData(powerTable: Powertable) {
// 	let serverMsgHandler = MSG_HANDLER_HANDLE_POWERTABLE_ACTION;
// 	let pageName = getPageName(powerTable);
// 	// let crudAndDtoArray : jointTypes.CudAndDto[] = getRowsToUpdate(powerTable.id);

// 	let msg : jointTypes.MsgPowerTableAction = {
// 		msgName: jointTypes.CS_MESSAGE, 
// 		module: null,
// 		serverMsgHandler: serverMsgHandler,
// 		pageName: pageName,
// 		command: POWERTABLE_COMMAND,
// 		subCommand: SAVE_TABLE_DATA,
// 		tableId: powerTable.id
// 	}

// 	SessionHandling.ajaxCallStandard(msg);	

// }


function getRowCount(tableId: string) : number {
	let tableEl : HTMLTableElement  = document.getElementById(tableId) as HTMLTableElement;
	let tableRowCountStr = tableEl.getAttribute(TABLE_ROWCOUNT_ATTR_NAME);
	let tableRowCount = Number(tableRowCountStr);
	return tableRowCount;
}

/**
 * Returns first row that is checked. 
 * Returns -1 if no row is checked
 * @param tableId PowerTable to process
 * @returns See description
 */
function getFirstSelectedRowIdx(tableId: string) {
	let tableRowCount = getRowCount(tableId);
	for (let rowRun = 0; rowRun < tableRowCount; rowRun++) {
		if (rowIsChecked(tableId, rowRun)) {
			return rowRun;
		}
	}
}

/**
 * Returns last row that is checked. 
 * Returns -1 if no row is checked
 * @param tableId PowerTable to process
 * @returns See description
 */
function getLastSelectedRowIdx(tableId: string) {
	let tableRowCount = getRowCount(tableId);
	for (let rowRun = tableRowCount-1; rowRun >= 0; rowRun--) {
		if (rowIsChecked(tableId, rowRun)) {
			return rowRun;
		}
	}
}



function rowIsChecked(tableId: string, rowIdx: number) : boolean {
	let checkBox : HTMLInputElement = getCheckBox(tableId, rowIdx);
	if (checkBox.checked) {
		return true;
	} 
	return false;
}


function getTableRow(tableId: string, rowIdx: number) : HTMLTableRowElement {
	let trId : string = constructTrId(tableId, rowIdx);
	return document.getElementById(trId) as HTMLTableRowElement;
}



function getCheckBox(tableId: string, rowIdx: number) : HTMLInputElement {
	let trId : string = tableId + '-cb-'  + rowIdx;
	return document.getElementById(trId) as HTMLInputElement;
}


/** 
 * *****************************************
 * Table column resize handling 
 *******************************************
**/

let g_resizerDragStartX = 0;
let g_resizerColWidth= 0;
let g_resizerElement = null;
let g_resizerThElement = null;

export function CEH_resizerMouseDownHandler(eve: MouseEvent) {
	// Get the current mouse position
	g_resizerDragStartX = eve.clientX;
	g_resizerElement = eve.target as HTMLElement;
	g_resizerThElement = g_resizerElement.parentElement;

	// Calculate the current width of column
	const styles = window.getComputedStyle(g_resizerThElement);
	g_resizerColWidth = parseInt(styles.width, 10);

	// Attach listeners for document's events
	document.addEventListener('mousemove', resizerMouseMoveHandler);
	document.addEventListener('mouseup', resizerMouseUpHandler);
}


function resizerMouseMoveHandler(e: MouseEvent) {
	// Determine how far the mouse has been moved
	const dx = e.clientX - g_resizerDragStartX;

	// Update the width of column
	g_resizerThElement.style.width = `${g_resizerColWidth + dx}px`;

	console.log("New width: " + g_resizerThElement.style.width);
}



// When user releases the mouse, remove the existing event listeners
function resizerMouseUpHandler(e: MouseEvent) {
	document.removeEventListener('mousemove', resizerMouseMoveHandler);
	document.removeEventListener('mouseup', resizerMouseUpHandler);

	let pageName = getPageName(g_resizerElement);
	let powerTable = getPowertableFromChildElement(g_resizerElement);
	let symbolColId = g_resizerElement.getAttribute(SYMBOL_COL_ID_ATTR_NAME);
	let newColWidth = g_resizerThElement.style.width;
	let serverMsgHandler = getServerMsgHandler(g_resizerElement);
	if (serverMsgHandler === null) {
		console.count("Whatabug!!");
	}


	let msg: jointTypes.MsgPowerTableAction_MsgUpdateTableColumnWidth = {
		
		msgName: jointTypes.CS_MESSAGE,
		module: null,
		serverMsgHandler: MSG_HANDLER_HANDLE_POWERTABLE_ACTION,
		// msgType: "MsgPowerTableAction_MsgUpdateTableColumnWidth",
		pageName: pageName,
		command: POWERTABLE_COMMAND,
		subCommand: UPDATE_TABLE_COLUMN_WIDTHS,
		tableId: powerTable.id,
		symbolColId: symbolColId,
		newColWidth: newColWidth	
	};

	SessionHandling.ajaxCallStandard(msg);

}


export function handleInsertEntityForTableEdit(mouseEvent: MouseEvent) {
	let target : HTMLElement = mouseEvent.target as HTMLElement;
	let tableId = target.getAttribute(jointTypes.TABLE_ID_ATTR_NAME);
	let rowCount = getRowCount(tableId);
	let pageName = getPageName(target);
	
	let table: HTMLTableElement = document.getElementById(tableId) as HTMLTableElement;
	const powerTable = getPowerTable(table);
	let serverMsgHandler = MSG_HANDLER_HANDLE_POWERTABLE_ACTION;

	let insertIdx = 0;
	let lastSelectedRow = getLastSelectedRowIdx(tableId);
	if (lastSelectedRow <= 0 || rowCount == 0) {
		insertIdx =0;
	} else {
		insertIdx = lastSelectedRow +1;
	}


	let msg: jointTypes.MsgInsertEntityForTableEdit =  { 
		msgName: jointTypes.CS_MESSAGE, 
		module: null, serverMsgHandler: serverMsgHandler, 
		pageName: pageName, 
		command: POWERTABLE_COMMAND,
		subCommand: CREATE_NEW_ENTITY_FOR_TABLE_EDIT,		
		tableId: powerTable.id,
		tablePosition: insertIdx	
	};
	SessionHandling.ajaxCallStandard(msg);

}






function getFocusedRow(powerTable: HTMLElement) : HTMLTableRowElement{
	let focusedEl = document.activeElement as HTMLElement;
	let result : HTMLTableRowElement = null;
	if (powerTable.contains(focusedEl)) {
		result = getTableRowForChild(focusedEl);
	}
	return result;
}



function getTableRowForChild(childInTable : HTMLElement) : HTMLTableRowElement{
	let treeRunner = childInTable;
	let tdElement: HTMLElement = null;
	while (treeRunner !== null && treeRunner.tagName !== "TABLE") {
		if (treeRunner.tagName === "TD") {
			tdElement = treeRunner;
			break;
		} else {
			treeRunner = treeRunner.parentElement;
		}
	} 
	return tdElement as HTMLTableRowElement;
}


// export function CEH_saveTableData(mouseEvent: MouseEvent) {
// 	let target : HTMLElement = mouseEvent.target as HTMLElement;
// 	let tableId = target.getAttribute("tableId");
//    	let powerTable = document.getElementById(tableId) as Powertable;
// 	saveTableData(powerTable);
// }


export function CEH_handleTableCheckBoxClicked(mouseEvent: MouseEvent) {
	let target : HTMLElement = mouseEvent.target as HTMLElement;
	let powerTable = getPowertableFromChildElement(target);
	let row = getTrFromChildElement(target);
	let rowIdx = getRowIdxFromTr(row);
	let serverMsgHandler = target.getAttribute(jointTypes.SERVER_MSG_HANDLER_ATTR_NAME);
	let subCommand = target.getAttribute(jointTypes.SERVER_SUB_COMMAND_ATTR_NAME);

	let pageName = getPageName(target);

	let msg : jointTypes.MsgPowerTableAction_OnRow = {
		msgName: jointTypes.CS_MESSAGE,
		module: null,  
		serverMsgHandler: "handlePowertableAction",
		// msgType: "MsgPowerTableAction_CheckboxClicked",
		command: POWERTABLE_COMMAND,
		subCommand: TOGGLE_CHECK_BOX,
		pageName: pageName,
		tableId: powerTable.id,
		rowIdx: rowIdx,
	}

	SessionHandling.ajaxCallStandard(msg);
}


export class Powertable extends HTMLElement {

	root: HTMLTableElement;
	private headInfo: FieldInfo[];


	constructor() {
		super();
	}
	connectedCallback() {
		const shadow = this.attachShadow({ mode: "open" });
		const sheet = new CSSStyleSheet();
		const dateEditorStyle = StyleManager.powerTableStyle + StyleManager.powertableInputFieldStyle + StyleManager.inputFieldStyle + StyleManager.horizontalDivFlowStyle + StyleManager.calendarLaunchButtonStyle;
		sheet.replaceSync(dateEditorStyle);
		shadow.adoptedStyleSheets = [sheet]

		shadow.appendChild(this.root);
	}

	static getTag() {
		return "kewebsi-powertable"
	}

	getHeadInfo() {
		return this.headInfo;
	}

	setHeadInfo(headInfo: FieldInfo[]) {
		this.headInfo = headInfo;
	}

	getTableCell(rowIdx: number, fieldName: string) : HTMLTableCellElement {
		// let headInfo = table.getHeadInfo();
		let fieldIdx = getIndexOfFieldInFieldInfo(fieldName, this.headInfo);
		let tdId = constructPayloadTdId(this.root, rowIdx, fieldIdx);
		return this.getChildById(tdId) as HTMLTableCellElement;
	}

	getPayloadTd(table: HTMLTableElement, rowIdx: number, fieldName: string) {
		let fieldInfo = getHeadInfo(table);
		let fieldIdx = getIndexOfFieldInFieldInfo(fieldName, fieldInfo);
		let tdId = constructPayloadTdId(table, rowIdx, fieldIdx);
		this.getChildById(tdId);
	}
	
	
	getStatusTd(rowIdx: number) {
		let tdId = constructRowStateSpanId(this.root, rowIdx);
		this.getChildById(tdId);
	}
	
	
	getStatusCellSpan(rowIdx: number) : HTMLSpanElement {
		let spanId = constructRowStateSpanId(this.root, rowIdx);
		let spanElement = this.getChildById(spanId) as HTMLSpanElement;
		return spanElement;
	}
	
	getSelectionCheckbox(rowIdx: number) : HTMLSpanElement {
		let spanId = constructSelectCheckboxId(this.root, rowIdx);
		let spanElement = this.getChildById(spanId) as HTMLSpanElement;
		return spanElement;
	}

	getChildById(childId: string) {
		return this.shadowRoot.getElementById(childId) as HTMLElement
	}

	getActiveElement() {
		return this.shadowRoot.activeElement;
	}

	isFocusedChild(elm: HTMLElement) {
		if (this.getActiveElement() === elm) {
			return true;
		}
		return false;
	}

	establishEditorOnTdIfCellFocused(eventOnTd: MouseEvent) : boolean { 
		let eventTarget:EventTarget = eventOnTd.currentTarget; // We capture also child clicks (except for those in an editor, which should be stopped from propagation)
		console.log("Target :" + eventTarget); 

		//
		// Just logging
		//
		let focusedElement = this.getActiveElement();
		if (focusedElement !== null) {
			console.log("Focused element :" + focusedElement.tagName + "  " + focusedElement.id); 
		}

		let editorEstablished = false;
	
	
		let tdElement : HTMLElement = eventTarget as HTMLElement;
		if (this.isFocusedChild(tdElement)) {
			console.log("isFocused");
	
			let newInputEl :HTMLElement  = establishCellEditorOnClick(eventOnTd);
			if (newInputEl !== null) {
				editorEstablished = true;
				eventOnTd.preventDefault();
	
				// Unfortunately, we have not found a way to place the cursor according to a click in this phase. 
				// Hence the next two lines are commented out. (They don't work, neither did all variations of this attempt that I tried.)
				// let mouseDown = new MouseEvent(eventOnTd.type, eventOnTd);
				// newInputEl.dispatchEvent(mouseDown);
	
				// As a second best solution, we select the whole content.
				if (newInputEl.tagName === "INPUT" ) {
					let inputField = newInputEl as HTMLInputElement;
					inputField.setSelectionRange(0, inputField.value.length);
				}
			}
		} else {
			console.log("is not focused");
		}
		return editorEstablished;
	}


}

//* **************************** Using GuiDef ************************************* */

export function createTableByDef(guiDef: GuiDef) : HTMLElement {

	const powerTable = document.createElement(Powertable.getTag()) as Powertable;
	powerTable.id = guiDef.id;
	

	if (guiDef.tag !== "powertable") {
		console.log("Invoked createTableByDef with wrong tag: " + guiDef.tag);
	}

	let payload: TablePropsAndState = guiDef.state as TablePropsAndState;

	let table = document.createElement("table");

	powerTable.root = table;
	setWebComponent(table, powerTable);

	table.id = guiDef.id + "-table";
	setStrippedTableId(table);

	powerTable.setHeadInfo(payload.headInfo);   // Store the head info since we need it for partial updates.

	// Client-side definitions
	table.classList.add("entity-table");
	table.setAttribute("style", "width:10px;");

	setBooleanAttribute(table, DETAIL_DISPLAYS_LINKED, payload.detailDisplaysAreLinked);
	createTableHeader(table, payload.headInfo);
	let rows : TableRowDataForGui[] = payload.rowDataAndErrors;
	createTableBody(table, payload.headInfo, rows);

	return powerTable;
}

function setWebComponent(htmlEl: HTMLElement, webComp: HTMLElement) {
	htmlEl["webcomp"] = webComp;
}


export function getWebComponent(htmlEl: HTMLElement) : HTMLElement {
	return htmlEl["webcomp"] as HTMLElement;
}


function createTableHeader(table: HTMLTableElement, headInfo: FieldInfo[]) {
	let thead = table.createTHead();
	// let thead = document.createElement("thead");
	let headRow = document.createElement("tr");
	thead.appendChild(headRow);
	headRow.appendChild(createStateTh());
	headRow.appendChild(createCheckTh());
	for (let fieldInfo of headInfo) {
		let th = document.createElement("th");
		headRow.appendChild(th);
		let style = "width:" + fieldInfo.colWidth + "; position:relative";  // "position:relative" is required in order for the absolute placing of the resizer to work.
		th.setAttribute("style", style);

		if (fieldInfo.label.length != null) {
			th.innerHTML = fieldInfo.label;
		}

		let resizer = createResizerDiv(fieldInfo);
		th.appendChild(resizer);

	}

}


export function updateTablePayloadByDef(guiDef: GuiDef) {
	let powerTable = document.getElementById(guiDef.id) as Powertable
	const htmlTable = powerTable.root;
	let updates: TableDataUpdateForGui = guiDef.state as TableDataUpdateForGui;
    
	let updateScope = updates.updateScope
	if (updateScope === "ALL_DATA") {
		let rows: TableRowDataForGui[] = updates.completeTableDataForGui;
		let headInfo = powerTable.getHeadInfo();
		
		replaceTableRows(htmlTable, headInfo, rows);
	} else {
		if (updateScope !== "PARTIAL_DATA") {
			console.log("WARNING: Unknown updatescope in updateTablePayloadByDef:" + updateScope);
		}
		if (updates.cellUpdates) {
			for (let cellUpdate of updates.cellUpdates) {
				let td = powerTable.getTableCell(cellUpdate.rowIdx, cellUpdate.fieldName);
				updatePayloadTd(td, cellUpdate.value, cellUpdate.errorMsg);
			}
		}
		if (updates.rowStateChanges) {
			for (let rowStateChange of updates.rowStateChanges) {
				let span = powerTable.getStatusCellSpan(rowStateChange.rowIdx)
				let parentTrElement = span.parentElement.parentElement as HTMLTableRowElement;

				let rowStateDisplay = mapEntityStateToDisplay(rowStateChange.newState);
				setRowState(parentTrElement, rowStateChange.newState);

				span.innerHTML = rowStateDisplay;
				parentTrElement.setAttribute(jointTypes.TD_ROW_STATE_ATTR_NAME, jointTypes.TD_ROW_STATE_UPDATED);
			}
		}

		if (updates.rowSelectionChanges) {
			for (let rowSelectionChange of updates.rowSelectionChanges) {
				let checkBox = powerTable.getSelectionCheckbox(rowSelectionChange.rowIdx);
				if (rowSelectionChange.isSelected) {
					checkBox.setAttribute("checked", "true");
				} else {
					checkBox.removeAttribute("checked");
				}
			}
		}


	}
}

function mapEntityStateToDisplay(entityState) : string {
	let display: string;
	switch(entityState) {
		case jointTypes.ENTITY_STATE_NEW: display = "N"; break;
		case jointTypes.ENTITY_STATE_UPDATED: display = "U"; break;
		case jointTypes.ENTITY_STATE_DELETED: display = "D"; break;
		case jointTypes.ENTITY_STATE_UNMODIFIED: display = " "; break;
		default: warn("Unrecognized entity state in mapEntityStateToDisplay(): " + entityState);
	}
	return display;
}

/**
 * Function not useful yet. Useful, if we change for example the row color depending on row state.
 * */
function setRowState(td: HTMLTableRowElement, entityState: string) : string {
	
	let stateIndicator: string;
	switch(entityState) {
		case jointTypes.ENTITY_STATE_NEW: stateIndicator = jointTypes.TD_ROW_STATE_INSERTED; break;
		case jointTypes.ENTITY_STATE_UPDATED: stateIndicator = jointTypes.TD_ROW_STATE_UPDATED; break;
		case jointTypes.ENTITY_STATE_DELETED: stateIndicator = jointTypes.TD_ROW_STATE_DELETED; break;
		case jointTypes.ENTITY_STATE_UNMODIFIED: stateIndicator = jointTypes.TD_ROW_STATE_UNMODIFIED; break;
		default: warn("Unrecognized entity state in mapEntityStateToDisplay(): " + entityState); break;
	}
	return stateIndicator;
}


function createTableBody(table: HTMLTableElement, headInfos: FieldInfo[], rows: TableRowDataForGui[]) : HTMLTableSectionElement {
	let tableBody = table.createTBody();  // Adds en empty body. (Note: createTBody() can create multiple bodies.)
	fillTable(table, headInfos, rows);
	return tableBody;
}


function fillTable(table: HTMLTableElement, headInfos: FieldInfo[], rows: TableRowDataForGui[]) {
	let rowIdx = 0;
	let strippedTableId = constructStrippedId(table.id);
	for (let rowData of rows) {
		let tr = table.tBodies[0].insertRow(-1);  // -1 adds ti the end
		tr.id = constructTrId(table.id, rowIdx);

		let stateTd = createStateTd(rowData, table, rowIdx);
		tr.appendChild(stateTd);

		let checkTd = createSelectTd(rowData, table, rowIdx);
		tr.appendChild(checkTd);

		let colIdx = 0;
		for (let cellText of rowData.payload) {
			let headInfo = headInfos[colIdx];
			let errorMsg = findErrorMsgForField(rowData.errors, headInfo.symbolColId);
			let td = createPayloadTd(strippedTableId, cellText, errorMsg, headInfo, rowIdx, colIdx);
			tr.appendChild(td);
			colIdx++;
		}

		rowIdx++
	}
}

function findErrorMsgForField(rowErrors: TableCellDisplayError[], fieldName: string) : string {
	let errorMsg: string;
	if (rowErrors) {
		for (let err of rowErrors) {
			if (err.fieldName === fieldName) {
				errorMsg = err.errorMsg;
			}
		}
	}
	return errorMsg;
}

/**
 * Removing a table body does not work for browser-internal reasons. 
 * Hence we need to remove all rows from it in order to re-populate it. 
 * @param table 
 */
function deleteAllTableRows(table: HTMLTableElement) {

	let tbody = table.tBodies[0];
	let trNodes = tbody.children;

	// while (trNodes.length > 0) {
	// 	trNodes.item[trNodes.length-1].remove();
	// }
	


	for (let rowIdx = trNodes.length-1; rowIdx >= 0; rowIdx--) {
		let row = trNodes[rowIdx] as HTMLTableRowElement;
		row.remove();
	}


	// while (table.rows.length > 0) {
	// 	let idxOfLastElement = table.rows.length-1;
	// 	let lastRow = table.rows[idxOfLastElement] as HTMLTableRowElement;
	// 	if (lastRow.tagName === "tr") {
	// 		table.deleteRow(table.rows.length-1);
	// 	}
	// }
} 

function replaceTableRows(table: HTMLTableElement, headInfos: FieldInfo[], rows: TableRowDataForGui[]) {
	deleteAllTableRows(table);
	fillTable(table, headInfos, rows);
}



function createResizerDiv(fieldInfo: FieldInfo) : HTMLElement {
	let div = document.createElement("div");
	div.classList.add("resizer");
	div.addEventListener("mousedown", CEH_resizerMouseDownHandler);
	div.setAttribute(SYMBOL_COL_ID_ATTR_NAME, fieldInfo.symbolColId);
	return div;
}


function createStateTh() : HTMLElement {
	let th = document.createElement("th");
	let style = "width:15px; position:relative";  // "position:relative" is required in order for the absolute placing of the resizer to work.
	th.setAttribute("style", style);
	th.innerHTML = "S";
	return th;
}

function createCheckTh() : HTMLElement {
	let th = document.createElement("th");
	let style = "width:15px; position:relative";  // "position:relative" is required in order for the absolute placing of the resizer to work.
	th.setAttribute("style", style);
	th.innerHTML = " ";
	return th;
}


function createStateTd(tableRowData: TableRowDataForGui, table: HTMLTableElement, row: number) : HTMLElement {
	let td = document.createElement("td");
	td.id = constructRowStateTdId(table, row);
	let span = document.createElement("span");
	td.appendChild(span);
	span.id = constructRowStateSpanId(table, row);
	span.innerHTML = tableRowData.state;
	return td;
}

function createSelectTd(tableRowData: TableRowDataForGui, table: HTMLTableElement, row: number) : HTMLElement {
	let td = document.createElement("td");
	td.id = constructSelectTdId(table, row);

	let cp = document.createElement("INPUT");
	cp.setAttribute("type", "checkbox");
	cp.id=constructSelectCheckboxId(table, row);
	if (tableRowData.selected) {
		cp.setAttribute("checked", "true")
	}
	td.appendChild(cp);
	td.addEventListener("click", CEH_handleTableCheckBoxClicked);

	return td;

}


function createPayloadTd(
	strippedTableId: string, 
	cellText: string, 
	errorMsg: string,
	fieldInfo: FieldInfo, 
	rowIdx: number, 
	colIdx: number) : HTMLTableCellElement 
{
	let td = document.createElement("td");
	td.id = constructPayloadTdIdFast(strippedTableId, rowIdx, colIdx);


	switch (fieldInfo.dataType) {
		case "LOCALDATETIME":
			let ldt: LocalDateTime;
			if (cellText.length == 0) {
				const today = new Date();
				const currentYear = today.getFullYear();
				const currentMonth = today.getMonth()+1;
				const currentDay = today.getDate();
				const currentHour = today.getHours();
				const currentMinute = today.getMinutes();
				const currentSecond = today.getSeconds();
				ldt = createLocalDateTime(currentYear, currentMonth, currentDay, currentHour, currentMinute, currentSecond);
			} else {
				ldt = parseIsoDateTime(cellText);
			}
			setTdBaseData_LocalDateTime(td, ldt);	
			let dateStr = printDateDDMMYYYY(ldt.date);
			let timeStr = printTime24h(ldt.time);
		break;
		default: 
		setTdBaseData_string(td, cellText);
	}


	//
	// Functional attributes
	//
	td.setAttribute("tabindex", "0");
	td.setAttribute(TD_MAP_TARGET_ATTR_NAME, fieldInfo.symbolColId);
	td.setAttribute(COLTYPE_ATTR_NAME, COLTYPE_PAYLOAD);
	td.setAttribute(ROW_IDX_ATTR_NAME, rowIdx.toString());
	td.setAttribute(COL_IDX_ATTR_NAME, colIdx.toString());
	td.setAttribute("data-editable", fieldInfo.editable ? "true" : "false");
	

	td.addEventListener("keydown", tdHandleKeyDown);
	td.addEventListener("mousedown", tdHandleMouseDown);
	td.addEventListener("focus", tdHandleFocusReceived);
	td.addEventListener("focusout", tdHandleFocusLost);


	let topDiv = document.createElement("div");
	topDiv.classList.add("vertikalDivTableCell");
	td.appendChild(topDiv);

	const errorWrapper = document.createElement("div");
	topDiv.appendChild(errorWrapper);


	let span = createContentSpan(cellText);
	errorWrapper.appendChild(span);

	if (errorMsg) {
		let errorSpan = createErrorSpan(errorMsg);
		errorWrapper.appendChild(span);
	}
	
	return td as HTMLTableCellElement;
}




export function updatePayloadTd(td: HTMLTableCellElement, cellText: string, errorMsg: string) {


	var contentSpanFieldInTd = td.querySelector(".tableCellContentSpan") as HTMLElement;
	var errorSpan = td.querySelector(".tableCellErrorSpan") as HTMLElement;

	if (errorMsg) {
		// When there is an error message, we do not update the payload data.
		if (errorSpan) {
			// There was an error displayed before.
			if (errorSpan.innerHTML !== errorMsg) {
				errorSpan.innerHTML = errorMsg;
			}
		} else {
			errorSpan = createErrorSpan(errorMsg);
			td.children[0].appendChild(errorSpan); // The child of the TD is assumed to be a DIV.
		}
	} else {
		if (errorSpan) {
			errorSpan.remove();
		}

		if (contentSpanFieldInTd.innerHTML !== cellText) {
			contentSpanFieldInTd.innerHTML = cellText;
		}
		let fieldInfo: FieldInfo = getFieldInfoFromTd(td);

		switch (fieldInfo.dataType) {
			case "LOCALDATETIME":
				let ldt: LocalDateTime = parseIsoDateTime(cellText);
				setTdBaseData_LocalDateTime(td, ldt);	
				// let tcd: LocalDateTime  = getTdBaseData_LocalDateTime(td);
				let dateStr = printDateDDMMYYYY(ldt.date);
				let timeStr = printTime24h(ldt.time);
			break;
			default: 
			setTdBaseData_string(td, cellText);
		}

	}
}



function createContentSpan(text: string) : HTMLSpanElement {
	let span = document.createElement("span");
	span.classList.add("tableCellContentSpan");
	span.innerHTML = text;
	return span;
}


function createErrorSpan(text: string) : HTMLSpanElement {
	let span = document.createElement("span");
	span.classList.add("tableCellErrorSpan");
	span.innerHTML = text;
	return span;
}



function setFocusOnTd(powerTable: Powertable, rowIdx: number, fieldName: string) {
	let td = powerTable.getTableCell(rowIdx, fieldName);
	td.focus();
}


export function setTableFocus(focusDef: TableFocusDef) {
	let powerTable = document.getElementById(focusDef.tagIdToFocus) as Powertable;
	setFocusOnTd(powerTable, focusDef.rowIdx, focusDef.fieldName);
}

