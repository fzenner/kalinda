
import * as jointTypes from"./jointTypes.js";
import { CheckBoxGuiDef, GuiDef } from "./messageTypes.js";
import { getPageName } from "./kewebsiUtils.js";
import { SessionHandling,  } from "./jaccessEventHandling.js";
import { setGenericAttributes, setServerMsgHandler, setStandardProperties } from "./kewebsiPageComposer.js";
import { KewebsiInputElement } from "./KewebsiInputElement.js";
import { InputElementStateInfoFromClientToServer } from "./InputFieldStates.js";
import { isWellDefined } from "./stringUtils.js";

/*********************************************
 * input
 *********************************************/

export function createKewebsiInputWithErrorDisplay(guiDef: GuiDef) : KewebsiInputElement {

	const newEl = KewebsiInputElement.create(guiDef);

	return newEl;

}



export function createKewebsiCheckbox(guiDef: GuiDef) : HTMLElement {

    let input = document.createElement("INPUT") as HTMLInputElement;
	input.type = "checkbox";
    input.id = guiDef.id;
    // input.classList.add("textInput");

	setGenericAttributes(input, guiDef);
	// setServerMsgHandler(input, guiDef);

	let inputFieldGuiDef: CheckBoxGuiDef = guiDef.tagSpecificData;

	if (isWellDefined(inputFieldGuiDef.value)) {
		input.checked = inputFieldGuiDef.value;
	}

	if (isWellDefined(inputFieldGuiDef.disabled)) {
		input.disabled = inputFieldGuiDef.disabled;
	}


    addCheckBoxClickListener(input);
    return input;
}

export function createKewebsiRadioButton(guiDef: GuiDef) : HTMLElement {

    let input = document.createElement("INPUT") as HTMLInputElement;
	input.type = "radio";
    input.id = guiDef.id;
    // input.classList.add("textInput");

	setGenericAttributes(input, guiDef);
	// setServerMsgHandler(input, guiDef);
    addRadioButtonClickListener(input);
    return input;
}


/*********************************************
 * select
 *********************************************/

export function createSelectBox(guiDef: GuiDef) : HTMLSelectElement{
	let result = document.createElement("select")
	result.setAttribute("value", guiDef.text);
	createSelectOptions(result, guiDef);
	setStandardProperties(result, guiDef);
	addSelectionMadeListener(result);
	setServerMsgHandler(result, guiDef);

	return result;
 }


 function createSelectOptions(elm: HTMLElement, guiDef: GuiDef) {

	let selectStrings: string[] = guiDef.state as string[];

	for (let optionStr of selectStrings) {
		let optionElm = document.createElement("option");
		optionElm.value = optionStr;
		optionElm.text = optionStr;
		let selectedOptionStr = guiDef.text;
		if (selectedOptionStr) {
			if (optionStr === selectedOptionStr) {
				optionElm.setAttribute("selected", "");
			}
		}
		elm.appendChild(optionElm);
	}
}





/*********************************************
 * Event handling
 *********************************************/



function CEH_selectionMade(event: Event) { 
	let target = event.target as HTMLInputElement;
	let serverMsgHandler = target.getAttribute(jointTypes.SERVER_MSG_HANDLER_ATTR_NAME);

	let serverSideId = target.getAttribute(jointTypes.SERVER_SIDE_ID_ATTR_NAME);

	let pageName = getPageName(target);
	
	let msg : jointTypes.MsgFieldDataEntered =  {
		msgName: jointTypes.CS_MESSAGE, 
		serverMsgHandler: "SMH_selectionMade",
		pageName: pageName, 
		tagId: target.id,
		// serverSideId: serverSideId,
		value: target.value,
		clientInputState: InputElementStateInfoFromClientToServer.CLIENT_INPUT_FILLED_OK.toString()
	};

	SessionHandling.ajaxCallStandard(msg);
}

// function addInputChangeListener(elm: HTMLElement) {
// 	elm.addEventListener("change", CEH_inputChange);
// }




function addCheckBoxClickListener(elm: HTMLElement) {
	elm.addEventListener("click", CEH_checkBoxClicked);
}


function addRadioButtonClickListener(elm: HTMLElement) {
	elm.addEventListener("click", CEH_radioButtonClicked);
}

function addSelectionMadeListener(elm: HTMLElement) {
	elm.addEventListener("change", CEH_selectionMade);
}

export function CEH_checkBoxClicked(mouseEvent: MouseEvent) {
	let target = mouseEvent.target as HTMLInputElement;
	let pageName = getPageName(target);

	let value = target.checked ? "t" : "f"

	let msg : jointTypes.MsgFieldDataEntered = {
		msgName: jointTypes.CS_MESSAGE,
		serverMsgHandler: "SMH_checkBoxClicked",
		pageName: pageName,
		tagId: target.id,
		value: value,
		clientInputState: InputElementStateInfoFromClientToServer.CLIENT_INPUT_FILLED_OK.toString()
	}

	SessionHandling.ajaxCallStandard(msg);
}




export function CEH_radioButtonClicked(event: Event) { 
	let target = event.target as HTMLInputElement;

	let value = target.checked ? "t" : "f"

	if (target.type !== "radio") {
		console.log("Warning: CEH_radioButtonClicked invoked but target is not a radio button");
	}

	let pageName = getPageName(target);
	
	let msg : jointTypes.MsgFieldDataEntered =  {
		msgName: jointTypes.CS_MESSAGE, 
		module: null, 
		serverMsgHandler: "SMH_radioButtonClicked", 
		pageName: pageName, 
		tagId: target.id,
		// serverSideId: target.id,
		value: value,
		clientInputState: InputElementStateInfoFromClientToServer.CLIENT_INPUT_FILLED_OK.toString()
	};

	SessionHandling.ajaxCallStandard(msg);
}