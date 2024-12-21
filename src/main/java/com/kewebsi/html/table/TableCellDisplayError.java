package com.kewebsi.html.table;

public class TableCellDisplayError {

    public Enum fieldName;
    public String errorMsg;

    public TableCellDisplayError(Enum fieldName, String errorMsg) {
        this.fieldName = fieldName;
        this.errorMsg = errorMsg;
    }
}
