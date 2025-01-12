import { CanBeDisabled, ErrorUpdate, GuiDef, InputFieldGuiDef } from "./messageTypes";
import { isWellDefined } from "./stringUtils";
import { WebCompSupportingUpdates } from "./webcomps";


export function updateErrorGivenServerUpdate(elm: WebCompSupportingUpdates, errorUpdate: ErrorUpdate, disabledUpdate: boolean, valueUpdate: any) {
	
    // const inputFieldGuiDef: CanBeDisabled = guiDef.tagSpecificData;
    if (isWellDefined(disabledUpdate)) {
        if (disabledUpdate) {
            if (elm.hasError()) {
                elm.clearData()  // We do not show values with errors  when disabled.
            }
            elm.clearError()  // We also do not show and forget the errors when disabled.
        }
    }

    //
    // Errors are similar to data. When there is no change, nothing is being communicated.
    // "undefined" hence means "no change" - but not "no error"!
    //
    if (isWellDefined(errorUpdate)) {
        // const errorUpdate = guiDef.errorUpdate;
        if (errorUpdate) {
            if (errorUpdate.wireNull) {
                elm.clearError();
            } else {
                if (!errorUpdate.errorText) {  // if on string: false if empty, null or undefined
                    errorUpdate.errorText = "Unspecified Error from Server"
                }
                elm.setErrorMessageForOverallComponent(errorUpdate.errorText); 
            }
        } else {
            console.warn("Error info null received. Null properties are not expected to be received from the server");
        }
    } else {
        // If there is no error info from the server. If there are multiple fields connected to a single (server-side) page variable,
        // there might be the following situation:
        // here was a syntax error on field one.
        // The input was corrected in field two.
        // The server sends no error information, since the error was client-side.
        // The error on field one needs to be cleared, but is not, since no client side activity (change and focus loss) is done on field one.
        // The clearing of the error is done by recognizing that we recognize the following state:
        // - There is no server side error in the update
        // - There is a new newValue in the update (the framework must make sure, that the server does not send an updated newValue when
        //   the request for the update at hand was the notification of a client side error)
        // - We have a client side error on the input field
        if (elm.hasError()) {
            if (elm.getError().isClientSideError) {
                if (isWellDefined(valueUpdate)) {
                    elm.clearError()
                }
            }
        }
    }
}
