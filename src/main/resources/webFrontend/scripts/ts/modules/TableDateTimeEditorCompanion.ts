import { LocalDate, LocalTime, LocalDateTime, createLocalDateTime } from "./dateTime"
import { TableEditorCompanion } from "./tableEditor"
import { compoundCellEditorFocusLostHandler, getWebComponent, keyDownDateTimeEditorInputField, unplaceCellEditor} from "./Powertable"
import {SessionHandling} from "./jaccessEventHandling";
import {MsgPowerTable_CalendarPopupCreate} from "./jointTypes";
import {CalendarGuiDef} from "./messageTypes"
import { getRowIdxFromTr, getTableFromChildElement, getTdFromChildElement, getTrFromChildElement, getColType, getSymbolColId } from "./powerTableNavigation";
import * as jointTypes from"./jointTypes.js";
import {MSG_HANDLER_HANDLE_POWERTABLE_ACTION} from "./messageTypes";
import { findFirstParentWithClass, selectAll } from "./kewebsiUtils";
import {isParsingError, parseGermanDate} from "./kewebsiDateUtils.js"
import { Companion, getCompanion } from "./Companion"; 
import { printDateDDMMYYYY, printTime24h } from "./stringUtils";
import {createCalendarButton} from "./editorHelper"
import { CalendarPopupPartner } from "./CalendarPopupPartner";
import { TableCalendarPopup } from "./TableCalendarPopup";
import { ErrorDisplayCompanion } from "./ErrorDisplayCompanion";

const TABLE_DATE_TIME_EDITOR_CLASS = "tableDateTimeEditor";

type TableCellData = {
    serverData: (string | LocalDateTime);
    unparseableData?: UnparseableData;
}

export function isLocalDateTime( tableCellServerData: string | LocalDateTime) : tableCellServerData is LocalDateTime {
    return (typeof(tableCellServerData) !== 'string');
}


type UnparseableData = {
    date: string
    time: string
}

    

export class TableDateTimeEditorCompanion extends TableEditorCompanion<HTMLDivElement> implements CalendarPopupPartner {
    errorWrapper: HTMLDivElement;
    dateEditor: HTMLInputElement;
    dateEditorOldValue: LocalDate;
    timeEditor: HTMLInputElement;
    timeEditorOldValue: LocalTime;
    calendarButton: HTMLElement;
    calendarPopup?: HTMLElement;
    dateErrorDisplayCompanion: ErrorDisplayCompanion;
    timeErrorDisplayCompanion: ErrorDisplayCompanion;
    errorDisplayElementServer?: HTMLSpanElement
    // popupDisplayedYear?: number;
    // popupDisplayedMonth?: number;

    constructor(el: HTMLDivElement, replacingSpanFieldId: string, oldValue: string) {
        super(el, replacingSpanFieldId, oldValue);
    }

    override getStringValue() : string {
        return this.dateEditor.value + " " + this.timeEditor.value;
    }

    getRelatedEditor(): HTMLElement {
        return this.topElement;
    }

    setDateRemoveDateError(localDate: LocalDate) {
        const dateStr = localDate ? printDateDDMMYYYY(localDate) : "";
        this.dateEditor.value = dateStr;
        this.dateEditorOldValue = localDate;
        this.removeErrorMessageDateIfExists();
    }


    setTimeRemoveTimeError(localTime: LocalTime) {
        const timeStr = localTime ? printTime24h(localTime) : "";
        this.timeEditor.value = timeStr;
        this.removeErrorMessageTimeIfExists();
    }

    setErrorMessageDate(errorMsg: string) {
        if (! this.dateErrorDisplayCompanion ) {
            this.dateErrorDisplayCompanion = new ErrorDisplayCompanion(errorMsg, () => this.setOldDateData(), () => this.unplaceCellEditor());
            this.errorWrapper.appendChild(this.dateErrorDisplayCompanion.topElement);
        } else {
            this.dateErrorDisplayCompanion.errorMsgSpan.textContent = errorMsg;
        }
    }

    unplaceCellEditor() {
        let tableCellElement = getTdFromChildElement(this.topElement);
        unplaceCellEditor(this.replacingSpanFieldId, this.oldValue, tableCellElement, this, false);
    }

    setOldDateData() {
        this.dateEditor.focus();
        selectAll(this.dateEditor);
        this.setDateRemoveDateError(this.dateEditorOldValue)
    }

    setOldTimeData() {
        this.timeEditor.focus();
        selectAll(this.timeEditor);
        this.setTimeRemoveTimeError(this.timeEditorOldValue)
    }

    removeTimeErrorAndFocus() {
        //
        // Attention! We need to move the focus away from the element
        // that we remove before we remove it. Otherwise we lose the focus to the next tabbable
        // element. That will cause a focusLost event on the surrounding editor
        // durig the execution of the remove function (!)
        //
        this.timeEditor.focus();
        this.removeErrorMessageTimeIfExists() 
        selectAll(this.timeEditor);
    }


    removeErrorMessageDateIfExists() {
        if (this.dateErrorDisplayCompanion) {
            this.dateErrorDisplayCompanion.remove();
            this.dateErrorDisplayCompanion = null;
        }
    }

    setErrorMessageTime(errorMsg: string) {
        if (! this.timeErrorDisplayCompanion ) {
            this.timeErrorDisplayCompanion = new ErrorDisplayCompanion(
                errorMsg, 
                () => this.setOldTimeData(),
                () => this.removeTimeErrorAndFocus()
            );
            this.errorWrapper.appendChild(this.timeErrorDisplayCompanion.topElement);
        } else {
            this.timeErrorDisplayCompanion.errorMsgSpan.textContent = errorMsg;
        }
    }

    removeErrorMessageTimeIfExists() {
        if (this.timeErrorDisplayCompanion) {
            this.timeErrorDisplayCompanion.remove();
            this.timeErrorDisplayCompanion = null;
        }
    }



    syntaxErrorDisplayed() : boolean {
        if (this.dateErrorDisplayCompanion != null) return true;
        if (this.timeErrorDisplayCompanion != null) return true;
        return false;
    }

    serverErrorDisplayed() : boolean {
        return (this.errorDisplayElementServer != null)
    }

    errorDisplayed() {
        if (this.syntaxErrorDisplayed) return true;
        if (this.serverErrorDisplayed) return true;
        return false;
    }

    setFocusAfterClosingPopup() {
        this.timeEditor.focus();
        selectAll(this.timeEditor);
    }

    closeCalendarPopup() {
        this.calendarPopup.remove();
        this.timeEditor.focus();
        selectAll(this.timeEditor);
        this.calendarPopup = null;
    }

    setCompoundCellEditorEventHandlers() {
        const topElement = this.topElement;
        topElement.addEventListener("mousedown", mouseDownCompoundCellEditor);
        topElement.addEventListener("focusout", compoundCellEditorFocusLostHandler);
        
        this.dateEditor.addEventListener("keydown", (ev: KeyboardEvent) => keyDownDateTimeEditorInputField(this, ev));
        this.timeEditor.addEventListener("keydown", (ev: KeyboardEvent) => keyDownDateTimeEditorInputField(this, ev));
    
        this.calendarButton.addEventListener("keydown", (ev: KeyboardEvent) => createPopupOnSpaceDown(this, ev));
    
    }




}



export function createTableDateTimeEditorWithCompanion(td: HTMLTableCellElement, idOfNewElement: string) : HTMLDivElement {

    const topElement = document.createElement("div");
    topElement.classList.add(TABLE_DATE_TIME_EDITOR_CLASS)
    topElement.id = idOfNewElement;

    var spanFieldInTd = td.querySelector(".tableCellContentSpan") as HTMLElement;

    const companion = new TableDateTimeEditorCompanion(topElement, spanFieldInTd.id, spanFieldInTd.textContent)

    companion.errorWrapper =  document.createElement("div");
    topElement.appendChild(companion.errorWrapper);
    companion.errorWrapper.classList.add("vertStretchDiv");

    // companion.dateEditor = null;
    // companion.timeEditor = null;

    let dateStr = "";
    let timeStr = "";

    if (hasUnparseableData(td)) {
        let unparseableData = getUnparseableData(td);
        companion.dateEditorOldValue = null;
        companion.timeEditorOldValue = null;
        dateStr = unparseableData.date;
        timeStr = unparseableData.time;
    } else {
        let tcd = getTableCellData(td);
        let serverData = tcd.serverData;

        if (isLocalDateTime(serverData)) {

            companion.dateEditorOldValue = serverData.date;
            companion.timeEditorOldValue = serverData.time;
            dateStr = printDateDDMMYYYY(serverData.date);
            timeStr =  printTime24h(serverData.time);

        } else {
            throw new Error("We do not expect strings as server data here.");
        }
    }

    companion.dateEditor = createSubEditor(dateStr, 8);
    companion.timeEditor = createSubEditor(timeStr, 4);

    companion.calendarButton = createCalendarButton();

    const dateFieldButtonTimeFieldDiv = document.createElement("div");
    companion.errorWrapper.appendChild(dateFieldButtonTimeFieldDiv);    
    dateFieldButtonTimeFieldDiv.appendChild(companion.dateEditor);
    dateFieldButtonTimeFieldDiv.appendChild(companion.calendarButton);
    dateFieldButtonTimeFieldDiv.appendChild(companion.timeEditor);

    companion.setCompoundCellEditorEventHandlers();

    return topElement;

}




function createPopupOnSpaceDown(dateTimeEditorCompanion: TableDateTimeEditorCompanion, ev: KeyboardEvent) {
    if (ev.key === " ") {
        sendEditorCreateRequestToServer(dateTimeEditorCompanion.topElement);
    }
}


function hasUnparseableData(td: HTMLTableCellElement) : boolean {
    let tableCellData = getTableCellData(td);
    if (tableCellData) {
        if (tableCellData.unparseableData) {
            return true;
        }
    }
    return false;
}

function getUnparseableData(td: HTMLTableCellElement) : UnparseableData {
    let tableCellData = getTableCellData(td);
    if (tableCellData) {
        if (tableCellData.unparseableData) { 
            return tableCellData.unparseableData;
        }
    }
    return null;
}

function getTableCellData(td: HTMLTableCellElement) : TableCellData {
    let tableCellData = td['tableCellData'] as TableCellData;
    return tableCellData;
}


function setTableCellData(td: HTMLTableCellElement, tableCellData: TableCellData) {
    td['tableCellData'] = tableCellData
}



/**
 * Create the property "serverData" to the TD element if it does not exist.
 * Set the value of that property to that string.
 * @param td 
 * @param text 
 * @returns 
 */
export function setTdBaseData_string(td: HTMLTableCellElement, text: string) {
    if (! getTableCellData(td)) {
        let tcd : TableCellData = {serverData: text}
        setTableCellData(td, tcd);
    } else {
        let tcd = getTableCellData(td);
        tcd.serverData = text;
        tcd.unparseableData = null;
    }
    return getTableCellData(td).serverData; 
}


export function setTdBaseData_LocalDateTime(td: HTMLTableCellElement, ldt: LocalDateTime) {
    if (! getTableCellData(td)) {
        let tcd : TableCellData = {serverData: ldt}
        setTableCellData(td, tcd);
    } else {
        let tcd = getTableCellData(td);
        tcd.serverData = ldt;
        tcd.unparseableData = null;
    }
    return getTableCellData(td).serverData;
}

export function getTdBaseData_LocalDateTime(td: HTMLTableCellElement) : LocalDateTime {
    return getTableCellData(td).serverData as LocalDateTime
}

export function getTdBaseData(td: HTMLTableCellElement, text: string) {
    return getTableCellData(td).serverData;
}


function createSubEditor(text: string, size: number)  {
    let inputField = document.createElement("input")
    inputField.type = "text";
    inputField.setAttribute("size", size.toString());
    inputField.value = text;
    inputField.classList.add("textSubInput");
    return inputField;

}


export function mouseDownCompoundCellEditor(event: MouseEvent) {
	console.log("mouseDownCompoundCellEditor");

	let target = event.target as HTMLElement;  // The clicked element.
	let editorTopDiv = event.currentTarget as HTMLElement;  // The element, whose event handler is invoked.
    const dateTimeEditorCompanion: TableDateTimeEditorCompanion = getCompanion(editorTopDiv) as TableDateTimeEditorCompanion;

	if (dateTimeEditorCompanion.calendarButton == target) {
		// The calendar button was clicked.
        sendEditorCreateRequestToServer(editorTopDiv);	
	} 

 	event.stopPropagation();
}




function sendEditorCreateRequestToServer(editorTopDiv: HTMLElement) {
    let table = getTableFromChildElement(editorTopDiv);
    let td = getTdFromChildElement(editorTopDiv);
    let tr = getTrFromChildElement(td);
    let rowIdx = getRowIdxFromTr(tr);
    let colType = getColType(td);
    let symbolColId = getSymbolColId(td);

    let webCompId = getWebComponent(table).id;;

    const dateTimeEditor: TableDateTimeEditorCompanion = getCompanion(editorTopDiv) as TableDateTimeEditorCompanion;

    let date: LocalDate = parseInputOrSetDefaults(dateTimeEditor, td);

    let msg: MsgPowerTable_CalendarPopupCreate = {
        msgName: jointTypes.CS_MESSAGE,
        serverMsgHandler: MSG_HANDLER_HANDLE_POWERTABLE_ACTION,
        subCommand: "CALENDAR_POPUP_CREATE",
        tableId: webCompId,
        tdId: td.id,
        rowIdx: rowIdx,
        symbolColId: symbolColId,
        year: date.year,
        month: date.month,
        day: date.day,
    };

    let createPopupCalendarForCallback = (calendarGuiDef: CalendarGuiDef) => createCalendarPopup(dateTimeEditor, calendarGuiDef);

    SessionHandling.ajaxCallWithCallback(msg, createPopupCalendarForCallback);
}

export function createCalendarPopup(dateTimeEditorCompanion: TableDateTimeEditorCompanion, guiDef: CalendarGuiDef) : TableCalendarPopup {
    const tableCalendarPopup =  TableCalendarPopup.create(dateTimeEditorCompanion, guiDef)
    document.body.appendChild(tableCalendarPopup);
    return tableCalendarPopup;

}

export function parseInputOrSetDefaults(dateTimeEditor: TableDateTimeEditorCompanion, td: HTMLTableCellElement) : LocalDate {
    let parsedDateOrError = parseGermanDate(dateTimeEditor.dateEditor.value);

    let date: LocalDate;


    if (isParsingError(parsedDateOrError)) {

        let tableCellData = getTableCellData(td);

        if (tableCellData !== null) {
            let serverDateTime: LocalDateTime = tableCellData.serverData as LocalDateTime;
            date = { year: serverDateTime.date.year, month: serverDateTime.date.month, day: serverDateTime.date.day };
        } else {
            const today = new Date();
            date = { year: today.getFullYear(), month: today.getMonth(), day: today.getDay() };
        }
    } else {
        date = { year: parsedDateOrError.year, month: parsedDateOrError.month, day: parsedDateOrError.day };
    }
    return date;
}

