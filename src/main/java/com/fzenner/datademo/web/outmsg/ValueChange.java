package com.fzenner.datademo.web.outmsg;

/**
 * Helps to communicate newValue changes.
 * A variable of the type ValueChange has ghe following semantic:
 * If it is null,
 * ValueChange valueChange = null
 * the newValue has not changed.
 * If the newValue is null,
 * ValueChange valueChange = new ValueChange(null)
 * then the value has changed to the new value null.
 * In any other case,
 * ValueChange valueChange = new ValueChange(newValue)
 * the newValue has changed to the new value.
 *
 * @param newValue
 * @param <T>
 */
public record ValueChange<T>(T newValue) {
    public ValueChange(T newValue) {
        this.newValue = newValue;
    }
}
