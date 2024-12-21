

export function replaceElement(elementToReplace, childDefinition) {
    if (elementToReplace == null) {
        console.log("No element to replace found: ");
        return;
    }
    return replaceElementCore(elementToReplace, childDefinition);
}
function replaceElementCore(elementToReplace, childDefinition) {
    let helpDiv = document.createElement("div"); // TODO: Use DocumentFragment
    helpDiv.innerHTML = childDefinition;
    var newElement = helpDiv.childNodes[0];
    elementToReplace.replaceWith(newElement);
    // setEventHandlersOnTreeInclRoot(newElement);
    return newElement;   
}


