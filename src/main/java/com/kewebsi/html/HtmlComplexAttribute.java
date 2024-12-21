package com.kewebsi.html;

import com.fzenner.datademo.web.inmsg.ServerMessageHandler;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.util.CommonUtils;
import com.kewebsi.util.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class allows for specifying parameters to be sent from the browser to the server that are not specified
 * in JavaScript but rather only on the server. The parameters will be wrapped in a "fixed" JSON object that
 * is provided to the client side as a single HTML attribute of the respective HTML element.
 */
public class HtmlComplexAttribute {

    protected ArrayList<String> keyValues;

    public HtmlComplexAttribute(String... keyValue) {
        if (keyValue.length % 2 != 0) {
            throw new CodingErrorException("Uneven key value pairs. Nbr of elements: " + keyValue.length);
        }
        keyValues = CommonUtils.arrayToArrayList(keyValue);
    }

    public void addAll(String[] keyValue) {
        if (keyValue.length % 2 != 0) {
            throw new CodingErrorException("Uneven key value pairs. Nbr of elements: " + keyValue.length);
        }
        if (keyValues == null) {
            keyValues = CommonUtils.arrayToArrayList(keyValue);
        } else {
            Collections.addAll(keyValues, keyValue);
        }
    }


    public static String getCompoundKey() {
        return ServerMessageHandler.FIXED_PARAMS_TO_SEND_DTO_ATTR_NAME;
    }


    public String getCompoundValue() {
        return JsonUtils.createJsonStrDoubleQuote(keyValues);
    }

}
