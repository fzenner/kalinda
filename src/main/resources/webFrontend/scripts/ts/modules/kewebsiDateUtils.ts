import {LocalDate, LocalTime, LocalDateTime, createLocalDateTime} from "./dateTime.js"
import { DateTimeWire, DateTimeWireOrNull } from "./jointTypes.js";
import {fillWithLeadingZeros, isNullOrUndefined, twoDigits} from "./stringUtils.js"


type StringParsingResult = {
    idx: number;
    result : number;
}

type ParsingError = {
    errorMessage: string
    errorValue?: string
}

export function isParsingError<T extends object>( locatDateOrParsingError: T | ParsingError) : locatDateOrParsingError is ParsingError {
    return 'errorMessage' in locatDateOrParsingError;
}

const YEAR_REQUIRED = "YEAR_REQUIRED";
const YEAR_FORBIDDEN = "YEAR_FORBIDDEN";
const YEAR_OPTIONAL = "YEAR_OPTIONAL";



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
export function parseDate(dateStr: string, monthBeforeDay: boolean, yearFirst: boolean, yearDigitsMode: string) : (LocalDate | ParsingError) {


    const firstIntResult : StringParsingResult = getNextInt(dateStr, 0);

    if (firstIntResult.idx < 0) {
        return {errorMessage: "Invalid date format: ", errorValue:  dateStr};
    }

    const secondIntResult: StringParsingResult = getNextInt(dateStr, firstIntResult.idx);

    if (secondIntResult.idx < 0) {
        return {errorMessage: "Invalid date format. ", errorValue:  dateStr};
    }

    const thirdIntResult: StringParsingResult = getNextInt(dateStr, secondIntResult.idx);

    const yearProvided = thirdIntResult.idx < 0 ? false : true;

    if (! yearProvided && yearDigitsMode === YEAR_REQUIRED) {
        return {errorMessage: "Invalid date format. Year value must be provided. "};
    }

    if (yearProvided && yearDigitsMode === YEAR_FORBIDDEN) {
        return {errorMessage: "Invalid date format. Unexpected year value provided."};
    }

    let year = 0;
    let month = 0;
    let day = 0;

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
        const year = new Date().getFullYear()
        if (monthBeforeDay) {
            month = firstIntResult.result;
            day = secondIntResult.result;
        } else {
            day = firstIntResult.result;
            month = secondIntResult.result;
        }
    }

    const resultDate : LocalDate = {year: year, month: month, day: day}; // Note that we do not check here, whether the date is possible. (E.g. 33.1.2020)
    return resultDate;
}


/**
 * Parses the string in ISO8601 format of the variation yyyy-mm-ddThh:mm:ss.f
 * @param dtstr 
 * @returns 
 */
export function parseIsoDateTime(dtstr: string) : LocalDateTime {
    let strArr: string[]  = dtstr.split(/[:. T-]/);

		
    let year = parseFloat(strArr[0]);;
    let month = 1;
    let day = 1
    let hour = 0;
    let minute = 0;
    let seconds = 0;
    let fraction = 0;
    
    if (strArr.length >= 2) {
    	month = parseFloat(strArr[1]);
    }
    if (strArr.length >= 3) {
    	day = parseFloat(strArr[2]);
    }
    if (strArr.length >= 4) {
    	hour = parseFloat(strArr[3]);
    }
    if (strArr.length >= 5) {
    	minute = parseFloat(strArr[4]);
    }
    if (strArr.length >= 6) {
    	seconds = parseFloat(strArr[5]);
    }
    if (strArr.length == 7) {
        let fractionStr = strArr[6];
        let fractionTop = parseFloat(fractionStr);
        let fractionBottom = Math.pow(10, fractionStr.length);
        fraction = fractionTop/fractionBottom;
    }

    let ldt = createLocalDateTime(year, month, day, hour, minute, seconds);
    
    return ldt;

}


export function  parseTime(timeStr: string) : (LocalTime | ParsingError) {
    return parseTimeStartingFromIdx(timeStr, 0);
}

function parseTimeStartingFromIdx(timeStr: string, startIdx: number) : (LocalTime | ParsingError) {
    const hours : StringParsingResult  = getNextInt(timeStr, startIdx);

    if (hours.idx < 0) {
        return {errorMessage: "Invalid time format."};
    }

    const minutes : StringParsingResult  = getNextInt(timeStr, hours.idx);

    if (minutes.idx < 0) {
        return {errorMessage: "Invalid time format."};
    }



    let amOptions = 0;  // 0 = no AM/PM, 1 = AM, 2 = PM

    const timeStringUpperCase = timeStr.toUpperCase();
    if (timeStringUpperCase.includes("AM")) {
        amOptions = 1;
    } else {
        if (timeStringUpperCase.includes("PM")) {
            amOptions = 2;
        }
    }

    let hh = hours.result;
    const mm = minutes.result;

    if (amOptions > 0 && hours.result > 12) {
        return {errorMessage: "Invalid data format.", errorValue: hh.toString()};
    }

    if (amOptions == 2) {
        hh +=12;
    }

    if (mm < 0 || mm > 59) {
        return {errorMessage: "Invalid time format. Minutes out of range."};
    }

    let result: LocalTime = {hour: hh, minute: mm, second:0, nanosecond: 0 }
    return result;

}

export function parseDateTimeIgnoresHoursAndMinutesXXX(dateTimeStr: string, monthBeforeDay: boolean,  yearFirst: boolean, yearDigitsMode: string) : (LocalDateTime | ParsingError) {

    const firstIntResult : StringParsingResult = getNextInt(dateTimeStr, 0);

    if (firstIntResult.idx < 0) {
        return {errorMessage: "Invalid datetime format: ", errorValue:  dateTimeStr};
    }

    const secondIntResult: StringParsingResult = getNextInt(dateTimeStr, firstIntResult.idx);

    if (secondIntResult.idx < 0) {
        return {errorMessage: "Invalid datetime format. ", errorValue:  dateTimeStr};
    }

    const thirdIntResult: StringParsingResult = getNextInt(dateTimeStr, secondIntResult.idx);

    const yearProvided = thirdIntResult.idx < 0 ? false : true;

    if (! yearProvided && yearDigitsMode === "YEAR_REQUIRED") {
        return {errorMessage: "Invalid datetime format. Year value must be provided. "};
    }

    if (yearProvided && yearDigitsMode === "YEAR_FORBIDDEN") {
        return {errorMessage: "Invalid datetime format. Unexpected year value provided."};
    }

    let year = 0;
    let month = 0;
    let day = 0;

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
        const year = new Date().getFullYear()
        if (monthBeforeDay) {
            month = firstIntResult.result;
            day = secondIntResult.result;
        } else {
            day = firstIntResult.result;
            month = secondIntResult.result;
        }
    }

    const resultDate : LocalDate = {year: year, month: month, day: day}; // Note that we do not check here, whether the date is possible. (E.g. 33.1.2020)
    const resultTime: LocalTime = {hour: 0, minute: 0, second: 0, nanosecond: 0}; // Note that we do not check here, whether the time is possible. (E.g. 33:70h)
    const result:  LocalDateTime = { date: resultDate, time: resultTime } ;

    return result;
}


export function  parseGermanDate(dateStr: string) : (LocalDate | ParsingError) {
   return parseDate(dateStr.trim(), false, false, YEAR_REQUIRED);
}


export function parseGermanDateTime(dateStr: string) : (LocalDateTime | ParsingError) {
    return  parseDateTimeIgnoresHoursAndMinutesXXX(dateStr.trim(), false, false, YEAR_REQUIRED);
}

function printLocalDate(localDate: LocalDate, monthBeforeDay: boolean, yearFirst: boolean, yearDigits: number, divider: string) : string {
    let yearStr = localDate.year.toString();
    const length = yearStr.length;

    if (yearDigits <=2) {
        yearStr = yearStr.substring(length - 2, length);
    }

    const monthStr = localDate.month.toString();
    const dayStr = localDate.day.toString();

    const result = getStringForYearMonthDay(yearStr, monthStr, dayStr, monthBeforeDay, yearFirst, divider);

    return result;
}


function printLocalDateTime(localDate: LocalDateTime, monthBeforeDay: boolean, yearFirst: boolean, yearDigits: number, printSeconds: boolean, printNanoSeconds: boolean, divider: string) : string {
    // TODO SPEEDUP: String operations can be drastically simplified.
    let yearStr = localDate.date.year.toString()
    const length = yearStr.length;

    if (yearDigits <=2) {
        yearStr = yearStr.substring(length - 2, length);
    }

    let monthStr = localDate.date.month.toString();
    let dayStr = localDate.date.day.toString();
    let hourStr = localDate.time.hour.toString();
    let minuteStr = localDate.time.minute.toString();
    let secondStr = localDate.time.second.toString();
    let nanoSecondStr = localDate.time.nanosecond.toString();   



    let result = getStringForYearMonthDay(yearStr, monthStr, dayStr, monthBeforeDay, yearFirst, divider);

    hourStr =  fillWithLeadingZeros(2, hourStr);
    minuteStr =  fillWithLeadingZeros(2, minuteStr);
    secondStr =  fillWithLeadingZeros(2, secondStr);
    nanoSecondStr =  fillWithLeadingZeros(9, nanoSecondStr);

    result += " " + hourStr + ":" + minuteStr;

    if (printSeconds) {
        result += ":" + secondStr;
        if (printNanoSeconds) {
            result += "." + nanoSecondStr;
        }
    }

    return result;
}

function getStringForYearMonthDay(yearStr: string, monthStr: string, dayStr: string, monthBeforeDay: boolean, yearFirst: boolean, divider: string) : string {
    const monthStringTwoDigits = fillWithLeadingZeros(2, monthStr);
    const dayStringTwoDigits = fillWithLeadingZeros(2, dayStr);


    let result: string;
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


function printGermanDate(localDate: LocalDate) : string {
    return printLocalDate(localDate, false, false, 4, ".");
}

function printGermanDateTime(localDate: LocalDateTime) : string {
    return printLocalDateTime(localDate, false, false, 4, true, true,".");
}

function printTime24h(localTime: LocalTime) : string{
    return twoDigits(localTime.hour) + ":" + twoDigits(localTime.minute);
}


/**
 * We start at the beginning of the integer or at a non-digit in front of the integer.
 * @param dateStr
 * @param idx
 * @return
 */
function  getNextInt(dateStr: string, idx: number) : StringParsingResult {
    let  outStr = "";
    let digitStarted = false;
    let digitCompleted = false;
    while (idx < dateStr.length && !digitCompleted) {
        let run = dateStr.charAt(idx);
        if (isDigit(run)) {
            digitStarted = true;
            outStr += run;
        } else {
            if (digitStarted) {
                digitCompleted = true;
            }
        }

        if (idx == dateStr.length-1 && digitStarted) {
            digitCompleted = true;
        }

        idx ++;
    }
    let result: StringParsingResult;
    if (!digitCompleted) {
        result = {idx: -1, result: -1};
    } else {
        let outInt = Number.parseInt(outStr);
        result = {idx: idx , result: outInt};
    }
    return result;
}

function isDigit(c: string) : boolean {
    if (c.length != 1) {
        throw Error("Unexpected string length. Expected length of 1 but got the following string: " + c);
    }

    const reg: RegExp = /\d/;

    return reg.test(c);
}

/**
 * 
 * @param dtwOrNull Must not be null. WireData is never null but might have nulls inside.
 * @returns 
 */
export function dateTimeWireToLocalDateTime(dtwOrNull: DateTimeWireOrNull) : LocalDateTime {
    if (isNullOrUndefined(dtwOrNull.value)) {
        return null;
    }
    const dtw = dtwOrNull.value;
    const localDate : LocalDate = {year: dtw.y, month: dtw.mo, day: dtw.d};
    const localTime : LocalTime = {hour: dtw.h, minute: dtw.mi, second: dtw.s, nanosecond: dtw.n};
    const localDateTime: LocalDateTime = {date: localDate, time: localTime}; 
    return localDateTime;
}

export function  localDateTimeToDateTimeWireOrNull(localDateTime: LocalDateTime ) : DateTimeWireOrNull {
    
    
    let dateTimeWire = localDateTime === null ? null :  {y: localDateTime.date.year, mo: localDateTime.date.month, d: localDateTime.date.day,
        h: localDateTime.time.hour, mi: localDateTime.time.minute, s: localDateTime.time.second, n: localDateTime.time.nanosecond }
    let result : DateTimeWireOrNull = {value: dateTimeWire}
    return result;;
}