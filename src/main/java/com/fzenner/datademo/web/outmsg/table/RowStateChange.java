package com.fzenner.datademo.web.outmsg.table;

public class RowStateChange {
    public int rowIdx;
    public String newState;

    public RowStateChange(int rowIdx, String newState) {
        this.rowIdx = rowIdx;
        this.newState = newState;
    }
}
