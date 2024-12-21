package com.fzenner.datademo.web.outmsg;

import com.kewebsi.errorhandling.ErrorInfo;
import com.kewebsi.html.HtmlTag;

/**
 * Applicable modifications are non-null.
*/
public record InputFieldModifications(
        String id,
        HtmlTag.Visibility visibility,
        String errorMsg,
        String value
) {}
