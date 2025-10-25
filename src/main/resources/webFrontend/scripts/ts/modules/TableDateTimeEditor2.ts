import { CalendarPopupPartner } from "./CalendarPopupPartner";
import { LocalDate, LocalTime } from "./dateTime";
import { createCalendarButton } from "./editorHelper";
import { ErrorDisplayCompanion } from "./ErrorDisplayCompanion";
import { SessionHandling } from "./jaccessEventHandling";
import { MsgPowerTable_CalendarPopupCreate } from "./jointTypes";
import * as jointTypes from "./jointTypes.js";
import { selectAll } from "./kewebsiUtils";
import { CalendarGuiDef, MSG_HANDLER_HANDLE_POWERTABLE_ACTION } from "./messageTypes";
import { compoundCellEditorFocusLostHandler, getWebComponent, keyDownDateTimeEditorInputField, unplaceCellEditorNew } from "./Powertable";
import { getRowIdxFromTr, getSymbolColId, getTableFromChildElement, getTdFromChildElement, getTrFromChildElement } from "./powerTableNavigation";
import { printDateDDMMYYYY, printTime24h } from "./stringUtils";
import { createCalendarPopup, isLocalDateTime, mouseDownCompoundCellEditor, parseInputOrSetDefaults, TableDataTypes } from "./TableDateTimeEditor";
import { TableEditor } from "./tableEditor";

const TABLE_DATE_TIME_EDITOR_CLASS = "tableDateTimeEditor";

export class TableDateTimeEditorCompanion extends TableEditor implements CalendarPopupPartner {

    static tag = "kalinda-table-datetimeeditor"
    isTableEditor = true;

    topElement: HTMLDivElement
    // replacingSpanFieldId: string;
    // oldValue: string;
    // restoreOldDataOnFocusLoss: boolean;
    // idPrefix: string



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

    constructor(replacingSpanFieldId: HTMLElement, oldValue: string, idPrefix: string, tableCellData: any) {
        super(replacingSpanFieldId, oldValue, TableDataTypes.LOCALDATETIME, idPrefix)
        this.tabIndex = -1
        this.topElement = document.createElement("div");
        this.topElement.tabIndex = -1
        this.topElement.classList.add(TABLE_DATE_TIME_EDITOR_CLASS)

        this.errorWrapper = document.createElement("div")
        this.errorWrapper.tabIndex = -1
        this.topElement.appendChild(this.errorWrapper);
        this.errorWrapper.classList.add("vertStretchDiv");

        this.restoreOldDataOnFocusLoss = false;
        let dateStr = "";
        let timeStr = "";

        if (tableCellData) {
            if (isLocalDateTime(tableCellData)) {
                this.dateEditorOldValue = tableCellData.date;
                this.timeEditorOldValue = tableCellData.time;
                dateStr = printDateDDMMYYYY(tableCellData.date);
                timeStr = printTime24h(tableCellData.time);
            } else {
                throw new Error("We do not expect strings as server data here.");
            }
        }


        this.dateEditor = createSubEditor(dateStr, 8);
        this.timeEditor = createSubEditor(timeStr, 4);
        this.calendarButton = createCalendarButton(this.idPrefix + "-button");

        const dateFieldButtonTimeFieldDiv = document.createElement("div");
        this.errorWrapper.appendChild(dateFieldButtonTimeFieldDiv);
        dateFieldButtonTimeFieldDiv.appendChild(this.dateEditor);
        dateFieldButtonTimeFieldDiv.appendChild(this.calendarButton);
        dateFieldButtonTimeFieldDiv.appendChild(this.timeEditor);

        this.setCompoundCellEditorEventHandlers();

    }

    connectedCallback() {
        this.appendChild(this.topElement)
    }




    getStringValue(): string {
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
        if (!this.dateErrorDisplayCompanion) {
            this.dateErrorDisplayCompanion = new ErrorDisplayCompanion(errorMsg, () => this.setOldDateData(), () => this.unplaceCellEditor());
            this.errorWrapper.appendChild(this.dateErrorDisplayCompanion.topElement);
        } else {
            this.dateErrorDisplayCompanion.errorMsgSpan.textContent = errorMsg;
        }
    }

    unplaceCellEditor() {
        let tableCellElement = getTdFromChildElement(this.topElement);
        unplaceCellEditorNew(this.replacingSpanField.id, this.oldValue, tableCellElement, this, false);
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
        if (!this.timeErrorDisplayCompanion) {
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



    syntaxErrorDisplayed(): boolean {
        if (this.dateErrorDisplayCompanion != null) return true;
        if (this.timeErrorDisplayCompanion != null) return true;
        return false;
    }

    serverErrorDisplayed(): boolean {
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


    sendEditorCreateRequestToServer() {
        let table = getTableFromChildElement(this);
        let td = getTdFromChildElement(this);
        let tr = getTrFromChildElement(td);
        let rowIdx = getRowIdxFromTr(tr);
        let symbolColId = getSymbolColId(td);

        let webCompId = getWebComponent(table).id;

        let date: LocalDate = parseInputOrSetDefaults(this, td);

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

        let createPopupCalendarForCallback = (calendarGuiDef: CalendarGuiDef) => createCalendarPopup(this, calendarGuiDef, "popupCalendarPrefix");

        SessionHandling.ajaxCallWithCallback(msg, createPopupCalendarForCallback);
    }

    override hasSyntacticError(): boolean {
        return false;
    }
}


console.log("fuck me twice!!!!")

// Register Web Component 
customElements.define(TableDateTimeEditorCompanion.tag, TableDateTimeEditorCompanion);


function createSubEditor(text: string, size: number) {
    let inputField = document.createElement("input")
    inputField.type = "text";
    inputField.setAttribute("size", size.toString());
    inputField.value = text;
    inputField.classList.add("textSubInput");
    return inputField;

}

function createPopupOnSpaceDown(dateTimeEditorCompanion: TableDateTimeEditorCompanion, ev: KeyboardEvent) {
    if (ev.key === " ") {
        dateTimeEditorCompanion.sendEditorCreateRequestToServer();
    }
}