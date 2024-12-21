import { GuiDef, InputFieldGuiDef, InputFieldModifications } from "./messageTypes";
import { KewebsiDateTimeEditor } from "./DateTimeEditor";
import { TableCalendarPopup } from "./TableCalendarPopup";
import { StandardCalendarPopup } from "./StandardCalendarPopup";
import { KewebsiInputElement } from "./KewebsiInputElement";

import { Powertable } from "./Powertable";


export class MyFirstWebComp extends HTMLElement {
    constructor() {
        super();
        const spanEl = document.createElement("span");
        spanEl.textContent = "Hello World, I am a WebComponent"
        this.attachShadow({ mode: 'open' }).appendChild(spanEl)

    }
}

export interface WebCompSupportingUpdates {
    updateModifications(modifications: GuiDef): void;
    setErrorMessageForOverallComponent(errorMsg: string);  // TODO: Consider to rename to setServerErrorMsg
    clearError();
  }



export function defineWebComponents() {

    customElements.define("my-first-webcomp", MyFirstWebComp);
    registerKewebsiComponent(KewebsiInputElement.tag, KewebsiInputElement);
    registerKewebsiComponent(KewebsiDateTimeEditor.tag, KewebsiDateTimeEditor);
    // registerKewebsiComponent(HtmlErrorDisplayCompanion.tag, HtmlErrorDisplayCompanion);
    registerKewebsiComponent(StandardCalendarPopup.getTag(), StandardCalendarPopup);
    registerKewebsiComponent(TableCalendarPopup.getTag(), TableCalendarPopup);
    registerKewebsiComponent(Powertable.getTag(), Powertable);

    var x = new Powertable();

    
}

export function registerKewebsiComponent(tag: string, theClass: CustomElementConstructor) {
    let registerdKewebsiComponents: string[] = []
    if (! document["kewebsiComponents"]) {
        document["kewebsiComponents"] = [];
    } else {
        registerdKewebsiComponents = document["kewebsiComponents"];
    }
    
    customElements.define(tag, theClass);
    registerdKewebsiComponents.push(tag);
}

export function isKewebsiComponent(tag: string) {

    const kewebsiComponents : string[] = document["kewebsiComponents"] as string[];

    if (kewebsiComponents.includes(tag)) {
        return true;
    }
    return false;


}