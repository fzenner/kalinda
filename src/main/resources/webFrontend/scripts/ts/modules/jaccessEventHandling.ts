import { getPageName, getPageName2, getValuesFromPageById, setSmartFocus } from "./kewebsiUtils.js";
import {InputElementStateInfoFromClientToServer } from "./InputFieldStates.js"
import {
	UPDATE_OPERATION_ATTRIBUTE_MODIFIED, UPDATE_OPERATION_ATTRIBUTE_NEW, UPDATE_OPERATION_ATTRIBUTE_REMOVED,
	UPDATE_OPERATION_CSS_CLASSES_CHANGED, UPDATE_OPERATION_MODIFIED, UPDATE_OPERATION_NEW,UPDATE_OPERATION_REMOVED, UPDATE_OPERATION_VISIBILITY_OR_DISPLAY_CHANGED,
	MsgAjaxResponse, MsgClientUpdate, UPDATE_BY_DEF_CUSTOM, UPDATE_BY_DEF_MODIFIED, UPDATE_BY_DEF_NEW, UPDATE_BY_DEF_REMOVED, TableFocusDef
} from "./messageTypes";

import { tdHandleKeyDown, tdHandleMouseDown, tdHandleFocusReceived, CEH_resizerMouseDownHandler as handleResizerMouseDown,
	handleInsertEntityForTableEdit, 
	CEH_handleTableCheckBoxClicked as handleTableCheckBoxClicked,
	setTableFocus
} 
from "./Powertable.js"; 	

import { updateByDef} from "./kewebsiPageComposer.js";

import { GuiDef } from "./messageTypes";

import { replaceElementByIdAndHtmlString, appendElementByIdAndHtmlString, removeElementById, CEH_openNav, CEH_closeNav, appendElementByIdAndDefOjb, replaceElementByIdAndDefObj } from "./kewebsiPageComposer.js";

import * as jointTypes from "./jointTypes.js";
import { mapClientInputStateInBrowserToStateInfoForServer } from "./InputFieldStates.js";


const SERVICE_NAME_ATTR_NAME = 'data-service-name';

const FUNCTION_NAME_ATTR_NAME = 'data-functionname';
const INVOKE_SERVICE = "INVOKE_SERVICE";
const FIELD_DATA_ENTERED = "INVOKE_SERVICE";
export const APPLY_CENTRAL_EVENT_HANDLER_CSS_CLASS = 'aceh'


export function loadDynamicContentImpl() {
	let msg : jointTypes.MsgInvokeServiceWithParams =  {
		msgName: jointTypes.CS_MESSAGE, 
		module: null, 
		serverMsgHandler: "PAGE_LOADER", 
		command: "loadDynamicContent",
		subCommand: null,
	};

	SessionHandling.ajaxCallStandard(msg);
}



export function findFirstChildWithAttribute(parent: HTMLElement, attr: string) : HTMLElement {
	for (let elm of parent.children) {
		if (elm.hasAttribute(attr)) {
			return elm as HTMLElement;
		}
	}
	return null;
}


export function setOneEventHandlerByGuiDef(htmlElm: HTMLElement, guiDef: GuiDef) {
	let eh = guiDef.clientEventHandler;
	let eventName: string  = null;
	if (eh) {
		switch (eh) {
			case "CEH_escapeKeyPressed": eventName = "keydown"; 
			break;
			case "CEH_standardButtonClicked": eventName = "click"; 
			break;
			default: warn("EventHanlder " +  eh + " not found.");

		}

		setOneEventHandler(htmlElm, eventName, guiDef.clientEventHandler);
	}
}

export function setClientEventHandler(htmlElm: HTMLElement, clientEventHandlerName: string)  {
	switch (clientEventHandlerName) {
		case "CEH_escapeKeyPressed": htmlElm.addEventListener("keydown", CEH_escapeKeyPressed); break;
		case "CEH_standardButtonClicked":  htmlElm.addEventListener("click", CEH_standardButtonClicked); break;
	}
}


export function setOneEventHandler(htmlElm: HTMLElement, eventName: string, clientEventHandlerName: string, ) {
	switch (clientEventHandlerName) {

		case jointTypes.CLIENT_EVENT_HANDLER_INSERT_ENTITY_FOR_TABLE_EDIT:
			{
				htmlElm.addEventListener(eventName, handleInsertEntityForTableEdit);
			}
			break;


		case "CEH_genericDataSubmit":
			htmlElm.addEventListener("click", CEH_genericDataSubmit);
			break;

		case "CEH_selectionMade":
			htmlElm.addEventListener(eventName, CEH_selectionMade);
			break;

		case "CEH_standardButtonClicked":
			htmlElm.addEventListener(eventName, CEH_standardButtonClicked);
			break;

		case "CEH_escapeKeyPressed":
			htmlElm.addEventListener(eventName, CEH_escapeKeyPressed);
			break;

		case "CEH_preventDefault":
				htmlElm.addEventListener(eventName, CEH_preventDefault);
			break;

		case "CEH_openNav":
				htmlElm.addEventListener(eventName, CEH_openNav);
			break;

		case "CEH_closeNav":
			htmlElm.addEventListener(eventName, CEH_closeNav);
		break;

			{
				console.log("WARNING: Could not find an event handler for " + clientEventHandlerName);
			}


	}
}



/**
 * Callback to invoke a server side function with parameters from the page. 
 * 
 * @param event 
 */
 
 function CEH_genericDataSubmit(event: KeyboardEvent) { 
	let target : HTMLElement = event.target as HTMLElement;
	let serverMsgHandler = target.getAttribute(jointTypes.SERVER_MSG_HANDLER_ATTR_NAME);
	let htmlFieldsToSendDtoAsString =  target.getAttribute(jointTypes.HTML_FIELDS_TO_SEND_DTO_ATTR_NAME);
	let fixedParamsToSendDtoAsString = target.getAttribute(jointTypes.FIXED_PARAMS_TO_SEND_DTO_ATTR_NAME);
	let specialDataToSendDtoAsString = target.getAttribute(jointTypes.SPECIAL_DATA_TO_SEND_DTO_ATTR_NAME);

	let pageName = getPageName(target);


	let htmlFieldData : any = null;
	if (htmlFieldsToSendDtoAsString !== null) {
		let dto = JSON.parse(htmlFieldsToSendDtoAsString);
		htmlFieldData = getValuesFromPageById(dto);
	}

	let fixedParams : string = null;
	if (fixedParamsToSendDtoAsString !== null) {
		fixedParams = JSON.parse(fixedParamsToSendDtoAsString);
	} 

	let specialData : string = null;
	if (specialDataToSendDtoAsString !== null) {
		specialData = JSON.parse(fixedParamsToSendDtoAsString);
	} 


	
	let msg : jointTypes.MsgInvokeServiceWithParams =  {
		msgName: jointTypes.CS_MESSAGE, 
		module: null, 
		serverMsgHandler: serverMsgHandler, 
		pageName: pageName, 
		command: INVOKE_SERVICE,
		subCommand: null,
		htmlFieldData: htmlFieldData,
		fixedParams: fixedParams,
		specialData: specialData
	};

	SessionHandling.ajaxCallStandard(msg);
}





export function CEH_standardButtonClicked(event: Event) {
	let target : HTMLElement = event.target as HTMLElement;
	let pageName = getPageName(target);

	let msg : jointTypes.MsgClientActionAndTag =  {
		msgName: jointTypes.CS_MESSAGE, 
		serverMsgHandler: "SMH_standardButtonClicked", 
		pageName: pageName, 
		tagId: target.id
	};

	SessionHandling.ajaxCallStandard(msg);
}



export function CEH_escapeKeyPressed(event: KeyboardEvent) {

	if (event.key==="Escape") {

		// Take the element to which this functin is registered, not the element where the focus is and the event originated.
		let target : HTMLElement = event.currentTarget as HTMLElement;  
		
		let pageName = getPageName(target);


		let msg : jointTypes.MsgClientActionAndTag =  {
			msgName: jointTypes.CS_MESSAGE, 
			serverMsgHandler: "SMH_escapeKeyPressed", 
			pageName: pageName, 
			tagId: target.id
		};


		SessionHandling.ajaxCallStandard(msg);	

	}

}


function CEH_selectionMade(event: Event) { 
	let target = event.target as HTMLInputElement;
	let serverSideId = target.getAttribute(jointTypes.SERVER_SIDE_ID_ATTR_NAME);

	let pageName = getPageName(target);
	
	let msg : jointTypes.MsgFieldDataEntered =  {
		msgName: jointTypes.CS_MESSAGE, 
		serverMsgHandler: "SMH_selectionMade", 
		pageName: pageName, 
		tagId: target.id,
		// serverSideId: serverSideId,
		value: target.value,
		clientInputState : InputElementStateInfoFromClientToServer.CLIENT_INPUT_FILLED_OK.toString()
	};

	SessionHandling.ajaxCallStandard(msg);
}




export function invokeServerCallbackForHtmlElement(serverMsgHandler: string, command: string, tagId: string, param: string) {
	let pageName = getPageName2()
	let msg : jointTypes.MsgClientActionAndTag =  {
		msgName: jointTypes.CS_MESSAGE, 
		serverMsgHandler: serverMsgHandler,
		command: command,
		param: param,
		pageName: pageName, 
		tagId: tagId
	};

	SessionHandling.ajaxCallStandard(msg);
}







export class SessionHandling {

	static ajaxCall(requestObj, resultCallback: (payload: string, status: number) => void) {
		var xhttp = new XMLHttpRequest();
		xhttp.open("POST", "/ajaxServlet", true);
		xhttp.setRequestHeader("Content-type", "application/json");

		xhttp.setRequestHeader("SENDING_CLIENT_URL", window.location.href);   // IS NOT NEEDED: Will be transferred as standard header. TODO: Remove
		let gepid = document.hasOwnProperty("globalEditPageId") ? document["globalEditPageId"] : -1;
		let obj = document as object;

		if (typeof gepid === "number" && gepid !== null && gepid != undefined && gepid > 0) {
			xhttp.setRequestHeader("EDIT_PAGE_ID", gepid.toString());
		} else {
			xhttp.setRequestHeader("EDIT_PAGE_ID", "NO_EDIT_PAGE_ID_PRESENT");
		}

		xhttp.onreadystatechange = function () {
			if (this.readyState == 4) {
				resultCallback(this.responseText, this.status);
			}
		}
		xhttp.withCredentials = true;

		let requestJson = JSON.stringify(requestObj);
		xhttp.send(requestJson);
	}

	/**
	 * Makes an ajax call and links it with the "standard" ajaxReplyHandler
	 * @param requestObj Json object representing the call payload
	 */
	static ajaxCallStandard(requestObj) {
		SessionHandling.ajaxCall(requestObj, SessionHandling.ajaxStandardReplyHandler);
	}


	static ajaxCallWithCallback(requestObj, callback : (parsedReply: any) => void ) {

		let parameterizedCallback = function(payload: string, status: number) { 
			SessionHandling.ajaxStandardReplyHandlerWithCallback(payload, status, callback);
		}
		SessionHandling.ajaxCall(requestObj, parameterizedCallback);
	}


	// TODO: We need to implement a callback that accepts a dynamic function (lambda) to be invoked on the client side
	// MOre specifically: The command to create a table-cell-calendar popup should be passed to the callback invocation.


	static ajaxStandardReplyHandlerWithCallback(rawReply: string, status: number, callback : (parsedReply: any) => void) {

		if (status == 0) {  // Firefox retuns 0 instead of 200
			status = 200;
		}

		if (status != 200) {
			displayErrorAsModalWindow("Ajax request returned with error:", "Received HTTP status: " + status);
			return;
		}
		let reply = JSON.parse(rawReply);

		if  (reply.msgName === "ERROR_INFO") {
			displayErrorAsModalWindow("Error processing Ajax request:", reply.errorText);
			return;
		}

		callback(reply.customCallbackData);

		return;
	}


	static ajaxStandardReplyHandler(data: string, status: number) {

		if (status == 0) {  // Firefox retuns 0 instead of 200
			status = 200;
		}

		if (status != 200) {
			displayErrorAsModalWindow("Ajax request returned with error:", "Received HTTP status: " + status);
			return;
		}
		let reply = JSON.parse(data);
		if (reply.msgName === "BUSINESS_ERROR") {
			dispatchAjaxResponse(reply);              // MAIN CALL
			// alert("Error processing Ajax request: " + reply.errorInfo.errorText);
			displayErrorAsModalWindow("Error processing Ajax request:", reply.errorInfo.errorText);
			return;
		}
		if  (reply.msgName === "ERROR_INFO") {
			displayErrorAsModalWindow("Error processing Ajax request:", reply.errorText);
			return;
		}

		if (reply.msgName === "EVAL_IN_BROWSER") {
			eval(reply.functionCode);
		} else if (reply.msgName === "SUCCESS") {
			dispatchAjaxResponse(reply);              // MAIN CALL
		}
		if (true) {
			console.log("Reply received:" + reply);
		} else { 
			alert("Unexpected reply creating table: " + reply.msgName);
			return;
		}
	}
}





function dispatchAjaxResponse(response: MsgAjaxResponse) {

	// window.history.pushState({make: "mehappy"}, "unused", "http://localhost:8080/gotcha");

	let updates: MsgClientUpdate[] = response.htmlUpdates as MsgClientUpdate[];
	// GLOBAL_EDIT_PAGE_ID = response.editPageId
	document["globalEditPageId"] = response.editPageId;
	if (updates !== null) {
		for (let updateRun of updates) {

			let updateOperation = updateRun.updateOperation;
			let idToUpdate = updateRun.idToUpdateOrParent;
			

			switch (updateOperation) {
				
				case UPDATE_BY_DEF_CUSTOM:
				case UPDATE_BY_DEF_MODIFIED:
				case UPDATE_BY_DEF_NEW:
				case UPDATE_BY_DEF_REMOVED:
					updateByDef(updateRun);
				break;				
				case UPDATE_OPERATION_MODIFIED:
					if (updateRun.newHtmlNode !== null && updateRun.newHtmlNode !== undefined) {
						let newHtmlNode = updateRun.newHtmlNode;
						replaceElementByIdAndDefObj(idToUpdate, newHtmlNode);
					} else {
						let newHtmlCode = updateRun.newHtmlCode;
						replaceElementByIdAndHtmlString(idToUpdate, newHtmlCode);
					}
					break;

				case UPDATE_OPERATION_NEW: {
					let idOfParent = updateRun.idToUpdateOrParent;

					if (updateRun.newHtmlNode !== null && updateRun.newHtmlNode !== undefined) {
						appendElementByIdAndDefOjb(idOfParent, updateRun.newHtmlNode)
					} else {
						let newHtmlCode = updateRun.newHtmlCode;
						appendElementByIdAndHtmlString(idOfParent, newHtmlCode)
					}
				}
					break;

				case UPDATE_OPERATION_REMOVED:
					let idToRemove = updateRun.idToUpdateOrParent
					removeElementById(idToRemove);
					break;


				case UPDATE_OPERATION_ATTRIBUTE_MODIFIED: {
					let key = updateRun.attributeKey;
					let val = updateRun.attributeValue;
					let elementToUpdate = document.getElementById(idToUpdate);
					// elementToUpdate.setAttribute(key, val);
					elementToUpdate[key] = val;
					break;
				}

				case UPDATE_OPERATION_ATTRIBUTE_NEW: { // This is the same code as MODIFIED
					let key = updateRun.attributeKey;
					let val = updateRun.attributeValue;
					let elementToUpdate = document.getElementById(idToUpdate);
					elementToUpdate.setAttribute(key, val);
					// elementToUpdate[key] = val;
					break;
				}

				case UPDATE_OPERATION_ATTRIBUTE_REMOVED: {
					let key = updateRun.attributeKey;
					let elementToUpdate = document.getElementById(idToUpdate);
					elementToUpdate.removeAttribute(key);
					// delete elementToUpdate[key];
					break;
				}

				case UPDATE_OPERATION_VISIBILITY_OR_DISPLAY_CHANGED: {
					let visiblityOrDisplay : string = updateRun.visibilityOrDisplay;
					let elementToUpdate = document.getElementById(idToUpdate);

					switch(visiblityOrDisplay) {
						case "VISIBILITY_HIDDEN":
							elementToUpdate.style.visibility = "hidden";
							break;
						case "VISIBILITY_VISIBLE":
							elementToUpdate.style.visibility = "visible";
							break;
						case "DISPLAY_NONE":
							elementToUpdate.style.display = "none";
						break;	
						case "DISPLAY_BLOCK":
							elementToUpdate.style.display = "block";
						break
						default:
							console.log("Unknown visiblityOrDisplay received:" + visiblityOrDisplay);
					}

					// if (visiblityOrDisplay) {
					// 	if (visiblityOrDisplay === "true") {
					// 		elementToUpdate.style.visibility = "hidden";
					// 	} else {
					// 		elementToUpdate.style.visibility = "visible";
					// 	}
					// } else {
					// 	elementToUpdate.style.visibility = null;
					// }
					break;

				}


				case UPDATE_OPERATION_CSS_CLASSES_CHANGED: {       //     XXXXXXXXXXXXXXXXXX
					let elementToUpdate = document.getElementById(idToUpdate);
					// let classList :DOMTokenList = elementToUpdate.classList;
					let classesString = updateRun.newHtmlCode
					elementToUpdate.className = classesString;
					}
					break;


				default:
					console.log("Unhandled updateoperation: " + updateOperation);
			}
		}
	}

	if (response.focusDef) {
		let focusDef = response.focusDef;
		switch (focusDef.focusFunction) {
			case "CFU_standardFocus": {
				let a: HTMLElement = document.activeElement as HTMLElement;
				a.blur();
				let elementToFocus = document.getElementById(focusDef.tagIdToFocus);
				setSmartFocus(elementToFocus);
			}
				break;
			case "CFU_tableFocus": {
				let tableFocusDef = response.focusDef as TableFocusDef;
				setTableFocus(tableFocusDef);
			}
				break;
		}
	}

}





export function getServerMsgHandler(elm: HTMLElement) {
	return elm.getAttribute(jointTypes.SERVER_MSG_HANDLER_ATTR_NAME);
}





export function CEH_preventDefault(mouseEvent: MouseEvent) {
	mouseEvent.preventDefault();
}



export function displayErrorAsModalWindow(errorMsg1: string, errorMsg2: string) {


	var msgSpan = document.createElement("span");
	msgSpan.textContent = errorMsg1;

    var okButton = document.createElement("button");
    okButton.textContent = "OK";
    okButton.onclick = closeErrorModal;

	var contentLayoutDiv = document.createElement("div");
	contentLayoutDiv.classList.add("modalErrorContentDiv")
    
    var innerDiv = document.createElement("div");
    innerDiv.classList.add("center-children-child")

    var middleDiv = document.createElement("div");
    middleDiv.classList.add("center-children-parent");

    var outerDiv = document.createElement("div");
    outerDiv.id = "error-modal";
    outerDiv.classList.add("modal-error");
    

    outerDiv.appendChild(middleDiv);
    middleDiv.appendChild(innerDiv);
	innerDiv.appendChild(contentLayoutDiv);
    contentLayoutDiv.appendChild(msgSpan);

	if (errorMsg2) {
		var msgSpan2 = document.createElement("span");
		msgSpan2.textContent = errorMsg2;
		contentLayoutDiv.appendChild(msgSpan2);
	}

	contentLayoutDiv.appendChild(okButton);

    document.body.appendChild(outerDiv);

}

export function closeErrorModal() {
    let modalWindow = document.getElementById("error-modal");
    modalWindow.remove();
}

export function warn(msg: string) {
	console.log("WARNING:" + msg);
}