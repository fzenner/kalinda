package com.kewebsi.util;

import com.kewebsi.errorhandling.CodingErrorException;

/**
 * Out-Value / Return value
 */
public class Out<T> {
    T ref;

    /**
     * Ov's cannot be populated during initialition. That would contradict its purpose.
     */
    public Out() {

    }

    public T get() {
        return ref;
    }

    public void set(T ref) {
        if (this.ref != null) {
            throw new CodingErrorException("Duplicate setting of return value.");
        }
        this.ref = ref;
    }

    public boolean isNull() {
        return (ref == null);
    }
}
