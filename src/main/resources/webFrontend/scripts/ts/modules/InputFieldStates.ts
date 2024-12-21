

export enum InputElementStateInBrowser {
    //
    // Note: Assigning a String to the enum is important, so that toString() generates a String, not a number.
    //    
    EMPTY_OPTIONAL = "EMPTY_OPTIONAL",
    EMPTY_REQUIRED = "EMPTY_REQUIRED",
    EMPTY_AS_SERVER_SIDE_ERROR = "EMPTY_AS_SERVER_SIDE_ERROR",
    INCOMPLETE = "INCOMPLETE",
    UNPARSEABLE = "UNPARSEABLE",
    FILLED_OK = "FILLED_OK",
    SERVER_SIDE_ERROR = "SERVER_SIDE_ERROR",
    DISABLED = "DISABLED",
    MULTI_FIELD_ERROR_MARKED = "MULTI_FIELD_ERROR_MARKED"
}


export enum InputElementStateInfoFromClientToServer {
    //
    // Note: Assigning a String to the enum is important, so that toString() generates a String, not a number.
    //
    CLIENT_INPUT_EMPTY = "CLIENT_INPUT_EMPTY",
    CLIENT_INPUT_NOT_TRANSMISSABLE = "CLIENT_INPUT_NOT_TRANSMISSABLE",
    CLIENT_INPUT_FILLED_OK = "CLIENT_INPUT_FILLED_OK"
}

export type InputElementError = {
    isClientSideError: boolean;  // false if serverSideError
    errorText: string
    errorId ? : number
}


export enum FieldType {
    STR = "STR",
    ENM = "ENM", 
    INT = "INT", 
    LONG = "LONG", 
    FLOAT = "FLOT", 
    LOCALDATETIME = "LOCALDATETIME", 
    LOCALDATE = "LOCALDATE" ,
    LOCALTIME = "LOCALTIME"
};



   /**
     * This function should only be invoked after a recalculation of the state after a value change
     * of the input element itself. Hence states set by the server
     * ( e.g. InputElementStateInBrowser.EMPTY_AS_SERVER_SIDE_ERROR) are not expected here and considered
     * an error.
     * @param stateInBrowser 
     * @returns 
     */
   export function mapClientInputStateInBrowserToStateInfoForServer(stateInBrowser: InputElementStateInBrowser) : InputElementStateInfoFromClientToServer  {

    switch (stateInBrowser) {
        case InputElementStateInBrowser.EMPTY_OPTIONAL: return InputElementStateInfoFromClientToServer.CLIENT_INPUT_EMPTY;
        case InputElementStateInBrowser.EMPTY_REQUIRED: return InputElementStateInfoFromClientToServer.CLIENT_INPUT_EMPTY;
        case InputElementStateInBrowser.EMPTY_AS_SERVER_SIDE_ERROR: throw new Error("Unexpected client state: " + stateInBrowser); 
        case InputElementStateInBrowser.INCOMPLETE: return InputElementStateInfoFromClientToServer.CLIENT_INPUT_NOT_TRANSMISSABLE;
        case InputElementStateInBrowser.UNPARSEABLE: return InputElementStateInfoFromClientToServer.CLIENT_INPUT_NOT_TRANSMISSABLE;
        case InputElementStateInBrowser.FILLED_OK: return InputElementStateInfoFromClientToServer.CLIENT_INPUT_FILLED_OK;
        case InputElementStateInBrowser.DISABLED: throw new Error("Unexpected client state: " + stateInBrowser); 
        case InputElementStateInBrowser.MULTI_FIELD_ERROR_MARKED: throw new Error("Unexpected client state: " + stateInBrowser); 

    }
}
