
import { COMPANION_PROP, Companion} from "./Companion";

export abstract class TableEditorCompanion<T extends HTMLElement> implements Companion<T> {

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

    abstract getStringValue();
}

export class StandardStringEditorCompanion<T extends HTMLInputElement| HTMLSelectElement> extends TableEditorCompanion<T> {

    constructor(el: T, replacingSpanField: HTMLSpanElement, idPrefix: string) {
        super(el, replacingSpanField.id, replacingSpanField.textContent, idPrefix)
    
    }

    getStringValue() : string {
        return this.topElement.value;
    }

}
