import { ErrorDisplayCompanion } from "./ErrorDisplayCompanion";
import { getPageName, propertyExists, stringIsInt } from "./kewebsiUtils";
import { GuiDef, InputFieldGuiDef, InputFieldModifications } from "./messageTypes";
import { hasInfo, isWellDefined, isNullOrUndefined as stringIsNullOrUndefined, stringToEnum } from "./stringUtils";
import { StyleManager } from "./StyleManager";
import * as jointTypes from "./jointTypes.js";
import { SessionHandling } from "./jaccessEventHandling";
import { WebCompSupportingUpdates} from "./webcomps.js";
import { InputElementError, InputElementStateInBrowser, InputElementStateInfoFromClientToServer } from "./InputFieldStates";
import { applyVisiblity, getColorForInputElementState } from "./kewebsiPageComposer";
import { FieldType, mapClientInputStateInBrowserToStateInfoForServer } from "./InputFieldStates";


type ElemenStateAndValueStr = {
    state: InputElementStateInBrowser;
    value: string
}


export class KewebsiInputElement extends HTMLElement implements WebCompSupportingUpdates {

    static tag = "kewebsi-input";

    typeOfValue: FieldType

    topDiv: HTMLDivElement;
    inputEl: HTMLInputElement
    toolTip: HTMLSpanElement;
    errorDisplayCompanion: ErrorDisplayCompanion;
    lastKnownErrorFreeValue: string;
    required: boolean;
    lastValueSyncedWithServer: string
    private _inputElementState: InputElementStateInBrowser;
    error: InputElementError
    
    /**
     * The visibilty of an element is defined through the style attribute of the DOM element.
     * That is clumsy to deal with In order to have a direct and clear access to that property
     * we copy the visibilty here.
     */
    // visibilityCopy: string

    static create(guiDef: GuiDef): KewebsiInputElement {
        const newEl = document.createElement("kewebsi-input") as KewebsiInputElement;
        newEl.configure(guiDef)
        return newEl;
    }

    constructor() {
        super();
        this.topDiv = document.createElement("div");
        this.topDiv.classList.add("tooltip-anchor");
        this.inputEl = document.createElement("input");
        this.inputEl.classList.add("tooltip-anchor");
        this.topDiv.appendChild(this.inputEl);
        this.toolTip = document.createElement("span");
        this.toolTip.classList.add("tooltip");
        this.toolTip.textContent = "Unset"
        this.topDiv.appendChild(this.toolTip)
        this.lastKnownErrorFreeValue = "";
    }

    connectedCallback() {
        const shadowRoot = this.attachShadow({ mode: "open" });
        const sheet = new CSSStyleSheet();
        sheet.replaceSync(StyleManager.inputFieldStyle);
        shadowRoot.adoptedStyleSheets = [sheet];
        shadowRoot.appendChild(this.topDiv);
    }

    configure(guiDef: GuiDef) {
        this.id = guiDef.id;
        this.inputEl.id = this.id + "-core"


        this.applyGuiDef(guiDef, true);

        this.inputEl.addEventListener("focusout", (e: FocusEvent) => this.focusOutEventHandler(e));
        this.inputEl.addEventListener("focusin", (e: FocusEvent) => this.focusInEventHandler(e));

    }

    updateModifications(modifications: GuiDef) {
        this.applyGuiDef(modifications, false);
    }

    /**
     * A JavaScript object property can generally have one of three states
     * - Property has data.
     * - Property is null.
     * - Property does not exist.
     * Since we minimize the data send from the server to the client, null properties are not send. 
     * The property with null value on the Java side will not exist in JavaScript object. 
     * (One advantage of this approach is that it is easier to analyze the exchanged data, since all 
     * irrelevant data is filtered out by default)
     * When a null value needs to be sent, the DTO needs to have a property "wireNull".
     * (Because all other properties of that object will be null on the server side, only the wireNull property,
     * will be transmitted.)
     * When a property is null, that means, it exists but its value is null (it should not happen here, but might), 
     * it is dealt in the same was as a non-existant property: No change, especially no un-setting is applied.
     * The ratio behind it is: null means no information (as prescribed by the minimizing Java-JavaScript interface
     * strategy.)
     * Since the cases "property does not exist" and "property is null" are handled the same here, it suffices
     * to test for "if (myStruct.myProperty)", which results to false in both cases.
     * TODO: Move this insight into a general docu.
     */ 


    /**
     * In creation mode, non-existant parameter are set to a default value.
     * In update mode, non-existing parameter are not applied at all.
     * @param guiDef 
     * @param createMode true, when creating, false, when updateing
     * 
     */
    applyGuiDef(guiDef: GuiDef, createMode: boolean) {


        this.updateErrorGivenServerUpdate(this, guiDef);
        let inputFieldGuiDef: InputFieldGuiDef = guiDef.tagSpecificData;
        applyVisiblity(this, guiDef);

        this.typeOfValue = stringToEnum(FieldType, inputFieldGuiDef.typeOfValue.toString())

        //
        // We do not allow undefined or null as value for an input element.
        // Hence When we create it, we set the empty string in such cases.
        // Updates with null or undefined have no effect in accordance with the general strategy.
        // This way, we ensure that at creation time a string (possibly empty) is set and
        // a status is set in the following code.
        // In createMode, we set a an empty string as value, even if nothing specified. This forces
        // a clean intialization. In update mode, we do not set anyhthing if nothing is provided.
        //
        if (createMode) {
            if (isWellDefined(inputFieldGuiDef.value)) {
                if ( hasInfo(inputFieldGuiDef.value)) {
                    this.setValueInSyncWithServer(inputFieldGuiDef.value);
                } else { 
                    // This case is for better debugging
                    this.setValueInSyncWithServer(inputFieldGuiDef.value);  
                }                
            } else {
                this.setValueInSyncWithServer(inputFieldGuiDef.value);
            }
        } else {
            if (isWellDefined(inputFieldGuiDef.value)) {
                if ( hasInfo(inputFieldGuiDef.value)) {
                    this.setValueInSyncWithServer(inputFieldGuiDef.value);
                } else { 
                    // This case is for better debugging
                    this.setValueInSyncWithServer(inputFieldGuiDef.value);  
                }
            } 
        }

        if (isWellDefined(inputFieldGuiDef.required)) {
            this.required = inputFieldGuiDef.required;
        }

        if (isWellDefined(inputFieldGuiDef.disabled)) {
            this.inputEl.disabled = inputFieldGuiDef.disabled;
        }

        if (isWellDefined(inputFieldGuiDef.size)) {
            if (inputFieldGuiDef.size > 0) {   // TODO: Clean up the size 0 case also with the server side. (Server should not set size 0 for undefined size.)
                this.inputEl.size = inputFieldGuiDef.size;
            }
        }

        if (isWellDefined(inputFieldGuiDef.placeholder)) {
            this.inputEl.placeholder = inputFieldGuiDef.placeholder;
        }

        this.calculateFieldStateAfterUpdateFromServer();

        this.setStyleForFieldState();

    }

    private updateErrorGivenServerUpdate(elm: WebCompSupportingUpdates, guiDef: GuiDef) {
	
        const inputFieldGuiDef: InputFieldGuiDef = guiDef.tagSpecificData;
        if (isWellDefined(inputFieldGuiDef.disabled)) {
            if (inputFieldGuiDef.disabled) {
                if (this.hasError()) {
                    this.setValue("")  // We do not show values with errors  when disabled.
                }
                this.clearError()  // We also do not show and forget the errors when disabled.
            }
        }

        //
        // Errors are similar to data. When there is no change, nothing is being communicated.
        // "undefined" hence means "no change" - but not "no error"!
        //
        if (isWellDefined(guiDef.errorInfo)) {
            const errorInfo = guiDef.errorInfo;
            if (errorInfo) {
                if (errorInfo.wireNull) {
                    elm.clearError();
                } else {
                    if (!errorInfo.errorText) {  // if on string: false if empty, null or undefined
                        errorInfo.errorText = "Unspecified Error from Server"
                    }
                    elm.setErrorMessageForOverallComponent(errorInfo.errorText); 
                }
            } else {
                console.warn("Error info null received. Null properties are not expected to be received from the server");
            }
        } else {
            // If there is no error info from the server. If there are multiple fields connected to a single (server-side) page variable,
            // there might be the following situation:
            // here was a syntax error on field one.
            // The input was corrected in field two.
            // The server sends no error information, since the error was client-side.
            // The error on field one needs to be cleared, but is not, since no client side activity (change and focus loss) is done on field one.
            // The clearing of the error is done by recognizing that we recognize the following state:
            // - There is no server side error in the update
            // - There is a new value in the update (the framework must make sure, that the server does not send an updated value when 
            //   the request for the update at hand was the notification of a client side error)
            // - We have a client side error on the input field
            if (this.error) {
                if (this.error.isClientSideError) {
                    if (isWellDefined(inputFieldGuiDef.value)) {
                        this.clearError()
                    }
                }
            }
        }
    }

    public isDisabled() : boolean {
        return this.inputEl.disabled;
    }

    public isRequired() : boolean {
        return this.required;
    }

    public isEmpty() : boolean {
        if (! hasInfo(this.inputEl.value)) {
            return true;
        } else {
            return false;
        }
    }

    calculateFieldStateAfterUpdateFromServer() : void {
        if (this.isDisabled()) {
            this.inputElementState = InputElementStateInBrowser.DISABLED;
            return;
        }

        if (this.isEmpty()) {
            if (this.isRequired()) {
                this.inputElementState = InputElementStateInBrowser.EMPTY_REQUIRED
            } else {
                this.inputElementState = InputElementStateInBrowser.EMPTY_OPTIONAL
            }
            return;
        }
        
        if (this.hasError()) {
            if (this.error.isClientSideError) {
                this.inputElementState = InputElementStateInBrowser.UNPARSEABLE
            } else {
                this.inputElementState = InputElementStateInBrowser.SERVER_SIDE_ERROR
            }
            return;
        }

        this.inputElementState =InputElementStateInBrowser.FILLED_OK;
    }

    setStyleForFieldState() {
        const color = getColorForInputElementState(this.inputElementState)
        this.inputEl.style.backgroundColor = color;
        this.toolTip.textContent = this.inputElementState.toString();
    }


    private setValue(newValue: string) {
        this.inputEl.value = newValue; 
        if (! this.hasError()) {
            this.lastKnownErrorFreeValue = newValue;
        }
    }

    private setValueInSyncWithServer(newValue: string) {
        this.inputEl.value = newValue; 
        if (! this.hasError()) {
            this.lastKnownErrorFreeValue = newValue;
        }
        this.lastValueSyncedWithServer = newValue;
    }


    setErrorMessageForOverallComponent(errorMsg: string) {
        this.setError(errorMsg, false)
    }


    private setError(errorText: string, isClientSideError: boolean) {

        this.error = {errorText: errorText, isClientSideError: isClientSideError}
        this.inputEl.classList.add("textInputWithError");  // TODO this must work not only the first time (remove classes as well...)
        if (!this.errorDisplayCompanion) {
            const inputEl = this.inputEl;
            const oldValue = this.lastKnownErrorFreeValue;
            // this.errorSpan = HtmlErrorDisplay.create(errorTxt, () => KewebsiInputElement.setOldData(this));
            this.errorDisplayCompanion = new ErrorDisplayCompanion(
                this.error.errorText, 
                () => {this.handleErrorRevertButtonClick()}, 
                () => {this.handleErrorCloseButtonClick()}
                );
            this.topDiv.appendChild(this.errorDisplayCompanion.topElement);
        } else {
            this.errorDisplayCompanion.errorMsgSpan.innerHTML = errorText;
        }
    }


    private handleErrorRevertButtonClick() : void {
        this.clearError(); 
        this.setOldData(); 
        this.inputEl.focus()
    }

    private handleErrorCloseButtonClick() {
        this.clearError(); 
        this.inputEl.focus();
    }

    clearError() {
        this.inputEl.classList.add("textInput");
        if (this.errorDisplayCompanion) {
            this.errorDisplayCompanion.remove();
            this.errorDisplayCompanion = null;
        }
        this.error = null;
    }

    private hasError() {
        return isWellDefined(this.error);
    }

    setOldData() {
        this.inputEl.value = this.lastKnownErrorFreeValue;
    }

   

    createErrorSpan(text: string) : HTMLSpanElement {
        let span = document.createElement("span");
        span.classList.add("inputFieldErrorSpan");
        span.innerHTML = text;
        return span;
    }


    changeEventHandler(event: Event) {
        let target = event.target as HTMLInputElement;
        let newValue = target.value;
        let pageName = getPageName(target);

        const val : string = this.updateStateAndGetStringValueAfterClientSideChange(newValue)
        const inputElementStateInfoFromClientToServer = mapClientInputStateInBrowserToStateInfoForServer(this.inputElementState);

        this.sendUpdateToServer(newValue, pageName);

    }


    focusOutEventHandler(event: FocusEvent) { 
        const focusDetails = this.printFocusEvent(event);
        console.log("focusOut: " + focusDetails);

        const target = event.target as HTMLInputElement;
        const newValue = target.value;

        if (newValue !== this.lastValueSyncedWithServer) {
            this.lastValueSyncedWithServer = newValue;
            const pageName = getPageName(target);
            const val : string = this.updateStateAndGetStringValueAfterClientSideChange(newValue)
            const inputElementStateInfoFromClientToServer = mapClientInputStateInBrowserToStateInfoForServer(this.inputElementState);
            this.sendUpdateToServer(newValue, pageName);
        }

        

    }

    private sendUpdateToServer(newValue: string, pageName: string) {
        const inputElementStateInfoFromClientToServer : InputElementStateInfoFromClientToServer = mapClientInputStateInBrowserToStateInfoForServer(this.inputElementState);
        let msg: jointTypes.MsgFieldDataEntered = {
            msgName: jointTypes.CS_MESSAGE,
            serverMsgHandler: "SMH_fieldDataEntered",
            pageName: pageName,
            tagId: this.id,
            clientInputState: inputElementStateInfoFromClientToServer.toString(),
            value: newValue
        };

        SessionHandling.ajaxCallStandard(msg);
    }

    focusInEventHandler(event: FocusEvent) { 
        const focusDetails = this.printFocusEvent(event);
        console.log("focusIn: " + focusDetails);
    }

    printFocusEvent(event: FocusEvent) { 
        const target = event.target as HTMLElement;
        const targetString = target ? target.id : "null";
        const currentTarget = event.currentTarget as HTMLElement;
        const currentTargetStdring = currentTarget ? currentTarget.id : "null";
        const relatedTarget = event.relatedTarget as HTMLElement
        const relatedTargetString = relatedTarget? relatedTarget.id : "null"

        const out = "target:" + targetString + " currentTarget: " + currentTargetStdring + " relatedTarget: " + relatedTargetString;
        return out;
    }


    private updateStateAndGetStringValueAfterClientSideChange(newValue: string) : string {
        
        var newValObj: string = null;

        //
        // Calculate clientInputState and the newValObj considering all corner cases.
        //

        if (this.isDisabled()) {
            this.inputElementState = InputElementStateInBrowser.DISABLED
            return null;
        }

        if (this.isEmpty()) {
            if (this.isRequired()) {
                this.inputElementState = InputElementStateInBrowser.EMPTY_REQUIRED
                return null;
            } else {
                this.inputElementState = InputElementStateInBrowser.EMPTY_OPTIONAL
                return null;
            }
        }


        let parsingErrorMsg = this.clientSideValidation(newValue);

        if (parsingErrorMsg) {
            this.setClientSideError(parsingErrorMsg);
            this.inputElementState = InputElementStateInBrowser.UNPARSEABLE
            return null
        } else {
            if (this.hasError()) {
                if (this.error.isClientSideError) {
                    this.clearError();
                }
            }
        }

         // When here, everything is OK.
        newValObj = newValue.trim()
        this.inputElementState = InputElementStateInBrowser.FILLED_OK
        return newValObj
    }

    clientSideValidation(newValue: string) : string {
        switch(this.typeOfValue) {
            case FieldType.STR: return null;
            case FieldType.INT: {
                if (! stringIsInt(newValue)) {
                    return "String is not an integer."
                }
                return null;
            };
            default: console.log("Unimplemented FieldType in clientSideValidation")
            return null;
        }
    }

    setClientSideError(errorMsg: string) {
        this.setError(errorMsg, true)
    }


    removeErrorMessageIfExists() {
        if (this.errorDisplayCompanion) {
            this.errorDisplayCompanion.remove();
            this.errorDisplayCompanion = null;
        }
    }    

    public get inputElementState() :InputElementStateInBrowser {
        return this._inputElementState;
    }

    public set inputElementState(newState: InputElementStateInBrowser) {
        this._inputElementState = newState;

        // We copy the state as an HTML attribute. This is done to support automatic testing.
        let stateStr : string = this.inputElementState;
        this.setAttribute("logicalstate", stateStr)

        const check = this.getAttribute("logicalstate");
        console.log("XXXcheck: " + check);
    }    
    
}