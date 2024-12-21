package com.fzenner.datademo.web.outmsg.table;

public class TableCellUpdate {
    public int rowIdx;
    public String fieldName;
    public String value;
    public String errorMsg;


    public TableCellUpdate(int rowIdx, String fieldName, String value, String errorMsg) {
        this.rowIdx = rowIdx;
        this.fieldName = fieldName;
        this.value = value;
        this.errorMsg = errorMsg;
    }
}
