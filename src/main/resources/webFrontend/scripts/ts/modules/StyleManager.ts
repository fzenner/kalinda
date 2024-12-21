

export class StyleManager {

    static symbolButtonTextSize = "120%";

    static redButtonInactiveColor = "rgba(255, 78, 78)";
    static redButtonActiveColor = "rgba(255, 0, 0)";
    
    
    // border:1px solid

    static inputFieldStyle =

    ".div-for-close-x {\n"
    + "    justify-content: right;\n"
    + "    display: flex;\n"
    + "    padding: 0.2em;\n"
    + "} \n"

    + ".error-button-text {\n"
    + "    border:1px solid;\n"
    + "    color: " + this.redButtonInactiveColor + ";\n"
    + "    font-size: " + StyleManager.symbolButtonTextSize + ";\n"
    + "    line-height: 0.8;\n"
    + "    font-weight: bold;\n"
    + "    margin: 1px;\n"
    + "} \n"

    + ".error-button-text:hover,\n"
    + ".error-button-text:focus {\n"
    + "    color: " + this.redButtonActiveColor + ";\n"
    + "    text-decoration: none;\n"
    + "    cursor: pointer;\n"
    + "} \n"

    + ".error-button-text:active {\n"
    + "    background-color: grey;\n"
    + "    outline-width: 3px;\n"
    + "    outline-style:  solid;\n"
    + "    outline-color: rgba(80, 80, 80, 0.9);\n"
    + "} \n"

    + ".error-msg-div-overlap {\n"
    + "    position: absolute;\n"
    + "    border-style: solid;\n"
    + "    border-width: thin;\n"
    + "    border-color: red;\n"
    + "    color: red;\n"
    + "    background: white;\n"
    + "    opacity: 0.8;\n"
    + "    display: flex;\n"
    + "    flex-flow: row;\n"
    + "} \n"

    + ".right-top-button-group {\n"
    + "    padding-left: 0.5em;\n"
    + "    display: flex;\n"
    + "    flex-flow: row;\n"
    + "} \n"

    + ".input-field-div {\n"
    + "    padding-left: 0.5em;\n"
    + "    display: flex;\n"
    + "    flex-flow: row;\n"
    + "} \n"

    
    // All chidren off first class (tool-tip-anchor) that have the second class (tooltip)
    // get this style.
    + ".tooltip-anchor .tooltip {\n"
    + "    visibility: hidden;\n"
    + "} \n"

    + ".tooltip-anchor:hover .tooltip {\n"
    + "    visibility: visible;\n"
    + "} \n"

    + ".tooltip {\n"
    + "    visibility: hidden;\n"
    + "    width: 120px;\n"
    + "    color: #fff;\n"
    + "    background-color: blue;\n"
    + "    text-align: center;\n"            
    + "    border-radius: 6px;\n"
    + "    padding: 5px 0;\n"
    + "    position: absolute;\n"
    + "    z-index: 1;\n"
    + "} \n"



    
    static divButtonTextClass = "error-button-text";

    static horizontalDivFlowStyle = ".horizontalDivFlow { display: flex; border: flex-flow: row;}\n"
    static horizontalDivFlowClass = "horizontalDivFlow";




    static divVorCloseX_Style = 
    ".div-for-close-x {\n"
    + " justify-content: right;\n"
    + " display: flex;\n"
    + " padding: 0.2em;\n"
    + " }\n"

    static divVorCloseX_Class = "div-for-close-x";



    static powerTableStyle = 
    "th {" 
    + "   border: 1px solid black;\n"
    + "    font-family: Arial;\n"
    + "    font-size: 12px;\n"
    + "    font-size-adjust: none;\n"
    + "    font-stretch: normal;\n"
    + "    font-style: normal;\n"
    + "    font-variant: normal;\n"
    + "    font-weight: bold;\n"
    + "    line-height: normal;\n"
    + "    margin: 0;\n"
    + "    padding-top: 3px;\n"
    + "    padding-bottom: 3px;\n"
    + "    padding-left: 3px;\n"
    + "    padding-right: 3px;\n"
    + "}\n"
    + "table {\n"
    + "    border: none;\n"
    + "}\n"
    
    + ".entity-table {\n"
    + "    border: 1px solid black;\n"
    + "    border-collapse: collapse;\n"
    + "    table-layout:fixed;\n"
    + " }\n"
    
    + "td {\n"
    + "    padding-top: 0px;\n"
    + "    padding-bottom: 0px;\n"
    + "    padding-left: 3px;\n"
    + "    padding-right: 3px;\n"
    + "    border: 1px solid black;\n"
        
    + "    font-family: Arial;\n"
    + "    font-size: 12px;\n"
    + "    font-size-adjust: none;\n"
    + "    font-stretch: normal;\n"
    + "    font-style: normal;\n"
    + "    font-variant: normal;\n"
    + "    line-height: normal;\n"
    + "    margin: 0;\n"
    
    + "    white-space: nowrap;\n"
    + "    text-overflow: ellipsis;\n"
    + "    margin-top: 0px;\n"
    + "    margin-bottom: 0px;\n"
    + "    overflow: hidden;\n"
    + "}\n"
    + ".forcedFocusOutline {\n"
    + "    outline-width: 2px;\n"
    + "    outline-style:  dashed;\n"
    + "    outline-color: rgb(145, 70, 180);\n"
    + "}\n"
    + "td:focus {\n"
    + "    outline-width: 2px;\n"
    + "    outline-style:  dashed;\n"
    + "    outline-color: steelblue;\n"
    + "}\n";


    static powertableInputFieldStyle = 
    ".textInput {\n"
    + "    white-space: nowrap;\n"
    + "    font-size: 12px;\n"
    + "    padding: 0px;\n"
    + "    margin: 0px;\n"
    + "}\n"
    + ".textSubInput {\n"
    + "    border:none;\n"
    + "    white-space: nowrap;\n"
    + "    font-size: 12px;\n"
    + "    padding: 0px;\n"
    + "    margin: 0px;\n"
    + "}\n"
    + ".textSubInputWithError {\n"
    + "    border:none;\n"
    + "    white-space: nowrap;\n"
    + "    font-size: 12px;\n"
    + "    padding: 0px;\n"
    + "    margin: 0px;\n"
    + "}\n"
    + ".textSubInputWithError:focus {\n"
    + "    outline-offset: 2px;\n"
    + "    outline-color: red;\n"
    + "    font-size: 12px;\n"
    + "    padding: 0px;\n"
    + "    margin: 0px;\n"
    + "}\n"

    static calendarLaunchButtonClass = "calendar-launch-button";
    static calendarLaunchButtonStyle = 
    ".calendar-launch-button {\n"
    + "    border:none;\n"
    + "    padding-top: 0px;\n"
    + "    padding-bottom: 0px;\n"
    + "    font-size: 12px;\n"
    + "}\n"
}

