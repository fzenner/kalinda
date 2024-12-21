import { StyleManager } from "./StyleManager";



function getErrorRevertButtonClass() : string {
    return "calendar-revert-button";
}

export function createCalendarButton() : HTMLElement {
    let button = document.createElement("button");
    button.classList.add(getCalendarButtonClass());
    button.innerHTML = "&#128197;"  // Calendar Symbol
    return button;
}



function getCalendarButtonClass() : string {
    return StyleManager.calendarLaunchButtonClass;
}



