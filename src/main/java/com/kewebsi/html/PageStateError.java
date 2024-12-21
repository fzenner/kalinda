package com.kewebsi.html;

import com.kewebsi.service.PageVarError;

public class PageStateError {

    protected PageState pageState;
    protected String errorMsg;

    public PageStateError(PageState pageState, String errorMsg) {
        this.pageState = pageState;
        this.errorMsg = errorMsg;
    }


    public PageStateError(PageVarError pageVarError) {
        pageState = pageVarError.getPageStateVar().getPageState();
        assert(pageState != null);
        errorMsg = "Input error: " + pageVarError.getErrorMsg() + ". Check input values. ";
    }


    public PageState getPageState() {
        return pageState;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
