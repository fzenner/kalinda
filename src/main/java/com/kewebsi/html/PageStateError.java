package com.kewebsi.html;

import com.kewebsi.service.PageVarError;
import com.kewebsi.service.PageVarErrorCore;

public class PageStateError {

    protected PageState pageState;
    protected String errorMsg;

    public PageStateError(PageState pageState, String errorMsg) {
        this.pageState = pageState;
        this.errorMsg = errorMsg;
    }


    public PageStateError(PageVarErrorCore pageVarError) {
        pageState = pageVarError.pageStateVar().getPageState();
        assert(pageState != null);
        errorMsg = "Input error: " + pageVarError.errorMsg() + ". Check input values. ";
    }


    public PageState getPageState() {
        return pageState;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
