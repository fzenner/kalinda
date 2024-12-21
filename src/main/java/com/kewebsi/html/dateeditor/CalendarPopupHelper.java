package com.kewebsi.html.dateeditor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.inmsg.Msg_CalendarPopupChange;
import com.fzenner.datademo.web.inmsg.MsgPowerTable_CalendarPopupCreate;
import com.fzenner.datademo.web.outmsg.CalendarGuiDay;
import com.fzenner.datademo.web.outmsg.CalendarGuiDef;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.util.JsonUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarPopupHelper {
//    public static MsgAjaxResponse createCalendarPopup(JsonNode rootNode ) {
//        MsgPowerTable_CalendarPopupCreate msg = JsonUtils.parseJsonFromClient(rootNode, MsgPowerTable_CalendarPopupCreate.class);
//
//        int year = msg.year;
//        int month = msg.month;
//
//        var guiDef = getCalendarGuiDef(year, month, year, month);
//        var result = MsgAjaxResponse.createSuccessMsg();
//        result.setCustomCallbackData(guiDef);
//
//        return result;
//    }


    public static MsgAjaxResponse updateCalendarPopup(JsonNode rootNode ) {
        Msg_CalendarPopupChange msg = JsonUtils.parseJsonFromClient(rootNode, Msg_CalendarPopupChange.class);

        int currentYear = msg.currentYear;
        int setYear = msg.setYear;
        int diffYear = msg.diffYear;
        int currentMonth = msg.currentMonth;
        int diffMonth = msg.diffMonth;
        int day = 1; // Is irrelevant.

        int yearOut;
        int monthOut;

        if (setYear >=0) {
            yearOut = setYear;
            monthOut = currentMonth;
        } else if (diffYear != 0) {
            yearOut = currentYear + diffYear;
            monthOut = currentMonth;
        } else if (diffMonth != 0) {
            LocalDate in = LocalDate.of(currentYear, currentMonth, day);
            LocalDate out = in.plusMonths(diffMonth);
            yearOut = out.getYear();
            monthOut = out.getMonthValue();
        } else {
            yearOut = currentYear;
            monthOut = currentMonth;
        }

        var guiDef = getCalendarGuiDef(yearOut, monthOut);
        var result = MsgAjaxResponse.createSuccessMsg();
        result.setCustomCallbackData(guiDef);

        return result;
    }


    public static CalendarGuiDef getCalendarGuiDef(int displayYear, int displayMonth) {
        return getCalendarGuiDef(displayYear, displayMonth, displayYear, displayMonth);
    }

    /**
     * The calendar is thought to display more than one month at a time. That has not been tested yet,
     * Hence call it allways with start and end value being the same.
     * @param displayYearStart
     * @param displayMonthStart
     * @param displayYearEnd
     * @param displayMonthEnd
     * @return
     */
    protected static CalendarGuiDef getCalendarGuiDef(int displayYearStart, int displayMonthStart, int displayYearEnd, int displayMonthEnd) {

        LocalDate startDate = LocalDate.of(displayYearStart, displayMonthStart, 1);
        DayOfWeek startDayOfWeek = startDate.getDayOfWeek();
        int dayOfWeekInt = startDayOfWeek.getValue();
        int nbrOfDaysInWeekBeforStartOfTheMonth = dayOfWeekInt-1;

        LocalDate firstDisplayedDate = startDate.minusDays(nbrOfDaysInWeekBeforStartOfTheMonth);

        boolean done = false;
        LocalDate dateRunner = firstDisplayedDate; // Points to a Monday


        ArrayList<ArrayList<CalendarGuiDay>> days = new ArrayList<>();

        // var tbody = new SimpleTag("tbody");
        boolean endOfMonthReached = false;
        while (! endOfMonthReached) {
            // var trNode = tbody.childTag("tr");
            ArrayList<CalendarGuiDay> daysInWeek = new ArrayList<>(7);
            days.add(daysInWeek);


            for (int i = 1; i<=7; i++) {
                int dd = dateRunner.getDayOfMonth();
                int mm = dateRunner.getMonthValue();
                int yy = dateRunner.getYear();

                boolean dateIsBeforeStartMonth = false;
                if (mm < displayMonthStart || yy < displayYearStart) {
                    dateIsBeforeStartMonth = true;
                } else {
                    dateIsBeforeStartMonth = false;
                }


                CalendarGuiDay guiDay = new CalendarGuiDay(yy, mm, dd);
                daysInWeek.add(guiDay);

                dateRunner = dateRunner.plusDays(1);
                dd = dateRunner.getDayOfMonth();
                mm =  dateRunner.getMonthValue();
                yy = dateRunner.getYear();

                if ((mm > displayMonthEnd && !dateIsBeforeStartMonth) || yy > displayYearEnd) {
                    endOfMonthReached = true;
                }
            }
        }

        CalendarGuiDef result = new CalendarGuiDef(displayYearStart, displayMonthStart, days, 1);

        return result;

    }
}
