package com.kewebsi.html;

import com.kewebsi.errorhandling.CanHaveError;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.errorhandling.ErrorInfo;
import com.kewebsi.util.DebugUtils;

public class HtmlErrorWrapper extends HtmlDiv2 {

    HtmlTag wrappedTag;
    ErrorInfo oldError;

    HtmlSpanText errorDisplayTag;

    public HtmlErrorWrapper(HtmlTag wrappedTag) {
        this.wrappedTag = wrappedTag;
        addChild(wrappedTag);

        id = "errorWrapperfor-" + wrappedTag.getId();
        this.addCssClass(HtmlPageVarField.SMART_INPUT_DIV_CLASS);

        if (DebugUtils.DEBUG_CHECKS_ON) {
            if (!(wrappedTag instanceof CanHaveError)) {   // TODO: Allow all classes, if it is not instanceof CanHaveError, adapt error handling (to no errors possible)
                throw new CodingErrorException("HtmlTag does not implement CanHaveError");
            }
        }
    }

    @Override
    public void updateChildList() {
        var newError = getErrorInfo();
        boolean errorDisplayExistanceChanged;
        if (oldError == null && newError != null || oldError != null && newError == null) {
            errorDisplayExistanceChanged = true;
        } else {
            errorDisplayExistanceChanged = false;
        }

        if (errorDisplayExistanceChanged) {
            if (newError != null) {
                errorDisplayTag = new HtmlSpanText(getId() + "-spn", newError.getErrorText());
                addChild(errorDisplayTag);
            } else {
                removeChild(errorDisplayTag);
                errorDisplayTag = null;
            }
        } else {
            if (newError != null) {
                // When here, we need do display an error and have displayed an error before.
                // We might have to update the existing error message.
                errorDisplayTag.setText(newError.getErrorText());
            }
        }
    }

    @Override
    public void setContentOrGuiDefNotModified() {
        oldError = getErrorInfo();
    }


    public ErrorInfo getErrorInfo() {
        return ((CanHaveError) wrappedTag).getErrorInfoToDisplayToClient();
    }
}
