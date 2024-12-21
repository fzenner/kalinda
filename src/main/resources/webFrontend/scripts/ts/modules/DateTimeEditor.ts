
import { CalendarGuiDef, DateTimeFieldGuiDef, GuiDef, InputFieldGuiDef } from "./messageTypes";
import { getPageName, selectAll } from "./kewebsiUtils";
import * as jointTypes from "./jointTypes.js";
import { SessionHandling } from "./jaccessEventHandling.js";
import { WebCompSupportingUpdates} from "./webcomps.js";
import { hasInfo, hasNoInfo, printDateDDMMYYYY, printTime24h, printDateTimeISO_DDMMYYYYTHHMMSS, isWellDefined, isNullOrUndefined } from "./stringUtils";
import { parseGermanDate, isParsingError, parseTime, parseIsoDateTime, dateTimeWireToLocalDateTime, localDateTimeToDateTimeWireOrNull } from "./kewebsiDateUtils";
import { createCalendarButton } from "./editorHelper";
import {LocalDate, LocalTime, LocalDateTime} from "./dateTime.js"
import { StandardCalendarPopup } from "./StandardCalendarPopup"
import { CalendarPopupPartner } from "./CalendarPopupPartner";
import { StyleManager } from "./StyleManager";
import { ErrorDisplayCompanion } from "./ErrorDisplayCompanion";
import { applyServerError, applyVisiblity, getColorForInputElementState } from "./kewebsiPageComposer";
import { InputElementStateInBrowser, InputElementStateInfoFromClientToServer, mapClientInputStateInBrowserToStateInfoForServer } from "./InputFieldStates";

// const DATE_TIME_EDITOR_CLASS = "textEditor";

// const CLIENT_INPUT_STATE_EMPTY = "EMPTY";
// const CLIENT_INPUT_STATE_INCOMPLETE = "INCOMPLETE";
// const CLIENT_INPUT_STATE_UNPARSEABLE = "UNPARSEABLE";
// const CLIENT_INPUT_STATE_OK = "OK";

type ElemenStateAndValue = {
    state: InputElementStateInBrowser;
    value: LocalDateTime;
}


// TODO: Unify with DateTimeEditorCompanion by moving stuff into a class (!) CalendarPopupPartner
export class KewebsiDateTimeEditor extends HTMLElement implements WebCompSupportingUpdates, CalendarPopupPartner {
    static tag = "kewebsi-date-time-edtior";
    
    topDiv: HTMLDivElement;
    errorSpan: HTMLSpanElement;


    errorWrapper: HTMLDivElement;
    dateEditor: HTMLInputElement;
    dateEditorLastParsedValueOrNull: LocalDate;
    timeEditor: HTMLInputElement;
    timeEditorLastParsedValueOrNull: LocalTime;
    calendarButton: HTMLElement;
    calendarPopup?: HTMLElement;
    dateErrorDisplayCompanion: ErrorDisplayCompanion;
    timeErrorDisplayCompanion: ErrorDisplayCompanion;
    overallComponentErrorDisplayCompanion: ErrorDisplayCompanion;
    errorDisplayElementServer?: HTMLSpanElement
    // popupDisplayedYear?: number;
    // popupDisplayedMonth?: number;

    // oldValueDate: LocalDate;
    // oldValueTime: LocalTime;
    restoreOldDataOnFocusLoss: boolean;

    disabled: boolean;

    required: boolean;

    inputElementState: InputElementStateInBrowser;

    public isDisabled() : boolean {
        return this.disabled;
    }

    public setDisabled(disabled: boolean) : void {
        this.disabled = disabled;
    }

    public isRequired() : boolean {
        return this.required;
    }

    public setRequired(required: boolean) : void {
        this.required = required;
    }

    

    defaultTime: LocalTime;


    static create(guiDef: GuiDef): KewebsiDateTimeEditor {

        const newEl = document.createElement(KewebsiDateTimeEditor.tag) as KewebsiDateTimeEditor;
        newEl.configure(guiDef)
        return newEl;
    }

    constructor() {
        super();
    }

    calculateFieldStateAndSetStyleAfterUpdateFromServer() : void {
        this.inputElementState = this.calculateInputElementStateAfterUpdateFromServer();
        this.setColorsForInputElementStateAfterServerUpdate();
    }

    setColorsForInputElementStateAfterServerUpdate() {
        switch (this.inputElementState) {
            case InputElementStateInBrowser.UNPARSEABLE: 
               // We want to show if an empty field is the cause for not transmittable in contrast to 
               // an unparseable field
               const errorColor = getColorForInputElementState(InputElementStateInBrowser.UNPARSEABLE)
               let missingFieldColor : string;

               if (this.isRequired()) {
                   missingFieldColor = getColorForInputElementState(InputElementStateInBrowser.EMPTY_REQUIRED);
               } else {
                   missingFieldColor =  getColorForInputElementState(InputElementStateInBrowser.EMPTY_OPTIONAL);
               }

               if (this.dateErrorDisplayCompanion) {
                    this.setDateEditorColor(errorColor)
               } else if (hasNoInfo(this.dateEditor.value)) {
                    this.setDateEditorColor(getColorForInputElementState(InputElementStateInBrowser.FILLED_OK))
               }

               if (this.timeErrorDisplayCompanion) {
                  this.setTimeEditorColor(errorColor)
               } else if (hasNoInfo(this.timeEditor.value)) {
                    this.setTimeEditorColor(getColorForInputElementState(InputElementStateInBrowser.FILLED_OK))
                }
                break;

            default:
                const overallColor = getColorForInputElementState(this.inputElementState);
                this.setDateEditorColor(overallColor);
                this.setTimeEditorColor(overallColor);
            }  
    }


    calculateInputElementStateAfterUpdateFromServer() : InputElementStateInBrowser {
        if (this.isDisabled()) {
            return InputElementStateInBrowser.DISABLED;
        }
        
        if (this.hasError()) {
            if (this.isClientSideError()) {
                return InputElementStateInBrowser.UNPARSEABLE
            } else {
                return  InputElementStateInBrowser.SERVER_SIDE_ERROR
            }
        }
        if (this.isEmpty()) {
            if (this.isRequired()) {
                return InputElementStateInBrowser.EMPTY_REQUIRED
            } else {
                return InputElementStateInBrowser.EMPTY_OPTIONAL
            }
        }

        if (this.isIncomplete()) {
            return InputElementStateInBrowser.INCOMPLETE;
        }

        return InputElementStateInBrowser.FILLED_OK;
    }

    setDateEditorColor(color: string) {
        this.dateEditor.style.backgroundColor = color;
    }

    setTimeEditorColor(color: string) {
        this.timeEditor.style.backgroundColor = color;
    }
  



    setDateRemoveDateError(date: LocalDate) {

       

        let dateString = "";
        if (date) {
            dateString = printDateDDMMYYYY(date);
        } 

        console.log("XXXPOS:setDateRemoveDateError:" + dateString)

        this.dateEditor.value = dateString;
        this.removeErrorMessageDateIfExists();

        if (hasNoInfo(this.timeEditor.value )) {
            console.log("XXXPOS2:setDateRemoveDateError:" + dateString)
            if (this.defaultTime) {
                console.log("XXXPOS3:setDateRemoveDateError:" + dateString)

                this.setTimeRemoveTimeError(this.defaultTime);
            }
        }
    }


    setTimeRemoveTimeError(localTime: LocalTime) {
        let timeString = "";
        if (localTime) {
            timeString = printTime24h(localTime)
        }
        this.timeEditor.value = timeString;
        this.removeErrorMessageTimeIfExists();
    }

    closeCalendarPopup(): void {
        this.calendarPopup.remove();
        this.timeEditor.focus();
        selectAll(this.timeEditor);
        this.calendarPopup = null;
    }
    getRelatedEditor(): HTMLElement {
        return this;
    }

    connectedCallback() {
        // const shadow = this.attachShadow({ mode: "open" }).appendChild(this.topDiv);
    }


    applyGuiDef(guiDef: GuiDef, createMode: boolean) {



        applyServerError(this, guiDef);
        applyVisiblity(this, guiDef);

        const dateTimeFieldGuiDef : DateTimeFieldGuiDef = guiDef.tagSpecificData as DateTimeFieldGuiDef;
        this.applyValue(dateTimeFieldGuiDef.dateTimeWireOrNull)
        
        if (isWellDefined(dateTimeFieldGuiDef.required)) {
            this.required = dateTimeFieldGuiDef.required;
        }

        if (isWellDefined(dateTimeFieldGuiDef.disabled)) {
            const valueToSet = dateTimeFieldGuiDef.disabled;
            this.disabled = valueToSet;
            this.dateEditor.disabled = valueToSet;
            this.timeEditor.disabled = valueToSet;
        }

        if (isWellDefined(dateTimeFieldGuiDef.defaultTimeWireOrNull)) {  // Data was received.
            if (isWellDefined(dateTimeFieldGuiDef.defaultTimeWireOrNull.value)) {  // The date is not null
                const defaultTimeWire = dateTimeFieldGuiDef.defaultTimeWireOrNull.value;
                this.defaultTime =  {
                    hour: defaultTimeWire.h,
                    minute: defaultTimeWire.mi,
                    second: defaultTimeWire.s,
                    nanosecond : defaultTimeWire.n
                }
            } else {  // We received a default time of null.
                this.defaultTime = null;
            }
        }

        this.calculateFieldStateAndSetStyleAfterUpdateFromServer();

    }

    /**
     * When newValue is null or undefined, the nothing is updated.
     * When vewValue.vlue is null, we set the value to null and the fields to empty strings.
     * Otherwise set the value to the year, month,... of the newValue.
     * @param newValue
     */
    applyValue(newValue: jointTypes.DateTimeWireOrNull) {
        if (newValue) {
            if (isWellDefined(newValue.value)) {
                let valueAslocalDateTime = dateTimeWireToLocalDateTime(newValue)
                this.dateEditor.value = printDateDDMMYYYY(valueAslocalDateTime.date);
                this.timeEditor.value = printTime24h(valueAslocalDateTime.time);
                this.dateEditorLastParsedValueOrNull = valueAslocalDateTime.date;
                this.timeEditorLastParsedValueOrNull = valueAslocalDateTime.time;
            } else {
                this.dateEditor.value = "";
                this.timeEditor.value = "";
                this.dateEditorLastParsedValueOrNull = null;
                this.timeEditorLastParsedValueOrNull = null;
            }
        }
    }

    configure(guiDef: GuiDef)  {

        this.id = guiDef.id;
        this.topDiv = document.createElement("div");
        this.topDiv.id = this.id + "-topdiv";
       

        this.errorWrapper =  document.createElement("div");
        this.topDiv.appendChild(this.errorWrapper);
        this.errorWrapper.classList.add("vertStretchDiv");

        this.dateEditor = this.createSubEditor("", 8);
        this.timeEditor = this.createSubEditor("", 4);

        // const defaultTimeWireOrNull = dateTimeFieldGuiDef.defaultTimeOrNull;
        // if (defaultTimeWireOrNull) {
        //     if (isWellDefined(defaultTimeWireOrNull.value)) {
        //         const defaultTimeWire = defaultTimeWireOrNull.value;
        //         this.defaultTimeWireOrNull =  {
        //             hour: defaultTimeWire.h,
        //             minute: defaultTimeWire.mi,
        //             second: defaultTimeWire.s,
        //             nanosecond : defaultTimeWire.n
        //         }
        //     }
        // }

    
        this.calendarButton = createCalendarButton();
    
        // A div that will contain the date field, the button, and the time field.
        const dateFieldButtonTimeFieldDiv = document.createElement("div");

        dateFieldButtonTimeFieldDiv.classList.add(StyleManager.horizontalDivFlowClass)

        dateFieldButtonTimeFieldDiv.setAttribute("display", "flex");
        dateFieldButtonTimeFieldDiv.setAttribute("flex-flow", "row");


        this.errorWrapper.appendChild(dateFieldButtonTimeFieldDiv);    
        dateFieldButtonTimeFieldDiv.appendChild(this.dateEditor);
        dateFieldButtonTimeFieldDiv.appendChild(this.calendarButton);
        dateFieldButtonTimeFieldDiv.appendChild(this.timeEditor);

        const shadow = this.attachShadow({ mode: "open" });
        const sheet = new CSSStyleSheet();

        const dateEditorStyle = StyleManager.inputFieldStyle + StyleManager.horizontalDivFlowStyle;
        sheet.replaceSync(dateEditorStyle);
        shadow.adoptedStyleSheets = [sheet];

        shadow.appendChild(this.topDiv); 
    
        this.setCompoundEventHandlers();

        // if (guiDef.errorInfo) {
        //     this.setErrorMessageFromServer(guiDef.errorInfo.errorText);
        // }

        this.applyGuiDef(guiDef, true);
    
    
    }

    setCompoundEventHandlers() {
        this.topDiv.addEventListener("mousedown", (ev: MouseEvent) => this.mouseDownCompoundCellEditor(ev));
        this.topDiv.addEventListener("focusout", (ev: FocusEvent) => this.focusLostHandler(ev));
        
        this.dateEditor.addEventListener("keydown", (ev: KeyboardEvent) => this.keyDownDateTimeEditorInputField(ev));
        this.timeEditor.addEventListener("keydown", (ev: KeyboardEvent) => this.keyDownDateTimeEditorInputField(ev));
    
        this.calendarButton.addEventListener("keydown", (ev: KeyboardEvent) => this.createPopupOnSpaceDown(ev));
    
    }

    createSubEditor(text: string, size: number)  {
        let inputField = document.createElement("input")
        inputField.type = "text";
        inputField.setAttribute("size", size.toString());
        inputField.value = text;
        inputField.classList.add("textSubInput");
        inputField.style.backgroundColor = "red";
        return inputField;
    
    }

    mouseDownCompoundCellEditor(event: MouseEvent) {
        let target = event.target as HTMLElement;  // The clicked element.
        if (this.calendarButton == target) {
            this.sendCalendarPopupRequestToServer();	
            event.stopPropagation();
        } 
         
    }

    sendCalendarPopupRequestToServer() {

        let date: LocalDate = this.parseInputOrSetDefaults();
    
    
        let msg: jointTypes.MsgCalendarEditorPopupCreate = {
            tagId: this.id,
            msgName: jointTypes.CS_MESSAGE,
            serverMsgHandler: "SMH_handleGuiEvent",
            command: "CALENDAR_POPUP_CREATE",
            year: date.year,
            month: date.month,
            day: date.day,
        };

        let createPopupCalendarForCallback = (calendarGuiDef: CalendarGuiDef) => this.createCalendarPopup(calendarGuiDef);
        SessionHandling.ajaxCallWithCallback(msg, createPopupCalendarForCallback);

    }

    createPopupOnSpaceDown(ev: KeyboardEvent) {
        if (ev.key === " ") {
            this.sendCalendarPopupRequestToServer();
        }
    }

    createCalendarPopup(guiDef: CalendarGuiDef) : StandardCalendarPopup {
        const standardCalendarPopup =  StandardCalendarPopup.createCalendarPopup(this, guiDef)
        document.body.appendChild(standardCalendarPopup);
        return standardCalendarPopup;
    }


    parseInputOrSetDefaults() : LocalDate {
        let parsedDateOrError = parseGermanDate(this.dateEditor.value);
        let date: LocalDate;
        if (isParsingError(parsedDateOrError)) {
            const today = new Date();
            date = { year: today.getFullYear(), month: today.getMonth(), day: today.getDay() };
        } else {
            date = { year: parsedDateOrError.year, month: parsedDateOrError.month, day: parsedDateOrError.day };
        }
        return date;
    }

    focusLostHandler(event: FocusEvent) {
        //
        // As long as there is a popup window open for this editor, we ignore the focus loss events.
        //
        if (this.calendarPopup) {
            return;
        }

        // TODO: XXXXXXXXXXXXXXXXXXX Handle the case when the value was not effectively modified.

        let leafElementThatWillReceiveFocus : HTMLElement = event.relatedTarget as HTMLElement;

        //
        // When we change the focus within descendants, we do nothing here. (The focus of the compountElement is not considered lost)
        //
        if (this.topDiv.contains(leafElementThatWillReceiveFocus)) {
            return;
        }

        //
        // If the input was lost because the cell editor has been programmatically removed, there is nothing to do.
        //
        if (this.parentElement == null) {
            return;
        }

        const dateInputString = this.dateEditor.value
        const timeInputString = this.timeEditor.value


        // !!!!!!!!!!!!!!!!!!! CONTINTUE HERE !!!!!!!!!!!!!!!!!!!!!!

        const stateAndVal : ElemenStateAndValue = this.calculateClientInputStateAndValue_setColorAndError_afterFocusLoss(dateInputString, timeInputString);

        const dateTimeWireOrNull = localDateTimeToDateTimeWireOrNull(stateAndVal.value)

        const inputElementStateInfoFromClientToServer = mapClientInputStateInBrowserToStateInfoForServer(stateAndVal.state);

        let pageName = getPageName(this);
        let msg: jointTypes.MsgDateTimeEntered = {
            msgName: jointTypes.CS_MESSAGE,
            serverMsgHandler: "SMH_dateTimeEntered",
            pageName: pageName,
            tagId: this.id,
            clientInputState: inputElementStateInfoFromClientToServer.toString(),
            value: dateTimeWireOrNull,
        };
        SessionHandling.ajaxCallStandard(msg);

    }



    /**
     * The server needs to know if we cannot send him the data (incomplete or unparseable).
     * Incomplete or unparseable is different from no data (empty, empty not required).
     * Empty will be transmitted as null value, the required option will be calculated by the server.
     * 
     * So we need to distinguish in general between client side error (incomplete, unparseable) or no 
     * client side error.
     * 
     * If the server responds on the transmission of the client side error, the client does not update an 
     * error markings (colors or error messages) since the client sets those before it sends the udpate.
     * 
     * THe server of course must not change requiredness or so when processing an client side error notification,
     * since, that would conflict with the errors on the client. 
     * 
     * So the server has two choices when getting a client side error notification:
     * 1.) Just remember the fact (until a not-client-error notification msg is received) , or 
     * 2.) Return a msg to the client that he should forget all errors, in which case all data needs to be set
     *     to the values sent by the server, otherwise client and server are out of sync, which we do a lot for
     *     to avoid in general. So opton 2 sucks.
     * @param dateInputString 
     * @param timeInputString 
     * @returns 
     */
    private calculateClientInputStateAndValue_setColorAndError_afterFocusLoss(dateInputString: string, timeInputString: string) : ElemenStateAndValue {
        this.removeErrorMessageDateIfExists();
        this.removeErrorMessageTimeIfExists();
        this.removeErrorMessageOverallComponentIfExists();

        var newValObj: LocalDateTime = null;

        //
        // Calculate clientInputState and the newValObj considering all corner cases.
        //

        if (this.isDisabled()) {
            return { state: InputElementStateInBrowser.DISABLED, value: null}
        }

        if (this.isEmpty()) {
            if (this.isRequired()) {
                return { state: InputElementStateInBrowser.EMPTY_REQUIRED, value: null};
            } else {
                return { state: InputElementStateInBrowser.EMPTY_OPTIONAL, value: null};
            }
        }

        if (hasNoInfo(dateInputString)) {
            this.setErrorMessageDate("Missing input");
            return { state: InputElementStateInBrowser.INCOMPLETE, value: null};
        }

        if (hasNoInfo(timeInputString)) {
            this.setErrorMessageTime("Missing input");
            return { state: InputElementStateInBrowser.INCOMPLETE, value: null};
        }

        var date: LocalDate = null;
        var time: LocalTime = null;
        let parsingErrorOccurred = false;
        let parsedDateOrError = parseGermanDate(this.dateEditor.value);
        if (isParsingError(parsedDateOrError)) {
            parsingErrorOccurred = true;
            this.setErrorMessageDate(parsedDateOrError.errorMessage);
        } else {
            date = parsedDateOrError as LocalDate;
        }

        if (!parsingErrorOccurred) {
            let parsedTimeOrError = parseTime(this.timeEditor.value);
            if (isParsingError(parsedTimeOrError)) {
                parsingErrorOccurred = true;
                this.setErrorMessageTime(parsedTimeOrError.errorMessage);
            } else {
                time = parsedTimeOrError as LocalTime;
            }
        }

        if (parsingErrorOccurred) {
            return {state: InputElementStateInBrowser.UNPARSEABLE, value: null};
        } 

        // When here, everything is OK.

        newValObj = { date: date as LocalDate, time: time };
        return {state: InputElementStateInBrowser.FILLED_OK, value: newValObj }
    }


    keyDownDateTimeEditorInputField(event: KeyboardEvent) {
        if (event.key === "Escape") {
            this.dateEditor.value = printDateDDMMYYYY(this.dateEditorLastParsedValueOrNull);
            this.timeEditor.value = printTime24h(this.timeEditorLastParsedValueOrNull);
        }
    }


    isEmpty() : boolean {
        if (hasInfo(this.dateEditor.value) )  return false;
        if (hasInfo(this.timeEditor.value) )  return false;
        return true;
    }

    isIncomplete() : boolean {
        let nbrOfFilledFields = 0;
        if (hasInfo(this.dateEditor.value) )  nbrOfFilledFields++;
        if (hasInfo(this.timeEditor.value) )  nbrOfFilledFields++;
        if (nbrOfFilledFields == 1) {
            return true;
        } 
        return false;
    }

    setErrorMessageDate(errorMsg: string) {
        if (! this.dateErrorDisplayCompanion ) {
            this.dateErrorDisplayCompanion = new ErrorDisplayCompanion(
                errorMsg,
                () => {this.setOldDateDataRemoveErrorAndFocus()} , 
                () => {this.removeDateErrorAndFocus()}
            );
            this.errorWrapper.appendChild(this.dateErrorDisplayCompanion.topElement);
        } else {
            this.dateErrorDisplayCompanion.errorMsgSpan.textContent = errorMsg;
        }
    }

    setErrorMessageTime(errorMsg: string) {
        if (!this.timeErrorDisplayCompanion) {
            this.timeErrorDisplayCompanion = new ErrorDisplayCompanion(
                errorMsg,
                () => { this.setOldTimeDataRemoveErrorAndFocus() },
                () => { this.removeTimeErrorAndFocus() }
            );
            this.errorWrapper.appendChild(this.timeErrorDisplayCompanion.topElement);
        } else {
            this.timeErrorDisplayCompanion.errorMsgSpan.textContent = errorMsg;
        }
    }

    setErrorMessageForOverallComponent(errorMsg: string) {
        if (! this.overallComponentErrorDisplayCompanion ) {
            this.overallComponentErrorDisplayCompanion = new ErrorDisplayCompanion(errorMsg, () => null, () => {this.removeServerErrorAndFocus()});
            this.errorWrapper.appendChild(this.overallComponentErrorDisplayCompanion.topElement);
        } else {
            this.overallComponentErrorDisplayCompanion.errorMsgSpan.textContent = errorMsg;
        }
    }

    removeErrorMessageDateIfExists() {
        if (this.dateErrorDisplayCompanion) {
            this.dateErrorDisplayCompanion.remove();
            this.dateErrorDisplayCompanion = null;
        }
    }

    removeErrorMessageTimeIfExists() {
        if (this.timeErrorDisplayCompanion) {
            this.timeErrorDisplayCompanion.remove();
            this.timeErrorDisplayCompanion = null;
        }
    }

    removeErrorMessageOverallComponentIfExists() {
        if (this.overallComponentErrorDisplayCompanion) {
            this.overallComponentErrorDisplayCompanion.remove();
            this.overallComponentErrorDisplayCompanion = null;
        }
    }

    public clearError() {
        this.removeErrorMessageDateIfExists();
        this.removeErrorMessageTimeIfExists();
        this.removeErrorMessageOverallComponentIfExists();
    }

    public hasError() {
        if (this.dateErrorDisplayCompanion) return true;
        if (this.timeErrorDisplayCompanion) return true;
        if (this.overallComponentErrorDisplayCompanion) return true;
        return false;
    }

    public isClientSideError() : boolean {
        if (this.dateErrorDisplayCompanion) return true;
        if (this.timeErrorDisplayCompanion) return true;
        return false;
    }

    public isServerSideError() : boolean {
        if (this.overallComponentErrorDisplayCompanion) return true;
        if (this.timeErrorDisplayCompanion) return true;
        return false;
    }



    removeDateErrorAndFocus() {
        //
        // Attention! We need to move the focus away from the element
        // that we remove before we remove it. Otherwise we lose the focus to the next tabbable
        // element. That will cause a focusLost event on the surrounding editor
        // durig the execution of the remove function (!)
        //
        this.dateEditor.focus();
        this.removeErrorMessageDateIfExists() 
        selectAll(this.dateEditor);
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

    removeServerErrorAndFocus() {
        this.dateEditor.focus();
        this.removeErrorMessageOverallComponentIfExists();
        selectAll(this.dateEditor);
    }


    setOldDateDataRemoveErrorAndFocus() {
        //
        // Attention! We need to move the focus away from the element
        // that we remove before we remove it. Otherwise we lose the focus to the next tabbable
        // element. That will cause a focusLost event on the surrounding editor
        // durig the execution of the remove function (!)
        //
        this.dateEditor.focus();
        this.setDateRemoveDateError(this.dateEditorLastParsedValueOrNull);
        selectAll(this.dateEditor);
    }

    setOldTimeDataRemoveErrorAndFocus() {
        //
        // Attention! We need to move the focus away from the element
        // that we remove before we remove it. Otherwise we lose the focus to the next tabbable
        // element. That will cause a focusLost event on the surrounding editor
        // durig the execution of the remove function (!)
        //
        this.timeEditor.focus();

        this.setTimeRemoveTimeError(this.timeEditorLastParsedValueOrNull);
        selectAll(this.timeEditor);
    }



    createErrorSpan(text: string) : HTMLSpanElement {
        let span = document.createElement("span");
        span.classList.add("inputFieldErrorSpan");
        span.innerHTML = text;
        return span;
    }

    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // We need to handle the case that the same server error persists after an update.
    // IN the moment, the server does not send an error message or clear,
    // It should send a new error message or something.
    // Also: Handle empty fields (especially when allowed)
    // Future: Warning functionality when unusual data is entered. / Confirmation message


    updateModifications(modifications: GuiDef) {
        this.applyGuiDef(modifications, false)
    }





    // changeEventHandler(event: Event) {
    //     let target = event.target as HTMLInputElement;

    //     // let serverSideId = target.getAttribute(jointTypes.SERVER_SIDE_ID_ATTR_NAME);

    //     let pageName = getPageName(target);

    //     let msg: jointTypes.MsgFieldDataEntered = {
    //         msgName: jointTypes.CS_MESSAGE,
    //         serverMsgHandler: "SMH_fieldDataEntered",
    //         pageName: pageName,
    //         tagId: this.id,
    //         // serverSideId: "-111",
    //         value: target.value
    //     };

    //     SessionHandling.ajaxCallStandard(msg);
    // }


}