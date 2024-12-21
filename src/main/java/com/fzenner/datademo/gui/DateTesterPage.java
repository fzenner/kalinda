package com.fzenner.datademo.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.FieldAssistantLocalDateTime;
import com.kewebsi.html.HtmlButtonStandard;
import com.kewebsi.html.HtmlPage;
import com.kewebsi.html.HtmlPageVarFieldDateTime;
import com.kewebsi.html.PageState;
import com.kewebsi.service.PageVarLocalDateColdLink;
import com.kewebsi.service.PageVarLocalDateTimeColdLink;

public class DateTesterPage extends HtmlPage  {

    private PageState pageState;

    enum MyOnlyFieldName {MY_ONLY_FIELD_NAME};
    protected MyOnlyFieldName myOnlyFieldName = MyOnlyFieldName.MY_ONLY_FIELD_NAME;

    public DateTesterPage() {
        super("DateTesterPage", "Date Tester");

        PageState pageState = new PageState("wtf");

        FieldAssistantLocalDateTime fieldAssistantLocalDateTime = new FieldAssistantLocalDateTime(myOnlyFieldName);
        PageVarLocalDateTimeColdLink pageVarLocalDateColdLink = new PageVarLocalDateTimeColdLink(pageState, fieldAssistantLocalDateTime);
        HtmlPageVarFieldDateTime dateTimeField = new HtmlPageVarFieldDateTime(pageVarLocalDateColdLink, "dateTimeTester");


        HtmlButtonStandard dummyButton = new HtmlButtonStandard("dummyButton", "Dummy Button") {
            @Override
            public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
                return MsgAjaxResponse.createSuccessMsg();
            }
        };

        body.addChild(dateTimeField);
        body.addChild(dummyButton);

    }

    @Override
    public PageState getPageState() {
        return pageState;
    }


}
