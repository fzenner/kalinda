import { CalendarPopup } from "./CalendarPopup";
import { CalendarPopupPartner } from "./CalendarPopupPartner";
import { SessionHandling } from "./jaccessEventHandling.js";
import { MsgPowerTable_CalendarPopupChange, CS_MESSAGE } from "./jointTypes";
import { MSG_HANDLER_HANDLE_POWERTABLE_ACTION, CalendarGuiDef } from "./messageTypes";
import { getTableFromChildElement, getTdFromChildElement, getTrFromChildElement, getRowIdxFromTr, getSymbolColId } from "./powerTableNavigation";


export class TableCalendarPopup extends CalendarPopup {

    static getTag() : string {
        return "kewebsi-table-calendar-popup"
    }

    static create(dateTimeEditorCompanion: CalendarPopupPartner, guiDef: CalendarGuiDef): TableCalendarPopup {
        const newEl = document.createElement(TableCalendarPopup.getTag()) as TableCalendarPopup;
        newEl.configure(dateTimeEditorCompanion, guiDef);
        return newEl;
    }

    override monthButtonClicked(dateTimeEditorCompanion: CalendarPopupPartner, nextMonth: boolean) {

        const editorTopDiv = dateTimeEditorCompanion.getRelatedEditor();
        const table = getTableFromChildElement(editorTopDiv);
        const td = getTdFromChildElement(editorTopDiv);
        const tr = getTrFromChildElement(td);
        const rowIdx = getRowIdxFromTr(tr);
        const symbolColId = getSymbolColId(td);

        const diffMonth = nextMonth ? 1 : -1;


        let msg: MsgPowerTable_CalendarPopupChange = {
            msgName: CS_MESSAGE,
            serverMsgHandler: MSG_HANDLER_HANDLE_POWERTABLE_ACTION,
            subCommand: "CALENDAR_POPUP_UPDATE",
            tableId: table.id,
            tdId: td.id,
            rowIdx: rowIdx,
            symbolColId: symbolColId,
            currentYear: this.displayedYear,
            currentMonth: this.displayedMonth,
            setYear: -1,
            diffYear: 0,
            diffMonth: diffMonth
        }


        let monthDownForCallback = (calendarGuiDef: CalendarGuiDef) => this.updateCalendarPopup(dateTimeEditorCompanion, calendarGuiDef);

        SessionHandling.ajaxCallWithCallback(msg, monthDownForCallback);
    }


    override selectYear(dateTimeEditorCompanion: CalendarPopupPartner, selectedYearStr: string) {

        const editorTopDiv = dateTimeEditorCompanion.getRelatedEditor();
        const table = getTableFromChildElement(editorTopDiv);
        const td = getTdFromChildElement(editorTopDiv);
        const tr = getTrFromChildElement(td);
        const rowIdx = getRowIdxFromTr(tr);
        const symbolColId = getSymbolColId(td);

        const selectedYear = parseInt(selectedYearStr);

        let msg: MsgPowerTable_CalendarPopupChange = {
            msgName: CS_MESSAGE,
            serverMsgHandler: MSG_HANDLER_HANDLE_POWERTABLE_ACTION,
            subCommand: "CALENDAR_POPUP_UPDATE",
            tableId: table.id,
            tdId: td.id,
            rowIdx: rowIdx,
            symbolColId: symbolColId,
            currentYear: this.displayedYear,
            currentMonth: this.displayedMonth,
            setYear: selectedYear,
            diffYear: 0,
            diffMonth: 0
        }


        let yearSelectForCallback = (calendarGuiDef: CalendarGuiDef) => this.updateCalendarPopup(dateTimeEditorCompanion, calendarGuiDef);

        SessionHandling.ajaxCallWithCallback(msg, yearSelectForCallback);
    }


    
}