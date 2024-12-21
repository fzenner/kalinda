package com.fzenner.datademo.web.outmsg;

import com.kewebsi.controller.FieldAssistant;

import java.util.ArrayList;

public class FieldInfo {
    public String symbolColId;
    public String label;
    public String dataType;  // See FieldAssistant.FieldType XXXXX Consider changing to enum.
    public String colWidth;
    public boolean editable;

    public ArrayList<String> options;

    public FieldInfo(String symbolColId, String label, FieldAssistant.FieldType dataType, boolean editable, String colWidth) {
        this.symbolColId = symbolColId;
        this.label = label;
        this.dataType = dataType.name();
        this.editable = editable;
        this.colWidth = colWidth;

    }

    public void addOption(String option) {
        if (options == null) {
            options = new ArrayList<>();
        }
        options.add(option);
    }


    public void addOptions(ArrayList<String> newOptions) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        }
        this.options.addAll(newOptions);
    }
}
