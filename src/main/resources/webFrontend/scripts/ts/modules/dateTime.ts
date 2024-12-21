
export type LocalDateTime = {
    readonly date: LocalDate,
    readonly time: LocalTime
}

export type LocalDate = {
    readonly year: number
    readonly month: number
    readonly day: number

}


export type LocalTime = {
    readonly hour: number
    readonly minute: number
    readonly second: number
    readonly nanosecond: number

}

export function createLocalDateTime(year: number, month: number, day: number, hour: number, minute: number, second: number) : LocalDateTime{
    let ld: LocalDate =  {
        year: year,
        month: month,
        day: day
    }

    let lt: LocalTime = {
        hour: hour, 
        minute: minute,
        second: second,
        nanosecond: 0
    }

    let ldt : LocalDateTime = {
        date: ld,
        time: lt
    }

    return ldt;
}