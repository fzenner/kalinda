package com.fzenner.datademo.web.outmsg.table;

public class RowSelectionChange {
    public int rowIdx;
    public boolean isSelected;

    public RowSelectionChange(int rowIdx, boolean isSelected) {
        this.rowIdx = rowIdx;
        this.isSelected = isSelected;
    }
}
