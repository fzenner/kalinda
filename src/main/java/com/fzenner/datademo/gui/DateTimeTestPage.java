package com.fzenner.datademo.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.*;
import com.kewebsi.errorhandling.DataOrError;
import com.kewebsi.html.*;
import com.kewebsi.html.search.HtmlCheckbox;
import com.kewebsi.service.*;

import static com.kewebsi.util.CommonUtils.isBetweenIncluding;

import java.time.LocalDateTime;
import java.util.function.Function;

public class DateTimeTestPage extends HtmlPage {

    PageState pageState;
    private boolean valueIsRequired;

    public DateTimeTestPage(String title, String pageName, boolean valueIsRequired) {
        super(title, pageName);
        this.valueIsRequired = valueIsRequired;

        pageState = new PageState("IntegerFieldTestPageDisabled");

        createHtmlObjects();

    }


    enum MyFieldNames {LocalDateTimeTestfield, CheckBox};

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


        SimpleFieldAssistantLocalDateTime sfi = new SimpleFieldAssistantLocalDateTime(MyFieldNames.LocalDateTimeTestfield) {
            @Override
            public FieldError validate(LocalDateTime val) {
                if (val == null) {
                    return super.validate(null); // Null handling is done in the standard validate method.
                } else {
                    int min = 1900;
                    int max = 2100;
                    if (!isBetweenIncluding(val.getYear(), min, max)) {
                        return new FieldError(this, "Value out of range. Must be between ", val.toString());
                    }
                    return super.validate(val);
                }
            }
        };
        sfi.setEditable(true);
        sfi.setCanBeNull(!valueIsRequired);

        PageStateVarColdLink<LocalDateTime> dateTimeTestVar = PageVarLocalDateTimeColdLink.createPageStateVar(pageState, sfi);

//        var dateTimeTestVar = new PageVarLocalDateTimeColdLink(pageState, sfi, "testpage") {
//            @Override
//            public boolean isRelevant() {
//                return !checkBoxVar.getVal();
//            };
//        };


        // XXXX TODO
        /* TODO:
            Do not calculate the disabled status dependent on the variable linked to the checkbox.
            Rather disable the variable in the checkbox action click.
            In this action, also the necessary cleaning (in case of errors on the variable) should be done.
            That is more direct as the cleaning of the variable upon creation of the GuiDef.

        */

        Function<Boolean, DataOrError<Boolean>> clearErrorWhenDisabled = (Boolean disabled) -> {
            if (disabled) {
                dateTimeTestVar.setIsRelevant(false);
            } else {
                dateTimeTestVar.setIsRelevant(true);
            }
            return new DataOrError<>(disabled);
        };

        checkBox.setCustomClickAction(clearErrorWhenDisabled);




        // dateTimeTestVar.setCheckRelevance( thisObj -> {return !checkBoxVar.getVal();});

        HtmlPageVarFieldDateTime dateTimeTestField = new HtmlPageVarFieldDateTime(dateTimeTestVar, "dateTimeTestField") {
            @Override
            public boolean isDisabled() {
                return this.pageStateVar.isMeaningless();
            }
        };


        var buttonTest = new HtmlButtonStandard("myTest", "Test It") {
            public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
                return MsgAjaxResponse.createSuccessMsg();
            }
        };

        body.addChild(buttonTest);
        body.addChild(htmlLabel);
        body.addChild(checkBox);
        body.addChild(dateTimeTestField);
    }

}
