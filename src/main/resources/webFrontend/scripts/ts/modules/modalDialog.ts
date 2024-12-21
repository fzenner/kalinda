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

    // let popupUnderId = modalDialogDef.popupUnderId;
    
    
    //  // The placing information is optional
    // if (popupUnderId) {
    //     let popupUnderTag = document.getElementById(popupUnderId);
        
    //     // This defines the tag that needs to be placed underneath the given tag.
    //     // let visiblePopupContentTag = document.getElementById(visiblePopupContentTagId);

    //     let rect = popupUnderTag.getBoundingClientRect();

    //     centerChildrenChild.style.position = "fixed";
    //     centerChildrenChild.style.top = rect.bottom.toString() + "px";
    //     centerChildrenChild.style.left = rect.left.toString() + "px";
    // }

    return centerChildrenParent;
}

export function createKewebsiPlacedPopup(guiDef: GuiDef) : HTMLElement {

    let placedPopupDef = guiDef as PlacedPopupDef;



    // let newElement = createElementByGuiDef(guiDef);
    let newElement = document.createElement("DIV");
    setStandardProperties(newElement, guiDef);
    createAndAppendChildren(newElement, guiDef);


    // let popupUnderId = placedPopupDef.relatedHtmlElementId;
    // if (popupUnderId) {

    //     if (newElement.childElementCount != 1) {
    //         warn("Error in createKewebsiPlacedPopup: Number of children is not 1 but " + newElement.childElementCount);
    //     }
    
    //     let childToPlace = newElement.children[0] as HTMLElement;
    
    //     let popupUnderTag = document.getElementById(popupUnderId);
        
    //     let rect = popupUnderTag.getBoundingClientRect();

    //     childToPlace.style.position = "fixed";
    //     childToPlace.style.top = rect.bottom.toString() + "px";
    //     childToPlace.style.left = rect.left.toString() + "px";
    // }
    return newElement;
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


