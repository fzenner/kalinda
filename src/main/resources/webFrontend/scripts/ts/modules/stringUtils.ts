import { LocalDate, LocalTime, LocalDateTime } from "./dateTime";

export function fillWithLeadingZeros(upToLength: number, input: string) {
	const nbrOfZeros = input.length-upToLength;

	let zeros = "";
	if (nbrOfZeros > 0) {
		zeros = "0".repeat(nbrOfZeros);
	}

	const result = zeros + input;
	return result;
}


export function createStringWithLeadingZeros(upToLength: number, inNbr: number) {
    let inStr = inNbr.toString();
    let nbrOfZeros = upToLength - inStr.length;
    if (nbrOfZeros < 0) {
        let dududada = 10;
    }
    let outStr = "0".repeat(nbrOfZeros) + inStr;
    return outStr;
}


export function twoDigits(input: number) : string {
	if (input < 10) {
		return "0" + input;
	} else {
		return input.toString();
	}
}

/**
 * ```ts
 * a = ""               => if (a) => false
 * b = undefined        => if (b) => false
 * c = null             => if (c) => false
 * ```
 * if (a), if (b) as well as if (c) result to false. 
 * This function is to distinguish, that a string is "somehow" set, either with a character sequence 
 * or as empty sting.
 * The negation of this function means that the string is the empty string ("") or has a length > 1.
 * @param value 
 * @returns 
 */
export function isNullOrUndefined(value: any) {
	return value === undefined || value === null;
}

/**
 * See "isNullOrUndefined(...)"
 * @param value 
 * @returns 
 */
export function isWellDefined(value: any) {
	return value !== undefined && value !== null;
}



export function hasInfo(str: string) {
	if (str !== undefined && str !== null) {
		if (str.length > 0) {
			return true;
		}
	}
	return false;
}

export function hasNoInfo(str: string) {
    return ! hasInfo(str);
}


export function printDateDDMMYYYY(date: LocalDate) : string {
    let yearStr = date.year.toString();
    let monthStr = createStringWithLeadingZeros(2, date.month);
    let dayStr = createStringWithLeadingZeros(2, date.day);
    let out = dayStr + "." +  monthStr + "." + yearStr;
    return out;
}


export function printDateDDMMYYYY_HHMMSS(dateTime: LocalDateTime) : string {
    let yearStr = dateTime.date.year.toString();
    let monthStr = createStringWithLeadingZeros(2, dateTime.date.month);
    let dayStr = createStringWithLeadingZeros(2, dateTime.date.day);

    let hourStr = createStringWithLeadingZeros(2, dateTime.time.hour);
    let minuteStr = createStringWithLeadingZeros(2, dateTime.time.minute);
    let secondStr = createStringWithLeadingZeros(2, dateTime.time.second);
    let out = dayStr + "." +  monthStr + "." + yearStr + " " + hourStr + ":" + minuteStr + ":" + secondStr
    return out;
}

export function printDateTimeISO_DDMMYYYYTHHMMSS(dateTime: LocalDateTime) : string {
    let yearStr = dateTime.date.year.toString();
    let monthStr = createStringWithLeadingZeros(2, dateTime.date.month);
    let dayStr = createStringWithLeadingZeros(2, dateTime.date.day);

    let hourStr = createStringWithLeadingZeros(2, dateTime.time.hour);
    let minuteStr = createStringWithLeadingZeros(2, dateTime.time.minute);
    let secondStr = createStringWithLeadingZeros(2, dateTime.time.second);
    let out = dayStr + "." +  monthStr + "." + yearStr + "T" + hourStr + ":" + minuteStr + ":" + secondStr
    return out;
}


export function printTime24h(date: LocalTime) : string {
    let hourStr = createStringWithLeadingZeros(2, date.hour);
    let minuteStr = createStringWithLeadingZeros(2, date.minute);
    let out = hourStr + ":" +  minuteStr;
    return out;
}


export function stringToEnum<T>(enumObject: T, value: string): T[keyof T] {
    const enumValues = Object.values(enumObject) as string[];
    if (enumValues.includes(value)) {
        return value as T[keyof T];
    } else {
        throw new Error(`Invalid value: ${value}`);
    }
}

