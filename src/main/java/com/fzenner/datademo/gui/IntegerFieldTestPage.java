package com.fzenner.datademo.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.*;
import com.kewebsi.errorhandling.StringParsingError;
import com.kewebsi.html.HtmlButtonStandard;
import com.kewebsi.html.HtmlPage;
import com.kewebsi.html.HtmlPageVarField;
import com.kewebsi.html.PageState;
import com.kewebsi.service.FieldError;
import com.kewebsi.service.PageStateVarColdLink;
import com.kewebsi.service.PageVarIntColdLink;

public class IntegerFieldTestPage extends HtmlPage {

    PageState pageState;
    private boolean valueIsRequired;

    public IntegerFieldTestPage(String title, String pageName, boolean valueIsRequired) {
        super(title, pageName);
        this.valueIsRequired = valueIsRequired;

        pageState = new PageState("IntegerFieldTestPage");

        createHtmlObjects();

    }

    enum MyFieldNames {TestField};

    @Override
    public PageState getPageState() {
        return pageState;
    }


    public void createHtmlObjects() {

        SimpleFieldAssistantInt sfi = new SimpleFieldAssistantInt(MyFieldNames.TestField) {
            @Override
            public FieldError validate(Integer val) {
                if (val > 3000) {
                    return new FieldError(this, "Value too large. Max val is 3000", val.toString());
                }
                return super.validate(val);
            }
        };
        sfi.setEditable(true);
        sfi.setCanBeNull(!valueIsRequired);
        PageStateVarColdLink<Integer> intTestVar = PageVarIntColdLink.createPageStateVar(pageState, sfi);


        HtmlPageVarField testField = new HtmlPageVarField(intTestVar);




        var buttonTest = new HtmlButtonStandard("myTest", "Test It") {
            public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
                return StandardController.makeATest();
            }
        };

        body.addChild(buttonTest);
        body.addChild(testField);
    }

}
