import { focusLossIsPermanentV2, stringIsInt } from "./kewebsiUtils";
import { Powertable, unplaceCellEditor } from "./Powertable";
import { getTableEditor, getTdFromChildElement } from "./powerTableNavigation";
import { TableDataTypes } from "./TableDateTimeEditor";
import { TableEditor } from "./tableEditor";


export class TableStringEditor extends TableEditor {

    static tag = "kalinda-tablestringeditor"
    isTableEditor: boolean = true;

    inputEl: HTMLInputElement
    toolTip: HTMLSpanElement;

    constructor(replacingSpanFieldId: HTMLElement, powerTble: Powertable, oldValue: string, dataType: TableDataTypes, idPrefix: string) {
        super(replacingSpanFieldId, oldValue, dataType, idPrefix);
        this.topElement = document.createElement("div");
        this.topElement.classList.add("tooltip-anchor");
        this.inputEl = document.createElement("input");
        this.inputEl.id = idPrefix + "-inputel"
        this.inputEl.classList.add("table-stringeditor");
        this.inputEl.value = oldValue;
        this.topElement.appendChild(this.inputEl);
        this.toolTip = document.createElement("span");
        this.toolTip.classList.add("tooltip");
        this.toolTip.textContent = "Unset"
        this.topElement.appendChild(this.toolTip)
        this.setCellEditorEventHandlers()

        // const debugMe = document.createElement("div");
        // debugMe.classList.add("tooltip-anchor")
        // this.topElement.appendChild(debugMe)
    }

    connectedCallback() {
        this.appendChild(this.topElement)
    }


    setCellEditorEventHandlers() {
        this.inputEl.addEventListener("blur", inputFieldFocusLostHandler);
        this.inputEl.addEventListener("keydown", keyDownInputField);
        this.inputEl.addEventListener("mousedown", mouseDownInputField);
    }

    getStringValue() {
        return this.inputEl.value;
    }

    override hasSyntacticError() {
        switch (this.dataType) {
            case TableDataTypes.INTEGER:
                return stringIsInt(this.getStringValue())
            default: 
            return false;
        }
    }

}

customElements.define(TableStringEditor.tag, TableStringEditor)

function inputFieldFocusLostHandler(event: FocusEvent) {
	console.log("inputFieldFocusLostHandler entered.");

	let eventTarget:EventTarget = event.target;
	let targetInputElement = eventTarget as HTMLInputElement;
    let tableEditor = getTableEditor(targetInputElement);

	// let table = getTableFromChildElement(targetInputElement);
	// let strippedTableId = constructStrippedId(table.id);

	if (! focusLossIsPermanentV2(event)) {
		return;
	}

	console.log("inputFieldFocusLostHandler 1.");
	let parentTdElement : HTMLTableCellElement = getTdFromChildElement(targetInputElement) as HTMLTableCellElement;

	if (parentTdElement == null) {
		//
		// The input was lost because the cell editor has been programmatically removed. In this case, there is nothing to do.
		//
		console.log("inputFieldFocusLostHandler 2.");
		return;

	}
	console.log("inputFieldFocusLostHandler 3.");
	
	// const ted = getCompanion(targetInputElement) as TableEditorCompanion<TableEditor>;


	let oldVal : string = tableEditor.oldValue;
	let oldId : HTMLElement = tableEditor.replacingSpanField;
	
	let newVal : string;
	let valueWasModified: boolean = false;
	if (tableEditor.restoreOldDataOnFocusLoss) {
		// Typically when Escape was pressed on the input field. 
		newVal = oldVal;  
	}  else {
		newVal = tableEditor.getStringValue();
		valueWasModified = true;
	}

	unplaceCellEditor(oldId, newVal, parentTdElement, tableEditor, valueWasModified);

	console.log("inputFieldFocusLostHandler leaving.");
}


export function keyDownInputField(event: KeyboardEvent) {
	console.log("keyDownInputField: " + event.key);
	if (event.key === "Escape") {
		console.log("keyDownInputField entered 1."); 
		let eventTarget:EventTarget = event.target;
		let targetInputElement = eventTarget as HTMLInputElement;
		let parentTdElement = getTdFromChildElement(targetInputElement);
		console.log(parentTdElement)

		const tableEditor = getTableEditor(targetInputElement); 

		tableEditor.restoreOldDataOnFocusLoss = true;
		parentTdElement.focus();
	} else {
		if (event.key ==="Enter") {
			let eventTarget:EventTarget = event.target;
			let targetInputElement = eventTarget as HTMLInputElement;
			let parentTdElement = getTdFromChildElement(targetInputElement);
			parentTdElement.focus();
		}
	}
	console.log("keyDownInputField leaving.");
}

function mouseDownInputField(event: MouseEvent) {
	console.log("mouseDownInputField");
 	event.stopPropagation();
}

