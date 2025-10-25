import * as jointTypes from "./jointTypes.js";

import {CMD_GENERATE_CALENDAR} from "./jointTypes.js";

import { createTableByDef} from "./Powertable.js";
import { SessionHandling, getServerMsgHandler, setClientEventHandler, CEH_escapeKeyPressed } from "./jaccessEventHandling.js";
import { getBody, getPageName, setSmartFocus } from  "./kewebsiUtils.js";
import { POWERTABLE_COMMAND, REMOVE_MODAL } from  "./Powertable.js";
import { GuiDef, ModalStandardDialogDef, PlacedPopupDef } from "./messageTypes.js";
import {createChildByGuiDef as createElementByGuiDefAndAppend, createAndAppendChildren, 
    setStandardProperties, 
    createChildByGuiDef, createChildByTagName, createElementByGuiDef} from "./kewebsiPageComposer";
import { warn } from "./jaccessEventHandling.js";

const DISPAY_UNDER_ATTR  = "data-date-input-field";


/**
 * Modal dialog with standard cancel buttons.
 * @param guiDef 
 * @returns 
 */
export function createModalStandardDialog(guiDef: GuiDef) : HTMLDivElement {


    // Create Calender Content


    let modalDialogDef: ModalStandardDialogDef = guiDef as ModalStandardDialogDef;

    let centerChildrenParent = document.createElement("DIV") as HTMLDivElement;
    setClientEventHandler(centerChildrenParent,"CEH_escapeKeyPressed");

    centerChildrenParent.id = guiDef.id;
    centerChildrenParent.classList.add("center-children-parent");
    getBody().appendChild(centerChildrenParent);

    let centerChildrenChild = createChildByTagName(centerChildrenParent, "DIV");
    centerChildrenChild.classList.add("center-children-child");
        
    let divForCloseX =  createChildByTagName(centerChildrenChild, "DIV");
    divForCloseX.classList.add("div-for-close-x");
    let cancelButtonX = createChildByGuiDef(divForCloseX, modalDialogDef.cancelXButton);
    
    
    let divMainArea = createChildByTagName(centerChildrenChild,"DIV");
    divMainArea.innerHTML = "<DIV>" + modalDialogDef.mainText + "</DIV>";

    let cancelButton = createChildByGuiDef(divMainArea, modalDialogDef.cancelButton) 
    let confirmButton = createChildByGuiDef(divMainArea, modalDialogDef.confirmButton)

    setSmartFocus(cancelButton);

    return centerChildrenParent;
}

export function createKewebsiPlacedPopup(guiDef: GuiDef) : HTMLElement {

    let placedPopupDef = guiDef as PlacedPopupDef;

    let newElement = document.createElement("DIV");
    setStandardProperties(newElement, placedPopupDef);
    createAndAppendChildren(newElement, guiDef);

    return newElement;
}

export function createSimpleModalPopup(message: string, okButtonText: string = "OK"): HTMLDivElement {
    // Create the modal background
    const modal = document.createElement("div");
    modal.classList.add("modal");

    // Create the modal content box
    const modalContent = document.createElement("div");
    modalContent.classList.add("modal-content");

    // Add the message
    const msgDiv = document.createElement("div");
    msgDiv.textContent = message;
    modalContent.appendChild(msgDiv);

    // Create the OK button
    const okButton = document.createElement("button");
    okButton.textContent = okButtonText;
    okButton.onclick = () => {
        modal.style.display = "none";
    };
    modalContent.appendChild(okButton);

    // Add content to modal
    modal.appendChild(modalContent);

    // Optionally, append to body
    document.body.appendChild(modal);

    return modal;
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

export function hideModalDialogById(id: string) {
    let dialog = document.getElementById(id) as HTMLElement;
    dialog.style.display = "none";
}

export function hideModalDialogContainingButton(containedButton: HTMLElement) {
    let dialogId = containedButton.getAttribute(jointTypes.CLIENT_EVENT_HANDLER_DIALOG_ID_ATTR_NAME);
    hideModalDialogById(dialogId);
}



export function CEH_executeServerAction(mouseEvent: MouseEvent) {
    
    let targetButton : HTMLElement = mouseEvent.target as HTMLElement;
    let serverMsgHandlerCloseModal = targetButton.getAttribute(jointTypes.SERVER_MSG_HANDLER_MODAL_CLOSE);
    let dialogId = targetButton.getAttribute(jointTypes.CLIENT_EVENT_HANDLER_DIALOG_ID_ATTR_NAME);
    let pageName = getPageName(targetButton);
    let msg : jointTypes.MsgClientActionAndTag = {
		msgName: jointTypes.CS_MESSAGE, 
		module: null, 
        serverMsgHandler: serverMsgHandlerCloseModal, 
		pageName: pageName, 
		command: POWERTABLE_COMMAND,
		subCommand: REMOVE_MODAL,
        tagId: dialogId
    }	
    SessionHandling.ajaxCallStandard(msg);	
}


