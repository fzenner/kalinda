
import { KewebsiDateTimeEditor } from "./DateTimeEditor";
import { CalendarPopup as CalendarPopup } from "./CalendarPopup";
import { SessionHandling, warn } from "./jaccessEventHandling";
import { CalendarGuiDef } from "./messageTypes";
import { CalendarPopupPartner } from "./CalendarPopupPartner";
import { CS_MESSAGE, MsgCalendarEditorPopupChange } from "./jointTypes";



export class StandardCalendarPopup extends CalendarPopup {
    
    static getTag() : string {
        return "kewebsi-standard-calendar-popup"
    }

    static createCalendarPopup(dateTimeEditorCompanion: CalendarPopupPartner, guiDef: CalendarGuiDef): StandardCalendarPopup {
        const newEl = document.createElement(StandardCalendarPopup.getTag()) as StandardCalendarPopup;

        newEl.configure(dateTimeEditorCompanion, guiDef);
        return newEl;
    }


    override monthButtonClicked(dateTimeEditorCompanion: CalendarPopupPartner, nextMonth: boolean) {

        const relatedEditor = dateTimeEditorCompanion.getRelatedEditor();
        const diffMonth = nextMonth ? 1 : -1;

        let msg: MsgCalendarEditorPopupChange = {
            msgName: CS_MESSAGE,
            tagId: relatedEditor.id,
            serverMsgHandler: "SMH_handleGuiEvent",
            command: "CALENDAR_POPUP_UPDATE",
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

        const relatedEditor = dateTimeEditorCompanion.getRelatedEditor();
        const selectedYear = parseInt(selectedYearStr);

        let msg: MsgCalendarEditorPopupChange = {
            msgName: CS_MESSAGE,
            tagId: relatedEditor.id,
            serverMsgHandler: "SMH_handleGuiEvent",
            command: "CALENDAR_POPUP_UPDATE",
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

























// export function createCalendarPopupOld(dateTimeEditorCompanion: DateTimeEditorCompanion, guiDef: CalendarGuiDef) : HTMLDivElement {
//     dateTimeEditorCompanion.popupDisplayedYear = guiDef.year;
//     dateTimeEditorCompanion.popupDisplayedMonth = guiDef.month;

//     let centerChildrenParent = document.createElement("DIV") as HTMLDivElement;
//     centerChildrenParent.addEventListener("keydown", (e: KeyboardEvent) => {handleKeyDownOnPopupOuterDiv(e, dateTimeEditorCompanion) });
//     centerChildrenParent.id = "popupCenterChildrenParentId";
//     centerChildrenParent.classList.add("center-children-parent");
//     document.body.appendChild(centerChildrenParent);
//     dateTimeEditorCompanion.calendarPopup = centerChildrenParent;

//     let centerChildrenChild = createChildByTagName(centerChildrenParent, "div");
//     centerChildrenChild.classList.add("center-children-child");
        
//     let divForCloseX =  createChildByTagName(centerChildrenChild, "div");
//     divForCloseX.classList.add("div-for-close-x");

//     let cancelButtonX = createChildByTagName(divForCloseX,"div");
//     cancelButtonX.classList.add("close");
//     cancelButtonX.innerHTML = "&#x2716;"
//     preventTabBackward(cancelButtonX);
//     cancelButtonX.tabIndex = 0;

//     cancelButtonX.addEventListener("click", (e: MouseEvent) => {closePopup(dateTimeEditorCompanion) });

//     const buttonLeft = document.createElement("button");
//     buttonLeft.textContent = "<"
//     buttonLeft.addEventListener("click", (e: MouseEvent) => {monthButtonClicked(dateTimeEditorCompanion, false) });;
//     // buttonLeft.addEventListener("keydown", (e: KeyboardEvent) => {preventTab(e, true) });


//     const buttonRight = document.createElement("button");
//     buttonRight.textContent = ">";
//     buttonRight.addEventListener("click", (e: MouseEvent) => {monthButtonClicked(dateTimeEditorCompanion, true) });

//     const buttonLeftSpan = document.createElement("span");
//     buttonLeftSpan.appendChild(buttonLeft);
//     // buttonLeftSpan.tabIndex = 0;

//     const buttonRightSpan = document.createElement("span");
//     buttonRightSpan.appendChild(buttonRight);
//     // buttonRightSpan.tabIndex = 0;

//     const yearSelect = createYearSelectBox(dateTimeEditorCompanion, guiDef);
//     addIdClass(yearSelect, "yearSelect");
//     yearSelect.tabIndex = 0;

//     const monthSpan = document.createElement("span");
//     addIdClass(monthSpan, "monthSpan");
//     monthSpan.textContent = getMonthName(guiDef.month);
//     monthSpan.tabIndex = 0;

//     const navigationRow = createChildByTagName(centerChildrenChild, "div");
//     navigationRow.classList.add("div_calendarnav");

//     var divMonthAndYear = document.createElement("div");
//     divMonthAndYear.append(monthSpan, yearSelect);

//     navigationRow.append(buttonLeftSpan, divMonthAndYear, buttonRightSpan)
    
    
//     let divMainArea = createChildByTagName(centerChildrenChild,"div");
//     divMainArea.classList.add("calendar");

//     let [table, firstButton, lastButton] = createDayGridTable(dateTimeEditorCompanion, guiDef);
//     divMainArea.appendChild(table);

//     preventTabForward(lastButton);
   
//      // The placing information is optional
//     if (dateTimeEditorCompanion) {
        
//         // This defines the tag that needs to be placed underneath the given tag.
//         // let visiblePopupContentTag = document.getElementById(visiblePopupContentTagId);

//         let rect = dateTimeEditorCompanion.relElement.getBoundingClientRect();

//         centerChildrenChild.style.position = "fixed";
//         centerChildrenChild.style.top = rect.bottom.toString() + "px";
//         centerChildrenChild.style.left = rect.left.toString() + "px";
//     }

//     setSmartFocus(firstButton);

//     return centerChildrenParent;
// }


// function createDayGridTable(dateTimeEditor: DateTimeEditorCompanion, guiDef: CalendarGuiDef) : [HTMLTableElement, HTMLButtonElement, HTMLButtonElement] {
//     let table = document.createElement("table");
//     addIdClass(table, "dayGridTable");
//     let thead = table.createTHead();
//     fillThead(thead);
//     let tbody = table.createTBody();
//     let [firstButton, lastButton] = fillTbody(table.tBodies[0], dateTimeEditor, guiDef);
//     // setSmartFocus(firstButton);
//     // preventTabForward(lastButton);
//     return [table, firstButton, lastButton];
// }

// export function updateCalendarPopup(dateTimeEditorCompanion: DateTimeEditorCompanion, guiDef: CalendarGuiDef,) {

//     dateTimeEditorCompanion.popupDisplayedYear = guiDef.year;
//     dateTimeEditorCompanion.popupDisplayedMonth = guiDef.month;

//     const calendarPopup = dateTimeEditorCompanion.calendarPopup;

    
//     const yearSelect: HTMLSelectElement = findChildChildByIdClass(dateTimeEditorCompanion.calendarPopup,"yearSelect") as HTMLSelectElement;    
//     yearSelect.value = guiDef.year.toString();

//     const monthSpan = findChildChildByIdClass(dateTimeEditorCompanion.calendarPopup,"monthSpan") as HTMLSpanElement;
//     monthSpan.textContent = getMonthNameEN(guiDef.month);

//     const oldTable = findChildChildByIdClass(dateTimeEditorCompanion.calendarPopup,"dayGridTable") as HTMLSpanElement;

//     const [newTable, firstButton, lastButton] = createDayGridTable(dateTimeEditorCompanion, guiDef);
//     oldTable.replaceWith(newTable);
//     preventTabForward(lastButton);
// }


// function fillThead(thead : HTMLTableSectionElement)  {

//     let ce = document.createElement;
//     let trow = document.createElement("tr");
//     thead.appendChild(trow);

//     let th = document.createElement("th"); th.innerHTML = "Mo";
//     trow.appendChild(th);
//     th = document.createElement("th"); th.innerHTML = "Tu";
//     trow.appendChild(th);
//     th = document.createElement("th"); th.innerHTML = "We";
//     trow.appendChild(th);
//     th = document.createElement("th"); th.innerHTML = "Th";
//     trow.appendChild(th);
//     th = document.createElement("th"); th.innerHTML = "Fr";
//     trow.appendChild(th);
//     th = document.createElement("th"); th.innerHTML = "Sa";
//     trow.appendChild(th);
//     th = document.createElement("th"); th.innerHTML = "Su";
//     trow.appendChild(th);

// }


// function fillTbody(body: HTMLTableSectionElement, dateTimeEditor: DateTimeEditorCompanion, guiDef: CalendarGuiDef) : [HTMLButtonElement, HTMLButtonElement] {

//     let firstButton : HTMLButtonElement = null;
//     let lastButton : HTMLButtonElement = null;

//     for (let week of guiDef.days) {
//         let tr = body.insertRow(-1);  // -1 adds at the end
//         for (let dayOfMonthInWeekArray of week) {  // The day in week is a string representing the date in month, but it is in the Week
//             let td = document.createElement("td");
//             td.classList.add("calendar-td");
//             tr.appendChild(td);
//             let dayButton = document.createElement("button");
//             if (firstButton == null) {
//                 firstButton = dayButton;
//             }
//             lastButton = dayButton;

//             dayButton.tabIndex = 0;
//             td.appendChild(dayButton);
//             dayButton.classList.add("calendar-button");
//             dayButton.textContent =  dayOfMonthInWeekArray.day.toString();

//             dayButton.addEventListener("click", (e: MouseEvent) => {calendarDayButtonClicked(dateTimeEditor, dayOfMonthInWeekArray) });

//         }
//     } 

//     return [firstButton, lastButton]

//     function calendarDayButtonClicked(dateTimeEditorCompanion: DateTimeEditorCompanion, day: CalendarGuiDay) {
//         const dateString = getDateString(day)
//         dateTimeEditor.setDateRemoveDateError(dateString);
//         closePopup(dateTimeEditorCompanion);
//     }
// }


// function getDateString(day: CalendarGuiDay) {
//     return day.day + "." + day.month + "." +  day.year;
// }

// function closePopup(dateTimeEditor: DateTimeEditorCompanion) {
//     dateTimeEditor.calendarPopup.remove();

//     const timeEditor = dateTimeEditor.timeEditor;
//     timeEditor.focus();
//     // timeEditor.setSelectionRange(0, timeEditor.value.length);
//     selectAll(timeEditor);

//     dateTimeEditor.calendarPopup = null;
// }


// function createYearSelectBox(dateTimeEditorCompanion: DateTimeEditorCompanion, guiDef: CalendarGuiDef) {

//     const displayYear = guiDef.year;
//     const yearRangeStart = displayYear - 100;
//     const yearRangeEnd = displayYear + 100;

//     const selectBox = document.createElement("select");

//     selectBox.addEventListener("change", () => {selectYear(dateTimeEditorCompanion, selectBox.value) });

//     for (let i = yearRangeStart; i <= yearRangeEnd; i++) {

//         let optionElm = document.createElement("option");
// 		optionElm.value = i.toString();
// 		optionElm.text = optionElm.value;
//         if (i === displayYear) {
//             optionElm.setAttribute("selected", "");
//         }
// 		selectBox.appendChild(optionElm);
// 	}
//     return selectBox;
// }


function getMonthName(month:number) {
    return getMonthNameEN(month);
}

function getMonthNameEN(month:number) : string {
    switch(month) {
        case 1: return "January";
        case 2: return "February";
        case 3: return "March";
        case 4: return "April";
        case 5: return "May";
        case 6: return "June";
        case 7: return "July";
        case 8: return "August";
        case 9: return "September";
        case 10: return "October";
        case 11: return "November";
        case 12: return "December";
    warn("Illegal month:" + month);
    }
}

// function monthButtonClicked(dateTimeEditorCompanion: DateTimeEditorCompanion, nextMonth: boolean) {

//     const editorTopDiv = dateTimeEditorCompanion.relElement;
//     const table = getTableFromChildElement(editorTopDiv);
//     const td = getTdFromChildElement(editorTopDiv);
//     const tr = getTrFromChildElement(td);
//     const rowIdx = getRowIdxFromTr(tr);
//     const symbolColId = getSymbolColId(td);

//     const currentYear = dateTimeEditorCompanion.popupDisplayedYear;
//     const currentMonth = dateTimeEditorCompanion.popupDisplayedMonth;
//     const diffMonth = nextMonth ? 1 : -1;


//     let msg : MsgPowerTable_CalendarPopupChange = {
//         msgName: jointTypes.CS_MESSAGE, 
//         serverMsgHandler: MSG_HANDLER_HANDLE_POWERTABLE_ACTION,
//         subCommand: "CALENDAR_POPUP_UPDATE",
//         tableId: table.id,
//         tdId: td.id,
//         rowIdx: rowIdx,
//         symbolColId: symbolColId,
//         currentYear: currentYear,
//         currentMonth: currentMonth,
//         setYear: -1,
//         diffYear: 0, 
//         diffMonth: diffMonth
//     }


//     let monthDownForCallback = (calendarGuiDef: CalendarGuiDef) => updateCalendarPopup(dateTimeEditorCompanion, calendarGuiDef);

//     SessionHandling.ajaxCallWithCallback(msg, monthDownForCallback);	
// }

// function selectYear(dateTimeEditorCompanion: DateTimeEditorCompanion, selectedYearStr: string) {

//     const editorTopDiv = dateTimeEditorCompanion.relElement;
//     const table = getTableFromChildElement(editorTopDiv);
//     const td = getTdFromChildElement(editorTopDiv);
//     const tr = getTrFromChildElement(td);
//     const rowIdx = getRowIdxFromTr(tr);
//     const symbolColId = getSymbolColId(td);

//     const currentYear = dateTimeEditorCompanion.popupDisplayedYear;
//     const currentMonth = dateTimeEditorCompanion.popupDisplayedMonth;
    
//     const selectedYear = parseInt(selectedYearStr);


//     let msg : MsgPowerTable_CalendarPopupChange = {
//         msgName: jointTypes.CS_MESSAGE, 
//         serverMsgHandler: MSG_HANDLER_HANDLE_POWERTABLE_ACTION,
//         subCommand: "CALENDAR_POPUP_UPDATE",
//         tableId: table.id,
//         tdId: td.id,
//         rowIdx: rowIdx,
//         symbolColId: symbolColId,
//         currentYear: currentYear,
//         currentMonth: currentMonth,
//         setYear: selectedYear,
//         diffYear: 0, 
//         diffMonth: 0
//     }


//     let yearSelectForCallback = (calendarGuiDef: CalendarGuiDef) => updateCalendarPopup(dateTimeEditorCompanion, calendarGuiDef);

//     SessionHandling.ajaxCallWithCallback(msg, yearSelectForCallback);	
// }


// function handleKeyDownOnPopupOuterDiv(event: KeyboardEvent, comp: DateTimeEditorCompanion) { 
//     if (event.key==="Escape") {
//         closePopup(comp);
//     }
// }

// function preventTabForward(el: HTMLElement) {
//     el.addEventListener("keydown", (e: KeyboardEvent) => {preventTab(e, false) });
// }

// function preventTabBackward(el: HTMLElement) {
//     el.addEventListener("keydown", (e: KeyboardEvent) => {preventTab(e, true) });
// }


// function preventTab(event: KeyboardEvent, whenShiftKeyPressed: boolean) { 
//     if (event.key === "Tab") {
//         if (event.shiftKey === whenShiftKeyPressed) {
//            //  event.stopPropagation();
//             event.preventDefault();
//         }

//     }
// }