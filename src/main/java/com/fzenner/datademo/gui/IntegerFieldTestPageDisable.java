package com.fzenner.datademo.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.*;
import com.kewebsi.html.*;
import com.kewebsi.html.search.AtomicSearchConstraint;
import com.kewebsi.html.search.HtmlCheckbox;
import com.kewebsi.html.search.HtmlSearchCheckbox;
import com.kewebsi.service.FieldError;
import com.kewebsi.service.PageStateVarColdLink;
import com.kewebsi.service.PageVarIntColdLink;

public class IntegerFieldTestPageDisable extends HtmlPage {

    PageState pageState;
    private boolean valueIsRequired;

    public IntegerFieldTestPageDisable(String title, String pageName, boolean valueIsRequired) {
        super(title, pageName);
        this.valueIsRequired = valueIsRequired;

        pageState = new PageState("IntegerFieldTestPageDisabled");

        createHtmlObjects();

    }


    enum MyFieldNames {IntTestField, CheckBox};

    @Override
    public PageState getPageState() {
        return pageState;
    }


    public void createHtmlObjects() {


        HtmlLabel htmlLabel = new HtmlLabel("Field is disabled:");

        SimpleFieldAssistantBool sfiBool = new SimpleFieldAssistantBool(MyFieldNames.CheckBox);
        PageStateVarColdLink<Boolean> checkBoxVar = PageStateVarColdLink.createPageStateVar(pageState, sfiBool);
        checkBoxVar.setValueCore(false);
        HtmlCheckbox checkBox = new HtmlCheckbox(checkBoxVar,"myCheckBox");


        SimpleFieldAssistantInt sfi = new SimpleFieldAssistantInt(MyFieldNames.IntTestField) {
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
        intTestVar.setCheckRelevance( thisObj -> {return !checkBoxVar.getVal();});


        HtmlPageVarField testField = new HtmlPageVarField(intTestVar, "idShouldBeTakenFromVar") {
            @Override
            public boolean isDisabled() {
                return this.pageStateVar.isMeaningless();
            }
        };


        var buttonTest = new HtmlButtonStandard("myTest", "Test It") {
            public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
                // return StandardController.makeATest();
                return MsgAjaxResponse.createSuccessMsg();
            }
        };

        body.addChild(buttonTest);
        body.addChild(htmlLabel);
        body.addChild(checkBox);
        body.addChild(testField);
    }

}
