package com.kewebsi.util;

import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.errorhandling.StringParsingError;
import com.kewebsi.html.dateeditor.CalendarController;
import com.kewebsi.html.dateeditor.StringParsingResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class KewebsiDateUtils {
    /**
     * Parses a date string in one of the forms:
     * day-month-year, // German
     * month-day-year, // American
     * year-month-day, // Technical/sortable
     * year-day-month, // Uncommon
     * day-month, // The year is then assumed the current year.
     * month-day // The year is then assumed the current year.
     * The separator is any sequence of non-digit characters.

     */
    public static LocalDate parseDate(String dateStr, boolean monthBeforeDay, boolean yearFirst, CalendarController.YEAR_DIGITS yearDigits) throws StringParsingError {


        StringParsingResult firstIntResult = getNextInt(dateStr, 0);

        if (firstIntResult.idx < 0) {
            throw new StringParsingError("Invalid data format", dateStr);
        }

        StringParsingResult secondIntResult = getNextInt(dateStr, firstIntResult.idx);

        if (secondIntResult.idx < 0) {
            throw new StringParsingError("Invalid data format", dateStr);
        }

        StringParsingResult thirdIntResult = getNextInt(dateStr, secondIntResult.idx);

        boolean yearProvided = true;
        if (thirdIntResult.idx < 0) {
            yearProvided = false;
        }

        if (! yearProvided && yearDigits== CalendarController.YEAR_DIGITS.Required) {
            throw new StringParsingError("Invalid data format. Year value must be provided", dateStr);
        }

        if (yearProvided && yearDigits== CalendarController.YEAR_DIGITS.Forbidden) {
            throw new StringParsingError("Invalid data format. Unexpected year value provided", dateStr);
        }

        int year;
        int month;
        int day;

        if (yearProvided) {
            if (yearFirst) {
                year = firstIntResult.result;
                if (monthBeforeDay) {
                    month = secondIntResult.result;
                    day = thirdIntResult.result;
                } else {
                    day = secondIntResult.result;
                    month = thirdIntResult.result;
                }
            } else {
                if (monthBeforeDay) {
                    month = firstIntResult.result;
                    day = secondIntResult.result;
                } else {
                    day = firstIntResult.result;
                    month = secondIntResult.result;
                }
                year = thirdIntResult.result;
            }
        } else {
            year = LocalDate.now().getYear();
            if (monthBeforeDay) {
                month = firstIntResult.result;
                day = secondIntResult.result;
            } else {
                day = firstIntResult.result;
                month = secondIntResult.result;
            }
        }

        LocalDate result;
        try {
            result = LocalDate.of(year, month, day);
        } catch (Exception e) {
            throw new StringParsingError(e);
        }
        return result;
    }


    public static LocalTime parseTime(String timeStr) throws StringParsingError {
        return parseTime(timeStr, 0);
    }

    public static LocalTime parseTime(String timeStr, int startIdx) throws StringParsingError {
        StringParsingResult hours = getNextInt(timeStr, startIdx);

        if (hours.idx < 0) {
            throw new StringParsingError("Invalid data format", timeStr);
        }

        StringParsingResult minutes = getNextInt(timeStr, hours.idx);

        if (minutes.idx < 0) {
            throw new StringParsingError("Invalid data format", timeStr);
        }

        int amOptions = 0;  // 0 = no AM/PM, 1 = AM, 2 = PM

        String timeStringUpperCase = timeStr.toUpperCase();
        if (timeStringUpperCase.contains("AM")) {
            amOptions = 1;
        } else {
            if (timeStringUpperCase.contains("PM")) {
                amOptions = 2;
            }
        }

        int hh = hours.result;
        int mm = minutes.result;

        if (amOptions > 0 && hours.result > 12) {
            throw new StringParsingError("Do not specify hours > 12 when specifying AM or PM ", String.valueOf(hh));
        }

        if (amOptions == 2) {
            hh +=12;
        }

        return LocalTime.of(hh, mm);

    }

    public static LocalDateTime parseDateTime(String dateTimeStr, boolean monthBeforeDay, boolean yearFirst, CalendarController.YEAR_DIGITS yearDigits) throws StringParsingError {

        if (yearDigits == CalendarController.YEAR_DIGITS.Optional) {
            throw new CodingErrorException(yearDigits + " not supported here.");
        }

        StringParsingResult firstIntResult = getNextInt(dateTimeStr, 0);

        if (firstIntResult.idx < 0) {
            throw new StringParsingError("Invalid data format", dateTimeStr);
        }

        StringParsingResult secondIntResult = getNextInt(dateTimeStr, firstIntResult.idx);

        if (secondIntResult.idx < 0) {
            throw new StringParsingError("Invalid data format", dateTimeStr);
        }

        StringParsingResult thirdIntResult = getNextInt(dateTimeStr, secondIntResult.idx);

        if (thirdIntResult.idx < 0) {
            throw new StringParsingError("Invalid data format", dateTimeStr);
        }

        StringParsingResult fourthIntResult = getNextInt(dateTimeStr, thirdIntResult.idx);

        if (fourthIntResult.idx < 0) {
            throw new StringParsingError("Invalid data format", dateTimeStr);
        }

        StringParsingResult fifthIntResult = null;

        if (yearDigits == CalendarController.YEAR_DIGITS.Required) {

            fifthIntResult = getNextInt(dateTimeStr, fourthIntResult.idx);
            if (fifthIntResult.idx < 0) {
                throw new StringParsingError("Invalid data format", dateTimeStr);
            }
        }


        boolean yearProvided = (yearDigits == CalendarController.YEAR_DIGITS.Required);

        int year;
        int month;
        int day;

        if (yearProvided) {
            if (yearFirst) {
                year = firstIntResult.result;

                // Date calculations in the database can crash due to a limited number range.
                // Hence we restrict date values to 4 digits.
                if (year < -9999 || year > 9999) {
                    throw new StringParsingError("Invalid data format. Year out of range", dateTimeStr);
                }

                if (monthBeforeDay) {
                    month = secondIntResult.result;
                    day = thirdIntResult.result;
                } else {
                    day = secondIntResult.result;
                    month = thirdIntResult.result;
                }
            } else {
                if (monthBeforeDay) {
                    month = firstIntResult.result;
                    day = secondIntResult.result;
                } else {
                    day = firstIntResult.result;
                    month = secondIntResult.result;
                }
                year = thirdIntResult.result;
            }
        } else {
            year = LocalDate.now().getYear();
            if (monthBeforeDay) {
                month = firstIntResult.result;
                day = secondIntResult.result;
            } else {
                day = firstIntResult.result;
                month = secondIntResult.result;
            }
        }

        int hour;
        int minute;
        if (yearProvided) {
            hour = fourthIntResult.result;
            minute = fifthIntResult.result;;
        } else {
            hour = thirdIntResult.result;
            minute = fourthIntResult.result;
        }

        LocalDateTime result;
        try {
            result = LocalDateTime.of(year, month, day,hour,minute);
        } catch (Exception e) {
            throw new StringParsingError(e);
        }
        return result;
    }


    public static LocalDate parseGermanDate(String dateStr) throws StringParsingError {
        LocalDate result = KewebsiDateUtils.parseDate(dateStr.trim(), false, false, CalendarController.YEAR_DIGITS.Required);
        return result;
    }


    public static LocalDateTime parseGermanDateTime(String dateStr) throws StringParsingError {
        LocalDateTime result = KewebsiDateUtils.parseDateTime(dateStr.trim(), false, false, CalendarController.YEAR_DIGITS.Required);
        return result;
    }

    public static String printLocalDate(LocalDate localDate, boolean monthBeforeDay, boolean yearFirst, int yearDigits, String divider) {
        String yearStr = String.valueOf(localDate.getYear());
        int length = yearStr.length();

        if (yearDigits <=2) {
            yearStr = yearStr.substring(length - 2, length);
        }

        String monthStr = String.valueOf(localDate.getMonthValue());
        String dayStr = String.valueOf(localDate.getDayOfMonth());


        String result = getStringForYearMonthDay(yearStr, monthStr, dayStr, monthBeforeDay, yearFirst, divider);

        return result;
    }


    public static String printLocalDateTime(LocalDateTime localDate, boolean monthBeforeDay, boolean yearFirst, int yearDigits, boolean printSeconds, boolean printNanoSeconds, String divider) {
        // TODO SPEEDUP: String operations can be drastically simplified.
        String yearStr = String.valueOf(localDate.getYear());
        int length = yearStr.length();

        if (yearDigits <=2) {
            yearStr = yearStr.substring(length - 2, length);
        }

        String monthStr = String.valueOf(localDate.getMonthValue());
        String dayStr = String.valueOf(localDate.getDayOfMonth());
        String hourStr = String.valueOf(localDate.getHour());
        String minuteStr = String.valueOf(localDate.getMinute());
        String secondStr = String.valueOf(localDate.getSecond());
        String nanoSecondStr = String.valueOf(localDate.getNano());



        String result = getStringForYearMonthDay(yearStr, monthStr, dayStr, monthBeforeDay, yearFirst, divider);

        hourStr =  CommonUtils.fillWithLeadingZeros(2, hourStr);
        minuteStr =  CommonUtils.fillWithLeadingZeros(2, minuteStr);
        secondStr =  CommonUtils.fillWithLeadingZeros(2, secondStr);
        nanoSecondStr =  CommonUtils.fillWithLeadingZeros(9, nanoSecondStr);

        result += " " + hourStr + ":" + minuteStr;

        if (printSeconds) {
            result += ":" + secondStr;
            if (printNanoSeconds) {
                result += "." + nanoSecondStr;
            }
        }

        return result;
    }

    private static String getStringForYearMonthDay(String yearStr, String monthStr, String dayStr, boolean monthBeforeDay, boolean yearFirst, String divider) {
        String monthStringTwoDigits = CommonUtils.fillWithLeadingZeros(2, monthStr);
        String dayStringTwoDigits = CommonUtils.fillWithLeadingZeros(2, dayStr);


        String result;
        if (yearFirst) {
            if (monthBeforeDay) {
                result = yearStr + divider + monthStr + divider + dayStr;
            } else {
                result = yearStr + divider + dayStringTwoDigits + divider + monthStringTwoDigits;
            }

        } else {
            if (monthBeforeDay) {
                result = monthStr + divider + dayStr + divider + yearStr;
            } else {
                result = dayStringTwoDigits + divider + monthStringTwoDigits + divider + yearStr;
            }
        }
        return result;
    }


//    public static String printDateTime(LocalDateTime localDate, boolean monthBeforeDay, boolean yearFirst, int yearDigits, String divider) {
//        String res
//    }

    public static String printGermanDate(LocalDate localDate) {
        return printLocalDate(localDate, false, false, 4, ".");
    }

    public static String printGermanDateTime(LocalDateTime localDate) {
        return printLocalDateTime(localDate, false, false, 4, true, true,".");
    }

    public static String printTime24h(LocalTime localTime) {
        return CommonUtils.twoDigits(localTime.getHour()) + ":" + CommonUtils.twoDigits(localTime.getMinute());
    }


    /**
     * We start at the beginning of the integer or at a non-digit in front of the integer.
     * @param dateStr
     * @param idx
     * @return
     */
    public static StringParsingResult getNextInt(String dateStr, int idx) {
        String outStr = "";
        boolean digitStarted = false;
        boolean digitCompleted = false;
        while (idx < dateStr.length() && !digitCompleted) {
            char run = dateStr.charAt(idx);
            if (isDigit(run)) {
                digitStarted = true;
                outStr += run;
            } else {
                if (digitStarted) {
                    digitCompleted = true;
                }
            }

            if (idx == dateStr.length()-1 && digitStarted) {
                digitCompleted = true;
            }

            idx ++;
        }
        StringParsingResult result = null;
        if (!digitCompleted) {
            result = new StringParsingResult(-1 ,-1);
        } else {
            int outInt = Integer.parseInt(outStr);
            result = new StringParsingResult(idx ,outInt);
        }
        return result;
    }

    public static boolean isDigit(char c) {
        if (c >='0' && c <='9') {
            return true;
        }
        return false;
    }


}
