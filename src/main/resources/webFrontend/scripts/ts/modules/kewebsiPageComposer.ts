

import { setOneEventHandler, setOneEventHandlerByGuiDef, CEH_standardButtonClicked, warn } from "./jaccessEventHandling.js";
import { CLIENT_EVENT_HANDLER_ATTR_NAME_1, SERVER_MSG_HANDLER_ATTR_NAME } from "./jointTypes.js";
import { MsgClientUpdate, GuiDef, UPDATE_BY_DEF_CUSTOM, UPDATE_BY_DEF_NEW, UPDATE_BY_DEF_MODIFIED,UPDATE_BY_DEF_REMOVED } from "./messageTypes";
import { createModalStandardDialog, createKewebsiPlacedPopup } from "./modalDialog.js";
import { createTableByDef, updateTablePayloadByDef, Powertable } from "./Powertable.js";
import { createKewebsiInputWithErrorDisplay, createSelectBox, createKewebsiCheckbox, createKewebsiRadioButton } from "./components";
import {  WebCompSupportingUpdates as WebCompSupportingUpdates, isKewebsiComponent } from "./webcomps.js";
import { KewebsiDateTimeEditor } from "./DateTimeEditor.js";
import { KewebsiInputElement } from "./KewebsiInputElement.js";
import { isWellDefined } from "./stringUtils.js";
import { InputElementStateInBrowser} from "./InputFieldStates.js";

export function replaceElementByHtmlString(elementToReplace: HTMLElement, childDefinition: string) : HTMLElement {
	if (elementToReplace == null) {
		console.log("No element to replace found: ");
		return;
	}
	return replaceElementCoreByHtmlString2(elementToReplace, childDefinition);
}


export function replaceElementByIdAndHtmlString(elementId: string, childDefinition: string) : HTMLElement {
	let elementToReplace: HTMLElement = <HTMLElement>document.getElementById(elementId);
	return replaceElementCoreByHtmlString2(elementToReplace, childDefinition);
}

export function replaceElementByIdAndDefObj(elementId: string, childDefinition: object) : HTMLElement {
	let elementToReplace: HTMLElement = <HTMLElement>document.getElementById(elementId);
	return replaceElementCoreByDefObj(elementToReplace, childDefinition);
}

// export function replaceElementCoreByHtmlString(elementToReplace: HTMLElement, childDefinition: string) : HTMLElement{
// 	let helpDiv = document.createElement(elementToReplace.parentElement.tagName);   
// 	helpDiv.innerHTML = childDefinition;
// 	var newElement = helpDiv.childNodes[0] as HTMLElement;
// 	elementToReplace.replaceWith(newElement);
// 	return newElement;
// }


export function replaceElementCoreByHtmlString2(elementToReplace: HTMLElement, childDefinition: string) : HTMLElement{
	let helpDiv = document.createElement("div");   
	helpDiv.innerHTML = childDefinition;
	var newElement = helpDiv.childNodes[0] as HTMLElement;
	elementToReplace.replaceWith(newElement);
	return newElement;
}

export function replaceElementCoreByDefObj(elementToReplace: HTMLElement, newObjDef: object) : HTMLElement{
	let newElement = createElementFromJsonDefObject(newObjDef);
	elementToReplace.replaceWith(newElement);
	// setEventHandlersOnTreeInclRoot(newElement);
	return newElement;
}


export function appendElementByIdAndHtmlString(parentId: string, childDefinition: string) : HTMLElement {
	let parentElement: HTMLElement = <HTMLElement>document.getElementById(parentId);
	return appendElementByHtmlString(parentElement, childDefinition);
}

export function appendElementByIdAndDefOjb(parentId: string, childDefinition: object) : HTMLElement {
	let parentElement: HTMLElement = <HTMLElement>document.getElementById(parentId);
	return appendElementByDefObj(parentElement, childDefinition);
}

export function appendElementByHtmlString(parent: HTMLElement, childDefinition: string) : HTMLElement {
	let helpDiv = document.createElement(parent.tagName); 
	helpDiv.innerHTML = childDefinition;
	var newElement = helpDiv.childNodes[0] as HTMLElement;
	parent.appendChild(newElement);
	// setEventHandlersOnTreeInclRoot(newElement);
	return newElement;
}


export function appendElementByDefObj(parent: HTMLElement, defObj: Object) : HTMLElement {
	let newElement = createElementFromJsonDefObject(defObj);
	parent.appendChild(newElement);
	// setEventHandlersOnTreeInclRoot(newElement);
	return newElement;
}

export function removeElementById(elementIdToRemove: string) {
	let elementToRemove: HTMLElement = <HTMLElement>document.getElementById(elementIdToRemove);
	elementToRemove.remove();
}

function removeElement(elementToRemove: HTMLElement) {
	elementToRemove.remove();
}


function createElementFromJsonDefObject(defObj: Object)  {
	let tagName : string = defObj["tagname"];

	let result = null;
	if (tagName === "TEXT_NODE") {

		// The text might contain character encoding like "Code &#x2716;" This will be displayed verbatim, if we simple 
		// create the text node by document.createTextNode(text); 
		// Hence we need to take an ugly detour over innerHtmls. But that detour craps out when the text is empty,
		// hence we need onother exception for that. 
		
		let text : string = defObj["text"];
		text.length === 0 
		if (text.length == 0 ) {
			let htmlText : Text = document.createTextNode(text);
		   result = htmlText;
		} else {
			let myDiv = document.createElement("div");
			myDiv.innerHTML = text;
			result = myDiv.firstChild;
		}

		// let htmlText : Text = document.createTextNode(text);
		// result = htmlText;
	} else {
		let htmlElm : HTMLElement = document.createElement(tagName) as HTMLElement
		result = htmlElm;
		let attrs : object = defObj["attrs"];
		if (attrs !== null && attrs !== undefined) {
			for (const [key, value] of Object.entries(attrs)) { 
				if (key === "id") {
					htmlElm["id"] = value;
				} else {
					htmlElm.setAttribute(key, value);
				}
			}
		}

		var children : object[] = defObj["children"];
		if (children !== null && children !== undefined) {
			for (let childRun of children) {
				appendElementByDefObj(htmlElm, childRun);
			}
		}
	}

	// The result is either a Text node oder an HTMLElement. Unfortunately, a Text is not a HTML element.
	// Things seem to work though.
	return result;
}


export function CEH_openNav() {
	document.getElementById("sideNav").style.width = "250px";
	document.getElementById("main").style.marginLeft = "250px";
  }
  
export function CEH_closeNav() {
	document.getElementById("sideNav").style.width = "0";
	document.getElementById("main").style.marginLeft= "0";
  }


  
export function updateByDef(updateDefinition: MsgClientUpdate) {  

	// XXXXXXXXXXXXXXXXXXXXXXXX
	switch(updateDefinition.updateOperation) {
		case UPDATE_BY_DEF_MODIFIED: {
			updateByDefModified(updateDefinition);
		}
		break;
		case UPDATE_BY_DEF_NEW: {
			let newElement = createElementByGuiDef(updateDefinition.guiDef);
			let parent = document.getElementById(updateDefinition.idToUpdateOrParent);
			parent.appendChild(newElement);
		}
		break;
		case UPDATE_BY_DEF_REMOVED: {
			let element = document.getElementById(updateDefinition.idToUpdateOrParent);
			element.remove();
		}
		break;

		case UPDATE_BY_DEF_CUSTOM: {
			updateByCustomOperation(updateDefinition);
		}
		break;

		default: warn("Unkown update operation:" + updateDefinition.updateOperation)
	}
}


function updateByDefModified(msgClientUpdate: MsgClientUpdate) {
	let guiDef = msgClientUpdate.guiDef;
	const updateMode = guiDef.updateMode;
	if (updateMode === "MODIFICATIONS_ONLY") {
		updateByGuiDef(guiDef);
		return;
	}
	if (updateMode === "REPLACE") {
		let newElement = createElementByGuiDef(msgClientUpdate.guiDef);
		let elementToReplace = document.getElementById(msgClientUpdate.idToUpdateOrParent);
		elementToReplace.replaceWith(newElement);
		return;
	}
	if (updateMode === "FULL" ) {
		throw new Error("UpdateMode FULL not implemented yet");
	}

	throw new Error("Unknown UpdateMode: " + updateMode)

}

export function updateByCustomOperation(updateOperation: MsgClientUpdate) {
	
	let guiDef: GuiDef = updateOperation.guiDef;
	let opname = guiDef.customUpdateOperation;

	if (opname === "UPDATE_TABLE_PAYLOAD") {
		updateTablePayloadByDef(guiDef);
	}

}

export function updateByGuiDef(guiDef: GuiDef) {
	const inputWebComp : HTMLElement = document.getElementById(guiDef.id);
	let webComp = (inputWebComp as any) as WebCompSupportingUpdates;
	if (! webComp.updateModifications) {
		let jodlelelel = 123;
	}
	webComp.updateModifications(guiDef);
}


// export function updateByGuiDef(updateMsg: MsgClientUpdate) {
// 	const inputFieldModifications = updateMsg.inputFieldModifications;
// 	if (inputFieldModifications) {
// 		const inputWebComp : KewebsiInputElement = document.getElementById(inputFieldModifications.id) as KewebsiInputElement;
// 		inputWebComp.update(updateMsg.inputFieldModifications);
// 	}
// }

export function createChildByGuiDef(parent: HTMLElement, guiDef: GuiDef) : HTMLElement {
	let newEl = createElementByGuiDef(guiDef);
	parent.appendChild(newEl);
	return newEl;
}

export function createChildByTagName(parent: HTMLElement, tagName: string) : HTMLElement {
	let newEl = document.createElement(tagName);
	parent.appendChild(newEl);
	return newEl;
} 

export function createElementByGuiDef(guiDef: GuiDef) : HTMLElement {
    let result: HTMLElement;
	let tag = guiDef.tag.toLowerCase();


    switch(guiDef.tag) {
        case "powertable":
            result = createTableByDef(guiDef);
            break;

		case "body":
			result = document.createElement("body");
			createAndAppendChildren(result, guiDef);
			setStandardPropertiesAndText(result, guiDef);
			break;

		case "div":
			result = document.createElement("div");
			createAndAppendChildren(result, guiDef);
			setStandardPropertiesAndText(result, guiDef);
			setOneEventHandlerByGuiDef(result, guiDef)
			break;

		case "standardbutton":
			result = document.createElement("button");
			createAndAppendChildren(result, guiDef);
			setStandardPropertiesAndText(result, guiDef);
			setClientEventHandlerForClick(result, guiDef);
			break;
		case "modalstandarddialog":
			result = createModalStandardDialog(guiDef);
			break;

		case "kewebsi-input":
			result =  KewebsiInputElement.create(guiDef);
			break;

		case "kewebsi-datetime-editor":
			result = KewebsiDateTimeEditor.create(guiDef);
			break;			

		case "kewebsi-checkbox":
			result = createKewebsiCheckbox(guiDef);
			break;

		case "kewebsi-radiobutton":
			result = createKewebsiRadioButton(guiDef);
			break;			
		
		case "kewebsi-placed-popup":
			result = createKewebsiPlacedPopup(guiDef);
			break;

		case "select":
			result = createSelectBox(guiDef);

			break;

        default: 
			result = document.createElement(guiDef.tag);
			setStandardPropertiesAndText(result, guiDef);
			createAndAppendChildren(result, guiDef);
    }
    return result;
 }

 function setStandardPropertiesAndText(elm: HTMLElement, guiDef: GuiDef) {
	setId(elm, guiDef.id);
	setClasses(elm, guiDef);
	setGenericAttributes(elm, guiDef);
	setText(elm, guiDef);
	place(elm, guiDef);
 }

 export function setStandardProperties(elm: HTMLElement, guiDef: GuiDef) {
	setId(elm, guiDef.id);
	setClasses(elm, guiDef);
	setGenericAttributes(elm, guiDef);
 }

 function setId(elm: HTMLElement, id: string) {
	if (id) {
		elm.id = id;
	}
 }

 function setClasses(elm: HTMLElement, guiDef: GuiDef) {
	if (guiDef.cssClasses && guiDef.cssClasses.length > 0) {
		elm.classList.add(...guiDef.cssClasses);
	}
 }

 function setText(elm: HTMLElement, guiDef: GuiDef) {
	if (guiDef.text && guiDef.text.length > 0) {
		elm.innerHTML = guiDef.text;
	}
 }


 export function setServerMsgHandler(elm: HTMLElement, guiDef: GuiDef) {
	let callbackId = guiDef.serverCallbackId;
	if (callbackId && callbackId.length > 0) {
		setServerMsgHandlerId(elm, guiDef.serverCallbackId);
	}
 }

 export function setServerMsgHandlerId(elm: HTMLElement, serverCallbackId: string) {
		elm.setAttribute(SERVER_MSG_HANDLER_ATTR_NAME, serverCallbackId);
		// elm.addEventListener("click", CEH_buttonClicked);
}


function setClientEventHandlerForStandardButtonClick(elm: HTMLElement, guiDef: GuiDef) {
	elm.addEventListener("click", CEH_standardButtonClicked);
 }

 function setClientEventHandlerForClick(elm: HTMLElement, guiDef: GuiDef) {
	let clientEventHandlerName = guiDef.clientEventHandler;
	// let specialClientEventHandlerSet = false;
	// if (clientEventHandlerName && clientEventHandlerName.length > 0) {

	// 	let eventName = "click";

	// 	switch (clientEventHandlerName) {
	// 		case "CEH_saveTableData":
	// 			elm.addEventListener("click", CEH_saveTableData);
	// 			specialClientEventHandlerSet = true
	// 			break;
	// 		default: 
				
	// 	}
	// }

	//if (! specialClientEventHandlerSet) {
		elm.addEventListener("click", CEH_standardButtonClicked);
	//}
 }



 

 export function createAndAppendChildren(parent: HTMLElement, holdingChildren: GuiDef) {
	if (holdingChildren.children && holdingChildren.children.length > 0)
	for (let childDef of holdingChildren.children) {
		let child = createElementByGuiDef(childDef);
		parent.appendChild(child);
	}
 }


 export function setGenericAttributes(elm: HTMLElement, guiDef: GuiDef) {
	let attrs = guiDef.attrs;
	if (attrs) {
		for (const [key, value] of Object.entries(attrs)) {
			elm.setAttribute(key, value);
		}
	}
 } 


 export function place(elm: HTMLElement, guiDef: GuiDef)  {
	let popupUnderId = guiDef.relatedHtmlElementId;
	if (popupUnderId) {
		if (guiDef.relatedPlacing === "UNDER_LEFT_ALIGNED") {
			let popupUnderTag = document.getElementById(popupUnderId);
			let rect = popupUnderTag.getBoundingClientRect();
			elm.style.position = "fixed";
			elm.style.top = rect.bottom.toString() + "px";
			elm.style.left = rect.left.toString() + "px";
		} else {
			warn("Error in function place. Value for GuiDef.relatedPlacing not known: " + guiDef.relatedPlacing);
		}
	}
 }


//  export function getCompanion(el: HTMLElement) : any {
// 	return el["companion"];
//  }


//  export function setCompanion(el: HTMLElement, distinctChildren: any) : void {
// 	el["companion"] = distinctChildren;
//  }

export function addIdClass(el: HTMLElement, classPostfix: string) {
	const className = "idClass_" + classPostfix;
	el.classList.add(className);
}

export function findChildChildByIdClass(parent: HTMLElement, classPostfix: string) {
	const className = "." + "idClass_" + classPostfix;
	const result = parent.querySelectorAll(className);
	if (result.length > 1) {
		throw Error("Id class not unique. More then child with classname " + className + " found." );
	}
	return result[0];
}

export function applyVisiblity(elm: HTMLElement, guiDef: GuiDef) {
	if (isWellDefined(guiDef.visibility)) {
		const newVisibility = guiDef.visibility;
		let styleVal = null;
		if (newVisibility) {
			switch (newVisibility) {
				case "DISPLAY_NONE": styleVal = "display:none;"; break;
				case "DISPLAY_BLOCK": styleVal = "display:block;"; break;
				case "VISIBILITY_HIDDEN": styleVal = "visibility:hidden"; break;
				case "VISIBILITY_VISIBLE": styleVal = "visibility:visible"; break;
				default: throw "Unknown visiblity detected: " + newVisibility + " for " + elm;
			};

			elm.setAttribute("style", styleVal);
		}
	}
}

export function applyServerError(elm: WebCompSupportingUpdates, guiDef: GuiDef) {
	
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
	}
}


export function getColorForInputElementState(state: InputElementStateInBrowser) {
	let color : string;
	switch(state) {
		case InputElementStateInBrowser.EMPTY_AS_SERVER_SIDE_ERROR:
			color = "Coral";
			break;
		case InputElementStateInBrowser.EMPTY_OPTIONAL:
			color = "White";
			break;
		case InputElementStateInBrowser.EMPTY_REQUIRED:
			color = "Yellow";
			break;
		case InputElementStateInBrowser.INCOMPLETE: 
			color = "LightYello";
			break;
		case InputElementStateInBrowser.UNPARSEABLE:
			color = "Orange";
			break;
		case InputElementStateInBrowser.FILLED_OK:
			color = "White";
			break;
		case InputElementStateInBrowser.DISABLED:
			color = "LightGray";
			break;
		case InputElementStateInBrowser.SERVER_SIDE_ERROR:
			color = "LightSalmon";
			break;
		case InputElementStateInBrowser.MULTI_FIELD_ERROR_MARKED:
			color = "Magenta";
			break;
		default: throw new Error("Unknown InputFieldState encountered:" + this.inputFieldState);
	}
	return color;
}