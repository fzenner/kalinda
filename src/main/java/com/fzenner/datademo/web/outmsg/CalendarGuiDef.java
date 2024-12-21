package com.fzenner.datademo.web.outmsg;

import java.util.ArrayList;

public record CalendarGuiDef(
        int year,
        int month,
        ArrayList<ArrayList<CalendarGuiDay>> days,
        int dayToFocus
) implements CustomCallbackData{};

