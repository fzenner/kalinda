package com.kewebsi.html;

public record AttributeModification(String key, Modification modification, String value) {

    public enum Modification {NEW, REMOVED,MODIFIED};

}
