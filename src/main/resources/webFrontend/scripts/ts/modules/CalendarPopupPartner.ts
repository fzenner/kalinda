import { LocalDate } from "./dateTime";



/**
 * The GUI Element (editor) which opens the popup and controls it to a certain degree.
 */
export interface CalendarPopupPartner {
    popupDisplayedYear ?: number
    popupDisplayedMonth ?: number
    calendarPopup?: HTMLElement;
    setDateRemoveDateError(date: LocalDate);
    closeCalendarPopup() : void;
    getRelatedEditor() : HTMLElement;
}
