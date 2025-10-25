import { LocalDate, LocalTime, LocalDateTime, createLocalDateTime } from "./dateTime"
import { CalendarGuiDef } from "./messageTypes"
import { getClosestAncestorByTag, selectAll } from "./kewebsiUtils";
import { isParsingError, parseGermanDate } from "./kewebsiDateUtils.js"
import { TableCalendarPopup } from "./TableCalendarPopup";
import { TableDateTimeEditorCompanion } from "./TableDateTimeEditor2.js";

export enum TableDataTypes {
    STRING,
    INTEGER,
    LOCALDATETIME,
    LOCALDATE,
    LOCALTIME,
    NUMBER,
    DECIMAL
}


type TableCellData = {
    serverData: (string | LocalDateTime);
    //   unparseableData?: UnparseableData;
}

export function isLocalDateTime(tableCellServerData: string | LocalDateTime): tableCellServerData is LocalDateTime {
    return (typeof (tableCellServerData) !== 'string');
}




type UnparseableData = {
    date: string
    time: string
}



console.log("fuck me!!!!")



export function createTableDateTimeEditorWithCompanion(td: HTMLTableCellElement, idOfNewElement: string): TableDateTimeEditorCompanion {

    var spanFieldInTd = td.querySelector(".tableCellContentSpan") as HTMLElement;
    let tcd = getTableCellData(td);

    const companion = new TableDateTimeEditorCompanion(spanFieldInTd, spanFieldInTd.textContent, idOfNewElement, tcd.serverData)

    return companion;

}







// function hasUnparseableData(td: HTMLTableCellElement) : boolean {
//     let tableCellData = getTableCellData(td);
//     if (tableCellData) {
//         if (tableCellData.unparseableData) {
//             return true;
//         }
//     }
//     return false;
// }

// function getUnparseableData(td: HTMLTableCellElement) : UnparseableData {
//     let tableCellData = getTableCellData(td);
//     if (tableCellData) {
//         if (tableCellData.unparseableData) { 
//             return tableCellData.unparseableData;
//         }
//     }
//     return null;
// }

function getTableCellData(td: HTMLTableCellElement): TableCellData {
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
    if (!getTableCellData(td)) {
        let tcd: TableCellData = { serverData: text }
        setTableCellData(td, tcd);
    } else {
        let tcd = getTableCellData(td);
        tcd.serverData = text;
    }
    return getTableCellData(td).serverData;
}


export function setTdBaseData_LocalDateTime(td: HTMLTableCellElement, ldt: LocalDateTime) {
    if (!getTableCellData(td)) {
        let tcd: TableCellData = { serverData: ldt }
        setTableCellData(td, tcd);
    } else {
        let tcd = getTableCellData(td);
        tcd.serverData = ldt;
    }
    return getTableCellData(td).serverData;
}

export function getTdBaseData_LocalDateTime(td: HTMLTableCellElement): LocalDateTime {
    return getTableCellData(td).serverData as LocalDateTime
}

export function getTdBaseData(td: HTMLTableCellElement, text: string) {
    return getTableCellData(td).serverData;
}





function haltHere() {
    const a = 1;
}


export function mouseDownCompoundCellEditor(event: MouseEvent) {
    console.log("mouseDownCompoundCellEditor");

    if (event.button != 0) {  // Only left button clicks are handled
        return;
    }

    let target = event.target as HTMLElement;  // The clicked element.
    let editorTopDiv = event.currentTarget as HTMLElement;  // The element, whose event handler is invoked.
    const dateTimeEditorCompanion: TableDateTimeEditorCompanion = getClosestAncestorByTag(TableDateTimeEditorCompanion.tag, editorTopDiv);

    if (dateTimeEditorCompanion.calendarButton == target) {
        // The calendar button was clicked.
        dateTimeEditorCompanion.sendEditorCreateRequestToServer();
    }

    event.stopPropagation();
}






export function createCalendarPopup(dateTimeEditorCompanion: TableDateTimeEditorCompanion, guiDef: CalendarGuiDef, idPrefix: string): TableCalendarPopup {
    const tableCalendarPopup = TableCalendarPopup.create(dateTimeEditorCompanion, guiDef, idPrefix)
    document.body.appendChild(tableCalendarPopup);
    return tableCalendarPopup;

}

export function parseInputOrSetDefaults(dateTimeEditor: TableDateTimeEditorCompanion, td: HTMLTableCellElement): LocalDate {
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

