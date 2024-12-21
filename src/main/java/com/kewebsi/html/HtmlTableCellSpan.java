package com.kewebsi.html;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kewebsi.errorhandling.CanHaveError;
import com.kewebsi.errorhandling.ErrorInfo;
import com.kewebsi.html.table.HtmlPowerTable;
import com.kewebsi.html.table.PowerTableModel;
import com.kewebsi.util.CommonUtils;
import com.kewebsi.util.JsonUtils;

public class HtmlTableCellSpan extends SmartTag implements CanHaveError {

    protected HtmlPowerTable table;
    protected int rowIdx;
    protected Enum<?> columnHeader;

    protected String oldValue;

    public HtmlTableCellSpan(HtmlPowerTable table, int rowIdx, Enum<?> columnHeader, String id) {
        this.table = table;
        this.rowIdx = rowIdx;
        this.columnHeader = columnHeader;
        setId(id);

    }

    public String getValue() {   // TODO: Differentiate between internal String and HTML escaped representation.
        PowerTableModel<?> model = table.getModel();
        return model.getValueForHtml(rowIdx, columnHeader);
    }

    @Override
    public boolean isContentOrGuiDefModified() {
        if (CommonUtils.equals(getValue(), oldValue)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setContentOrGuiDefNotModified() {
        oldValue = getValue();
    }


    @Override
    public String getTag() {
        return "SPAN";
    }

    @Override
    public boolean hasClosingTag() {
        return true;
    }


    @Override
    public ErrorInfo getErrorInfoToDisplayToClient() {
        PowerTableModel<?> model = table.getModel();
        var error = model.getErrorCheckingForeignUpdates(rowIdx, columnHeader);
        if (error != null) {
            var errorDebugRemoveMe = model.getErrorCheckingForeignUpdates(rowIdx, columnHeader);
            return new ErrorInfo(error.getMessage());

        }
        return null;
    }
}