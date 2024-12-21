package com.fzenner.datademo.web.outmsg;

/**
 *
 * @param y Year
 * @param mo Month - 1 based
 * @param d Day - 1 based
 * @param h Hour
 * @param mi Minute
 * @param s Seconds
 * @param n Nanoseconds
 */
public record DateTimeWire(Integer y, Integer mo, Integer d, Integer h, Integer mi, Integer s, Integer n) {


}
