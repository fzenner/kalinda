

import { loadDynamicContentImpl } from "./modules/jaccessEventHandling";
import { defineWebComponents } from "./modules/webcomps";



export function initThisShit() {
	defineWebComponents();
    loadDynamicContent();
}   

export function loadDynamicContent() {
	loadDynamicContentImpl();
}
  

function getValueById(elementId: string): string {
	let elm: HTMLInputElement = <HTMLInputElement>document.getElementById(elementId);
	let inputValue = elm.value;
	return inputValue;
}

function setValueById(elementId: string, value: string) {
	let elm: HTMLInputElement = <HTMLInputElement>document.getElementById(elementId);
	elm.value = value;
}

function disableById(elementId: string, disabled: boolean) {
	let elm: HTMLInputElement = <HTMLInputElement>document.getElementById(elementId);
	elm.disabled = disabled;
}

function getRadioButtonChoiceByName(name: string): string {
	let elm: HTMLInputElement = <HTMLInputElement>document.querySelector("input[name='" + name + "']:checked");
	let val = elm.value;
	return val;
}





function getButtonById(elementId: string, disabled: boolean): HTMLButtonElement {
	let elm: HTMLButtonElement = <HTMLButtonElement>document.getElementById(elementId);
	return elm;
}



function setCheckBoxValue(elementId: string, val: boolean) {
	let elm: HTMLInputElement = <HTMLInputElement>document.getElementById(elementId);
	elm.checked = val;
}



function addClickCallbackById(elementId: string, callback: () => void) {
	let elm: HTMLButtonElement = <HTMLButtonElement>document.getElementById(elementId);
	elm.addEventListener('click', callback);
	return elm;
}


/**
 * 
 * @param elementId HTML element ID to add the callback function to
 * @param functionAsString The callback function as string. It must have the signature () => void, 
 *                          e.g. "function providedInString() {alert(\"called from within script\")}"
 */
function addClickCallbackByIdAndFunctionAsString(elementId: string, functionAsString: string) {
	let funcVar: () => void = parseFunction(functionAsString);
	addClickCallbackById(elementId, funcVar);
}


function parseFunction(functionAsString: string): () => void {
	let funcVar: () => void;
	let scriptExpression = "funcVar = " + functionAsString;
	// Assign the function to our variable.
	try {
		eval(scriptExpression);
	} catch (e) {
		console.log(e); // Fehler-Objekt an die Error-Funktion geben
	}
	return funcVar;
}



function appendValueById(elementId: string, value: string) {
	let elm: HTMLTextAreaElement = <HTMLTextAreaElement>document.getElementById(elementId);
	elm.value = elm.value + "\n" + value;
}













