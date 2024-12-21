import { GuiDef, MSG_HANDLER_HANDLE_POWERTABLE_ACTION } from "./messageTypes";
import { TableDateTimeEditorCompanion } from "./TableDateTimeEditorCompanion";
import { CalendarGuiDef, CalendarGuiDay } from "./messageTypes";
import { createChildByTagName } from "./kewebsiPageComposer";
import { addIdClass, findChildChildByIdClass } from "./kewebsiPageComposer";
import { setSmartFocus } from "./kewebsiUtils";
import { selectAll } from "./kewebsiUtils";
import { getTableFromChildElement, getTdFromChildElement, getTrFromChildElement, getRowIdxFromTr, getSymbolColId } from "./powerTableNavigation";
import { warn } from "./jaccessEventHandling";
import { MsgPowerTable_CalendarPopupChange } from "./jointTypes.js";
import { SessionHandling } from "./jaccessEventHandling";
import { CalendarPopupPartner } from "./CalendarPopupPartner";
import { LocalDate } from "./dateTime";




export abstract class CalendarPopup extends HTMLElement {

    //static tag = "kewebsi-calendar-popup";
    static getTag() : string {
        throw new Error("CalendarPopup.getTag must be overloaded");
    }

    static styleStr =
        ".center-children-parent { "
        + "display: flex;"
        + "align-items: center;"
        + "justify-content: center;  "
        + "position: fixed;" /* Stay in place */
        + "z-index: 1;" /* Sit on top */
        + "left: 0;"
        + "top: 0;"
        + " width: 100%; /* Full width */"
        + "height: 50%;"
        + "overflow: auto;" /* Enable scroll if needed */
        + "} \n"

        + ".center-children-child {"
        + "background-color: #fefefe;"
        + "padding: 20px;"
        + "border: 1px solid #888;"
        + "} \n"



        + ".calendar-content {"
        + " background-color: #fefefe;"
        + "margin: 0px ;"
        + "padding: 2px;"
        + "border: 1px solid #888;"
        + "grid-template-columns:  min-content;"
        + "} \n"


        + ".close {"
        + "color: #aaa;"
        + "float: right;"
        + "font-size: 20px;"
        + "line-height: 0.8;"
        + "font-weight: bold;"
        + "} \n"

        + ".close:hover,"
        + ".close:focus {"
        + "color: black;"
        + "text-decoration: none;"
        + "cursor: pointer;"
        + "} \n"

        + ".close:active {"
        + "background-color: grey;"
        + "outline-width: 3px;"
        + "outline-style:  solid;"
        + "outline-color: rgba(80, 80, 80, 0.548);"
        + "} \n"

        + ".close-button {"
        + "color: #aaa;"
        + "font-size: 28px;"
        + "font-weight: bold;"
        + "}\n"

        + ".div-for-close-x {"
        + "justify-content: right;"
        + "display: flex;"
        + "padding: 0.2em;"
        + "} \n"


        /** Horizontal calendar navigation flow */
        + ".div_calendarnav {"
        + "display: flex;"
        + "justify-content: space-between;"
        + "} \n"

        + ".calendar-launch-button {"
        + " border: none;"
        + "padding-top: 0px;"
        + "padding-bottom: 0px;"
        + "font-size: 12px;"
        + "} \n"


    focusWhenPoppedUp: HTMLElement;


    /**
     * This element is the topDiv. It spans the whole window with a transparent layer
     * and positions its children in the center or as defined.
     */
    centerChildrenParent: HTMLDivElement;
    yearSelect: HTMLSelectElement;
    monthSpan: HTMLSpanElement;
    dayGridTable: HTMLTableElement;
    displayedYear: number;
    displayedMonth: number;


    // static create(dateTimeEditorCompanion: CalendarPopupPartner, guiDef: CalendarGuiDef): CalendarPopup {
    //     const newEl = document.createElement(CalendarPopup.getTag()) as CalendarPopup;

    //     newEl.configure(dateTimeEditorCompanion, guiDef);
    //     return newEl;
    // }


    configure(dateTimeEditorCompanion: CalendarPopupPartner, guiDef: CalendarGuiDef): void {

        this.centerChildrenParent = document.createElement("DIV") as HTMLDivElement;
        const centerChildrenParent = this.centerChildrenParent;
        this.appendChild(centerChildrenParent);

        this.displayedYear = guiDef.year;
        this.displayedMonth = guiDef.month;

        centerChildrenParent.addEventListener("keydown", (e: KeyboardEvent) => { this.handleKeyDownOnPopupOuterDiv(e, dateTimeEditorCompanion) });
        centerChildrenParent.id = "popupCenterChildrenParentId";
        centerChildrenParent.classList.add("center-children-parent");
        dateTimeEditorCompanion.calendarPopup = centerChildrenParent;

        let centerChildrenChild = createChildByTagName(centerChildrenParent, "div");
        centerChildrenChild.classList.add("center-children-child");

        let divForCloseX = createChildByTagName(centerChildrenChild, "div");
        divForCloseX.classList.add("div-for-close-x");

        let cancelButtonX = createChildByTagName(divForCloseX, "div");
        cancelButtonX.classList.add("close");
        cancelButtonX.innerHTML = "&#x2716;"
        this.preventTabBackward(cancelButtonX);
        cancelButtonX.tabIndex = 0;

        // cancelButtonX.addEventListener("click", (e: MouseEvent) => { CalendarPopup.closePopup(dateTimeEditorCompanion) });
        cancelButtonX.addEventListener("click", (e: MouseEvent) => { dateTimeEditorCompanion.closeCalendarPopup() });

        const buttonLeft = document.createElement("button");
        buttonLeft.textContent = "<"
        buttonLeft.addEventListener("click", (e: MouseEvent) => { this.monthButtonClicked(dateTimeEditorCompanion, false) });;
        // buttonLeft.addEventListener("keydown", (e: KeyboardEvent) => {preventTab(e, true) });


        const buttonRight = document.createElement("button");
        buttonRight.textContent = ">";
        buttonRight.addEventListener("click", (e: MouseEvent) => { this.monthButtonClicked(dateTimeEditorCompanion, true) });

        const buttonLeftSpan = document.createElement("span");
        buttonLeftSpan.appendChild(buttonLeft);
        // buttonLeftSpan.tabIndex = 0;

        const buttonRightSpan = document.createElement("span");
        buttonRightSpan.appendChild(buttonRight);
        // buttonRightSpan.tabIndex = 0;

        this.yearSelect = this.createYearSelectBox(dateTimeEditorCompanion, guiDef);
        addIdClass(this.yearSelect, "yearSelect");
        this.yearSelect.tabIndex = 0;

        this.monthSpan = document.createElement("span");
        addIdClass(this.monthSpan, "monthSpan");
        this.monthSpan.textContent = this.getMonthName(guiDef.month);
        this.monthSpan.tabIndex = 0;

        const navigationRow = createChildByTagName(centerChildrenChild, "div");
        navigationRow.classList.add("div_calendarnav");

        var divMonthAndYear = document.createElement("div");
        divMonthAndYear.append(this.monthSpan, this.yearSelect);

        navigationRow.append(buttonLeftSpan, divMonthAndYear, buttonRightSpan)


        let divMainArea = createChildByTagName(centerChildrenChild, "div");
        divMainArea.classList.add("calendar");

        let [table, firstButton, lastButton] = this.createDayGridTable(dateTimeEditorCompanion, guiDef);
        this.dayGridTable = table;
        this.focusWhenPoppedUp = firstButton;
        divMainArea.appendChild(this.dayGridTable);

        this.preventTabForward(lastButton);

        // The placing information is optional
        if (dateTimeEditorCompanion) {

            // This defines the tag that needs to be placed underneath the given tag.
            // let visiblePopupContentTag = document.getElementById(visiblePopupContentTagId);

            let rect = dateTimeEditorCompanion.getRelatedEditor().getBoundingClientRect();

            centerChildrenChild.style.position = "fixed";
            centerChildrenChild.style.top = rect.bottom.toString() + "px";
            centerChildrenChild.style.left = rect.left.toString() + "px";
        }


    }

    constructor() {
        super();

    }

    connectedCallback() {
        const shadowRoot = this.attachShadow({ mode: "open" })
        const sheet = new CSSStyleSheet();
        sheet.replaceSync(CalendarPopup.styleStr);
        shadowRoot.adoptedStyleSheets = [sheet];
        shadowRoot.appendChild(this.centerChildrenParent);
        setSmartFocus(this.focusWhenPoppedUp);
    }




    createDayGridTable(dateTimeEditor: CalendarPopupPartner, guiDef: CalendarGuiDef): [HTMLTableElement, HTMLButtonElement, HTMLButtonElement] {
        let table = document.createElement("table");
        addIdClass(table, "dayGridTable");
        let thead = table.createTHead();
        this.fillThead(thead);
        let tbody = table.createTBody();
        let [firstButton, lastButton] = this.fillTbody(table.tBodies[0], dateTimeEditor, guiDef);
        // setSmartFocus(firstButton);
        // preventTabForward(lastButton);
        return [table, firstButton, lastButton];
    }

    updateCalendarPopup(dateTimeEditorCompanion: CalendarPopupPartner, guiDef: CalendarGuiDef,) {

        this.displayedYear = guiDef.year;
        this.displayedMonth = guiDef.month;

        this.yearSelect.value = guiDef.year.toString();

        this.monthSpan.textContent = this.getMonthNameEN(guiDef.month);

        const [newTable, firstButton, lastButton] = this.createDayGridTable(dateTimeEditorCompanion, guiDef);
        this.dayGridTable.replaceWith(newTable);
        this.dayGridTable = newTable;
        this.preventTabForward(lastButton);
    }


    fillThead(thead: HTMLTableSectionElement) {

        let ce = document.createElement;
        let trow = document.createElement("tr");
        thead.appendChild(trow);

        let th = document.createElement("th"); th.innerHTML = "Mo";
        trow.appendChild(th);
        th = document.createElement("th"); th.innerHTML = "Tu";
        trow.appendChild(th);
        th = document.createElement("th"); th.innerHTML = "We";
        trow.appendChild(th);
        th = document.createElement("th"); th.innerHTML = "Th";
        trow.appendChild(th);
        th = document.createElement("th"); th.innerHTML = "Fr";
        trow.appendChild(th);
        th = document.createElement("th"); th.innerHTML = "Sa";
        trow.appendChild(th);
        th = document.createElement("th"); th.innerHTML = "Su";
        trow.appendChild(th);

    }


    fillTbody(body: HTMLTableSectionElement, dateTimeEditor: CalendarPopupPartner, guiDef: CalendarGuiDef): [HTMLButtonElement, HTMLButtonElement] {

        let firstButton: HTMLButtonElement = null;
        let lastButton: HTMLButtonElement = null;

        for (let week of guiDef.days) {
            let tr = body.insertRow(-1);  // -1 adds at the end
            for (let dayOfMonthInWeekArray of week) {  // The day in week is a string representing the date in month, but it is in the Week
                let td = document.createElement("td");
                td.classList.add("calendar-td");
                tr.appendChild(td);
                let dayButton = document.createElement("button");
                if (firstButton == null) {
                    firstButton = dayButton;
                }
                lastButton = dayButton;

                dayButton.tabIndex = 0;
                td.appendChild(dayButton);
                dayButton.classList.add("calendar-button");
                dayButton.textContent = dayOfMonthInWeekArray.day.toString();

                dayButton.addEventListener("click", (e: MouseEvent) => { calendarDayButtonClicked(dateTimeEditor, dayOfMonthInWeekArray) });

            }
        }

        return [firstButton, lastButton]

        function calendarDayButtonClicked(dateTimeEditorCompanion: CalendarPopupPartner, day: CalendarGuiDay) {
            console.log("XXX calendarDayButtonClicked")
            const dateString = CalendarPopup.getDateString(day)
            const localDate : LocalDate = {year: day.year, month: day.month, day: day.day};
            console.log("XXX localDate:" + localDate.day + "|" + localDate.month)

            dateTimeEditor.setDateRemoveDateError(localDate);
            // CalendarPopup.closePopup(dateTimeEditorCompanion);
            dateTimeEditorCompanion.closeCalendarPopup();
        }
    }


    static getDateString(day: CalendarGuiDay) {
        return day.day + "." + day.month + "." + day.year;
    }

    static closePopup(dateTimeEditor: CalendarPopupPartner) {
        dateTimeEditor.closeCalendarPopup();
    }


    createYearSelectBox(dateTimeEditorCompanion: CalendarPopupPartner, guiDef: CalendarGuiDef) {

        const displayYear = guiDef.year;
        const yearRangeStart = displayYear - 100;
        const yearRangeEnd = displayYear + 100;

        const selectBox = document.createElement("select");

        selectBox.addEventListener("change", () => { this.selectYear(dateTimeEditorCompanion, selectBox.value) });

        for (let i = yearRangeStart; i <= yearRangeEnd; i++) {

            let optionElm = document.createElement("option");
            optionElm.value = i.toString();
            optionElm.text = optionElm.value;
            if (i === displayYear) {
                optionElm.setAttribute("selected", "");
            }
            selectBox.appendChild(optionElm);
        }




        return selectBox;
    }


    getMonthName(month: number) {
        return this.getMonthNameEN(month);
    }

    getMonthNameEN(month: number): string {
        switch (month) {
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
            default: warn("Illegal month:" + month);
        }
    }

    abstract monthButtonClicked(dateTimeEditorCompanion: CalendarPopupPartner, nextMonth: boolean)
 
    abstract selectYear(dateTimeEditorCompanion: CalendarPopupPartner, selectedYearStr: string);

    handleKeyDownOnPopupOuterDiv(event: KeyboardEvent, comp: CalendarPopupPartner) {
        if (event.key === "Escape") {
            // CalendarPopup.closePopup(comp);
            comp.closeCalendarPopup();
        }
    }

    preventTabForward(el: HTMLElement) {
        el.addEventListener("keydown", (e: KeyboardEvent) => { this.preventTab(e, false) });
    }

    preventTabBackward(el: HTMLElement) {
        el.addEventListener("keydown", (e: KeyboardEvent) => { this.preventTab(e, true) });
    }


    preventTab(event: KeyboardEvent, whenShiftKeyPressed: boolean) {
        if (event.key === "Tab") {
            if (event.shiftKey === whenShiftKeyPressed) {
                //  event.stopPropagation();
                event.preventDefault();
            }

        }

    }

}