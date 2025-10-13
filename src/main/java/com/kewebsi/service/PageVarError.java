package com.kewebsi.service;

import com.kewebsi.errorhandling.ErrorUpdate;
import com.kewebsi.html.PageStateVarIntf;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;

/**
 * A page var error is an error that can potentially be fixed by modifying a single page variable.
 * A typical example would be a number field that contains a character.
 * However, the validity of a page variable might be context sensitive. For example, the value of a day of month field
 * depends on the month being edited. Or the field is allowed to be empty depending on a checkbox somewhere else on
 * the page.
 */
// TODO: Consider making this a record or something similarly simple.
public class PageVarError extends Exception {

    public enum ErrorType {CLIENT_DATA_NOT_TRANSMISSABLE, SERVER_SIDE_PARSING, SERVER_SIDE_VALUE};

    PageVarErrorCore pageVarErrorCore;


//    protected PageStateVarIntf pageStateVar;
//    protected String errorMsg;
//    protected ErrorType errorType;

    /**
     * Creates a bidirectional link between the pageStateWar and the created error.
     * @param pageStateVar
     * @param errorMsg
     */

    public PageVarError(PageVarErrorCore pageVarErrorCore) {
        this.pageVarErrorCore = pageVarErrorCore;
    }

//    public PageVarError(@NotNull PageStateVarIntf pageStateVar, String errorMsg) {
//        super(errorMsg);
//
//        assert(pageStateVar != null);
//        this.pageStateVar = pageStateVar;
//        this.errorMsg = errorMsg;
//        pageStateVar.setError(this);
//        errorType = ErrorType.SERVER_SIDE_VALUE;
//    }

//    public PageVarError(@NotNull PageStateVarIntf pageStateVar, String errorMsg, ErrorType errorType) {
//        super(errorMsg);
//
//        assert(pageStateVar != null);
//        this.pageStateVar = pageStateVar;
//        this.errorMsg = errorMsg;
//        pageStateVar.setError(this);
//        this.errorType = errorType;
//    }
//
//
//    public PageVarError(@NotNull PageStateVarIntf pageStateVar, String errorMsg, boolean registerWithPageVar) {
//        super(errorMsg);
//
//        assert(pageStateVar != null);
//        this.pageStateVar = pageStateVar;
//        this.errorMsg = errorMsg;
//        if (registerWithPageVar) {
//            pageStateVar.setError(this);
//        }
//        errorType = ErrorType.SERVER_SIDE_VALUE;
//    }
//
//    public PageVarError(@NotNull PageStateVarIntf pageStateVar, FieldError fieldError) {
//        super(fieldError.getMessage());
//        assert(pageStateVar != null);
//        this.pageStateVar = pageStateVar;
//        this.errorMsg = fieldError.getMessage();
//        if (fieldError.getMessage() == null) {
//            System.out.println("OH NOOOOOH");
//        }
//        errorType = ErrorType.SERVER_SIDE_VALUE;
//        pageStateVar.setError(this);
//    }

    public static PageVarErrorCore createClientSideIncompleteOrParsingError(PageStateVarIntf pageStateVar) {
        var pvec = new PageVarErrorCore(pageStateVar,  PageVarErrorType.CLIENT_DATA_NOT_TRANSMISSABLE, "ClientSyncError");
        return pvec;
//        var error = new PageVarError(pageStateVar, "ClientSyncError", ErrorType.CLIENT_DATA_NOT_TRANSMISSABLE);
//        return error;
    }

    public static PageVarErrorCore createServerSideParsingError(PageStateVarIntf pageStateVar, String errorMsg) {
        var error = new PageVarErrorCore(pageStateVar, PageVarErrorType.SERVER_SIDE_PARSING, errorMsg);
        return error;
    }




    @Override
    public String toString() {
        return pageVarErrorCore.toString();
    };

//    public static PageVarError createException(PageStateVarIntf pageStateVar, String errorDetails) {
//        String errorMsg = createErrorMsgForUser(pageStateVar, errorDetails);
//        return new PageVarError(pageStateVar, errorMsg);
//    }


    public static String createErrorMsgForUser(PageStateVarIntf pageStateVar, String errorDetails ) {
        String r = "Error validating " + pageStateVar.getPageVarId() + ":" + errorDetails;
        return r;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        PageVarError that = (PageVarError) o;
//
//        if (! Objects.equals(pageStateVar, that.pageStateVar)) {
//            return false;
//        }
//
//        if (! Objects.equals(errorMsg, that.errorMsg)) {
//            return false;
//        }
//
//        if (this.errorType != that.errorType) {
//            return false;
//        }
//
//        if (that.errorMsg == null) {
//            System.out.println("OH GOTT!!");
//        }
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(pageStateVar, errorMsg);
//    }
//
//
//    public PageStateVarIntf getPageStateVar() {
//        return pageStateVar;
//    }
//
//    public String getErrorMsg() {
//        return errorMsg;
//    }
//
//    public ErrorUpdate getErrorInfo() {
//        return new ErrorUpdate(errorMsg);
//    }
//
//    public ErrorType getErrorType() {
//        return errorType;
//    }
//
//    public void setErrorType(ErrorType errorType) {
//        this.errorType = errorType;
//    }




//    public boolean isParsingError() {
//        return isParsingError;
//    }
//
//    public void setIsParsingError(boolean parsingError) {
//        isParsingError = parsingError;
//    }

}
