package com.kewebsi.service;

import com.kewebsi.errorhandling.ErrorUpdate;
import com.kewebsi.html.PageStateVarIntf;

public record PageVarErrorCore(PageStateVarIntf pageStateVar, PageVarErrorType errorType,String errorMsg) {


    public static PageVarErrorCore createAndLinkError(PageStateVarIntf pageStateVar, PageVarErrorType pageVarErrorType, String errorMsg) {
        PageVarErrorCore error = new PageVarErrorCore(pageStateVar, pageVarErrorType, errorMsg);
        pageStateVar.setError(error);
        return error;
    }

    public static PageVarErrorCore createAndLinkServerSideError(PageStateVarIntf pageStateVar, String errorMsg) {
        PageVarErrorCore error = new PageVarErrorCore(pageStateVar, PageVarErrorType.SERVER_SIDE_VALUE, errorMsg);
        pageStateVar.setError(error);
        return error;
    }

    public static PageVarErrorCore createAndLinkServerSideError(PageStateVarIntf pageStateVar, FieldError fieldError) {
        PageVarErrorCore error = new PageVarErrorCore(pageStateVar, PageVarErrorType.SERVER_SIDE_VALUE, fieldError.getMessage());
        pageStateVar.setError(error);
        return error;
    }

    public static PageVarErrorCore createAndLinkServerSideParsingError(PageStateVarIntf pageStateVar, String errorMsg) {
        PageVarErrorCore error = new PageVarErrorCore(pageStateVar, PageVarErrorType.SERVER_SIDE_PARSING, errorMsg);
        pageStateVar.setError(error);
        return error;
    }

    public boolean isClientSideError() {
        if (errorType == PageVarErrorType.CLIENT_DATA_NOT_TRANSMISSABLE) {
            return true;
        }
        return false;
    }

    public ErrorUpdate getErrorInfo() {
        return new ErrorUpdate(errorMsg);
    }

}
