
export function getPageName(elm: HTMLElement) {
	let body = elm.ownerDocument.body as HTMLElement;
	return body.getAttribute("pageName");
}

export function getPageName2() {
	let bodies = document.getElementsByTagName("BODY");
	let body = bodies[0];
	return body.getAttribute("pagename");
}

export function getBody() : HTMLBodyElement {
	let bodies = document.getElementsByTagName("BODY");
	return bodies[0] as HTMLBodyElement;
}


/**
 * Fills the member variables of the given object with input values from the current document.
 * The values are retrieved by IDs. The read IDs are identical with the keys of the given dto object.
 * @param dto Object to be filled. 
 * @returns Filled object.
 */
 export function getValuesFromPageByIdOld(dto: Object): Object {

	for (const [key, value] of Object.entries(dto)) {
		let inputEl = getInputById(key);
		if (inputEl !== null) {
			dto[key] = inputEl.value;
		} else {
			console.log("Input field with ID " + key + " not found");
		}



		console.log('${key}: ${value}');
	}
	return dto;
}


/**
 * Fills the member variables of the given object with input values from the current document.
 * The values are retrieved by IDs. The read IDs are identical with the keys of the given dto object.
 * @param fields Filelds to brea reak to be filled. 
 * @returns New object with given fields and values read from the page.
 */ 
export function getValuesFromPageById(fields: Array<string>): Object {
	let result = new Object();
	for (var idx in fields) {
		var fieldName = fields[idx];
		let inputEl = getInputById(fieldName);
		if (inputEl !== null) {
			result[fieldName] = inputEl.value;
		} else {
			console.log("Input field with ID " + fieldName + " not found");
		}
	}
	return result;
}


export function isFocused(elm: HTMLElement) {
	let isFocused = (document.activeElement === elm);
	return isFocused;
}


export function getInputById(elementId: string): HTMLInputElement {
	let elm: HTMLInputElement = <HTMLInputElement>document.getElementById(elementId);
	return elm;
}

export function boolToString(val: boolean) : string {
	return val ? "y" : "n";
}

export function stringToBool(val: string) : boolean {
	if (val === "y") {
		return true;
	} else {
		if (val === "n") {
			return false; 
		} else {
			throw "Illegal value: " + val + ". Expected either 'y' or 'n'."
		}
	}
}


export function getBooleanAttribute(htmlElement: HTMLElement, attributeName: string) : boolean {
	let strVal = htmlElement.getAttribute(attributeName);	
	let boolVal = stringToBool(strVal);
	return boolVal;
}


export function setBooleanAttribute(htmlElement: HTMLElement, attributeName: string, value :boolean) {
	let strVal = boolToString(value);
	htmlElement.setAttribute(attributeName, strVal);
}

export function modalWindowIsShown() : boolean{
	var elementFound = document.querySelector("[data-is-modal-window='true']");
	if (elementFound === null) {
		return false;
	} else {
		return true;
	}
}



export function setSmartFocus(container: HTMLElement) {
	if (isFocusable(container)) {
		container.focus();
	} else {
		let focusableElement = findFirstFocusableElement(container);
		if (focusableElement) {
			focusableElement.focus()
		}
	}
	
}

export function findFirstFocusableElement(container: HTMLElement) : HTMLElement {
	return Array.from(container.getElementsByTagName("*")).find(isFocusable) as HTMLElement;
};
  
export function isFocusable(item: HTMLElement)  { 
	if (item === null) {
		let a = 1;
	}
	if (item.tabIndex < 0) {
	  return false;
	}

	item.getElementsByTagName
	
	switch (item.tagName) {
	  case "A":
		let anchor = item as HTMLAnchorElement;
		return !!anchor.href;
	  case "INPUT":
		let input = item as HTMLInputElement;
		return input.type !== "hidden" && !input.disabled;
	  case "SELECT":
		let select = item as HTMLSelectElement;
		return !select.disabled;
	  case "TEXTAREA":
		let ta = item as HTMLTextAreaElement;
		return !ta.disabled;
	  case "BUTTON":
		let button = item as HTMLButtonElement;
		return !button.disabled;
	  default:
		return true;
	}
  };

export function findFirstParentWithClass(el: HTMLElement, className: string) : HTMLElement {
	if (className.includes(" ")) {
		console.error("Function findFirstParentWithClass does not work with classnames containing blanks.");
		return null;
	}
  	return el.closest("." + className);
}

export function selectAll(el: HTMLInputElement) {
	el.setSelectionRange(0, el.value.length);
}

export function propertyExists(prop: any) : boolean {
	if (prop === undefined) {
		return false;
	} else {
		return true;
	}
}

export function stringIsInt(possiblyUntrimmedString: string) : boolean {
	var reg = /^\d+$/;
	var isInt = reg.test(possiblyUntrimmedString.trim());
	return isInt;
}