package com.fzenner.datademo.web.outmsg.table;

import com.fzenner.datademo.web.outmsg.FocusDef;
import com.kewebsi.html.table.HtmlPowerTable;

public class TableFocusDef extends FocusDef {

    public String fieldName;
    public int rowIdx;


    public TableFocusDef(HtmlPowerTable table, int rowIdx, String fieldName) {
        super(table, FocusDef.TABLE_FOCUS_FUNCTION);
        this.fieldName = fieldName;
        this.rowIdx = rowIdx;
    }


}
