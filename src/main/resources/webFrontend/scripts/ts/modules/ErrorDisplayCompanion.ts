import { Companion } from "./Companion";
import { StyleManager } from "./StyleManager";

export class ErrorDisplayCompanion implements Companion<HTMLDivElement> {
    
    static tag = "kewebsi-error-display";
    // static styleSheet = "span { display: flex; border: flex-flow: row;}"

    topElement: HTMLDivElement; 
    errorMsgSpan: HTMLSpanElement;
    revertButton: HTMLElement;

    
    constructor(errorMsg: string, setOldData: () => void, removeErrorDisplay: () => void) {
        this.topElement = document.createElement("div");
        this.topElement.classList.add("error-msg-div-overlap")
        this.createChildElements(errorMsg, setOldData, removeErrorDisplay);
    }
    getTopElement(): HTMLDivElement {
        return this.topElement;
    }
    setTopElement(el: HTMLDivElement) {
       if (this.topElement) {
        throw Error("Overwriting this.topElement not supported");
       }
       this.topElement = el;
       el['companion'] = this;
    }

    remove(): void {
        this.topElement.remove();
    }


    /**
     * 
     * @param errorMsg 
     * @param setOldData Can be null. Then there will be no button for reverting data.
     * @param removeErrorDisplay Can be null. Then there will be no button for removing the error display.-
     */
    createChildElements(errorMsg: string, setOldData: () => void, removeErrorDisplay: () => void) {
        // this.topDiv = document.createElement("div");
        this.errorMsgSpan = document.createElement("span");
        this.errorMsgSpan.style.whiteSpace="normal";  // Allows line breaks within the span.
        this.topElement.appendChild(this.errorMsgSpan);
        this.errorMsgSpan.textContent = errorMsg;

        let buttonDiv = document.createElement("div");
        buttonDiv.classList.add("right-top-button-group")
        // buttonDiv.style.paddingLeft = "0.5em"

        if (setOldData != null) {
            this.revertButton = createErrorRevertButton();
            this.revertButton.onclick = (e: MouseEvent) => setOldData();
            buttonDiv.appendChild(this.revertButton);      
        }
        
        if (removeErrorDisplay != null) {
            const cancelButton = createErrorCancelButton();
            cancelButton.onclick = (e: MouseEvent) => removeErrorDisplay();
            buttonDiv.appendChild(cancelButton);  
        }

        this.topElement.appendChild(buttonDiv);

    }

}

export function createErrorRevertButton() : HTMLElement {
    let button = document.createElement("div");
    button.classList.add(StyleManager.divButtonTextClass);
    button.innerHTML = "&#9100;";  // Revert symbol
    return button;
}

export function createErrorCancelButton() : HTMLElement {
    let cancelButtonX = document.createElement("div");
    // let cancelButtonX = createChildByTagName(divForCloseX, "div");
    // cancelButtonX.classList.add("close");
    cancelButtonX.classList.add(StyleManager.divButtonTextClass)
    cancelButtonX.innerHTML = "&#x2716;"  // Large X

    return cancelButtonX;
}