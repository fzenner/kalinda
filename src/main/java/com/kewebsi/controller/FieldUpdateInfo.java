package com.kewebsi.controller;

import com.kewebsi.errorhandling.CodingErrorException;

public record FieldUpdateInfo(
    Enum  fieldName,
    boolean frontToBack,
    boolean backToFront,
    boolean errorInfo
) {
    public enum UpdateDirection {FRONT_TO_BACK, BACK_TO_FRONT, ERROR_INFO}

    public static FieldUpdateInfo backToFront(Enum fieldName) {
        return new FieldUpdateInfo(fieldName,false, true, false);
    }


    public static FieldUpdateInfo frontToBack(Enum fieldName) {
        return new FieldUpdateInfo(fieldName,false, true, false);
    }

    public static FieldUpdateInfo errorInfo(Enum fieldName) {
        return new FieldUpdateInfo(fieldName,false, true, false);
    }

    public FieldUpdateInfo createMerge(FieldUpdateInfo other) {
        if (this.fieldName != other.fieldName) {
            throw new CodingErrorException("Merging different fields:" + this.fieldName + ", " + other.fieldName);
        }

        if (this.equals(other)) {
            return this;
        } else {
            var newFieldUpdateInfo = new FieldUpdateInfo(
                    this.fieldName,
                    this.frontToBack || other.frontToBack,
                    this.backToFront || other.backToFront,
                    this.errorInfo || other.errorInfo
            );
            return newFieldUpdateInfo;
        }
    }
}



