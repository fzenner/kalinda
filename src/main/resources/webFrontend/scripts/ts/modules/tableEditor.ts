
import { COMPANION_PROP, Companion} from "./Companion";
import { TableDataTypes } from "./TableDateTimeEditor";
import { Powertable } from "./Powertable";

export abstract class TableEditorCompanion<T extends TableEditor>  {

    topElement: T;
    replacingSpanFieldId: string;
    oldValue: string;
    restoreOldDataOnFocusLoss: boolean;
    idPrefix: string

    constructor(el: T, replacingSpanFieldId: string, oldValue: string, idPrefix: string) {
        this.setTopElement(el)
        this.replacingSpanFieldId = replacingSpanFieldId;
        this.oldValue = oldValue;
        this.restoreOldDataOnFocusLoss = false;
        this.idPrefix = idPrefix + "-editor";
    }
    getTopElement(): T {
        return this.topElement;
    }
    setTopElement(el: T) {
        
        if (this.topElement) {
            throw Error("Overwriting this.topElement not supported");
        }
        this.topElement = el;
        el[COMPANION_PROP] = this;
    }
    remove(): void {
        this.topElement.remove();
    }

    // abstract getStringValue();
}

export class StandardStringEditorCompanion<T extends TableEditor> extends TableEditorCompanion<T> {

    constructor(el: T, replacingSpanField: HTMLSpanElement, idPrefix: string) {
        super(el, replacingSpanField.id, replacingSpanField.textContent, idPrefix)
    
    }

    // getStringValue() : string {
    //     return this.topElement.value;
    // }

}



export abstract class TableEditor extends HTMLElement {
    isTableEditor : boolean;
    dataType : TableDataTypes;
    powerTable: Powertable


    topElement: HTMLElement;
    replacingSpanField: HTMLElement;
    oldValue: string;
    restoreOldDataOnFocusLoss: boolean;
    idPrefix: string

    constructor(replacingSpanField: HTMLElement, oldValue: string, dataType: TableDataTypes, idPrefix: string) {
        super()
        this.replacingSpanField = replacingSpanField;
        this.oldValue = oldValue;
        this.dataType = dataType;
        this.restoreOldDataOnFocusLoss = false;
        this.id = idPrefix + "-editor";
    }
    getTopElement(): HTMLElement {
        return this.topElement;
    }
    setTopElement(el: HTMLElement) {
        
        if (this.topElement) {
            throw Error("Overwriting this.topElement not supported");
        }
        this.topElement = el;
        el[COMPANION_PROP] = this;
    }

    abstract getStringValue() : string;

    abstract hasSyntacticError() : boolean; 

}