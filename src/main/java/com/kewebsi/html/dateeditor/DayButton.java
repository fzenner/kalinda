package com.kewebsi.html.dateeditor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.html.HtmlButtonStandard;

public abstract class DayButton extends HtmlButtonStandard {

    protected int year;
    protected int month;
    protected int day;


    public DayButton(String id, String label, int year, int month, int day) {
        super(id, label);
        this.year = year;
        this.month = month;
        this.day = day;
    }

}
