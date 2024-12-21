package com.kewebsi.html.table;

import com.fzenner.datademo.web.outmsg.table.*;

import java.util.ArrayList;

/**
 * Transports updated payload data. Eithier for the complete table or (exclusively) for single cells or rowstate changes.
 * Updates of single cells and rowstate changes can both occur in the same update, but never together with the
 * update of the complate date.
 */
public class TableDataUpdateForGui {

    /**
     * Populated (non-null), when ALL rows are updated.
     */
    enum UpdateScope {ALL_DATA, PARTIAL_DATA}

    public ArrayList<TableRowDataForGui> completeTableDataForGui;


    /**
     * Populated (non-null), when single cells are update.
     */
    public ArrayList<TableCellUpdate> cellUpdates;

    /**
     * Populated (non-null), when single cells are update.
     */
    public ArrayList<RowStateChange> rowStateChanges;
    public ArrayList<RowSelectionChange> rowSelectionChanges;



    public TableFocusDef tableFocusDef;

    public UpdateScope updateScope;


    public TableDataUpdateForGui(ArrayList<TableRowDataForGui> completeTableDataForGui) {
        this.completeTableDataForGui = completeTableDataForGui;
        updateScope = UpdateScope.ALL_DATA;
    }

    public TableDataUpdateForGui(ArrayList<TableCellUpdate> cellUpdates,
                                 ArrayList<RowStateChange> rowStateChanges,
                                 ArrayList<RowSelectionChange> rowSelectionChanges) {
        this.cellUpdates = cellUpdates;
        this.rowStateChanges = rowStateChanges;
        this.rowSelectionChanges = rowSelectionChanges;
        updateScope = UpdateScope.PARTIAL_DATA;
    }

    public void setTableFocusDef(TableFocusDef tableFocusDef) {
        this.tableFocusDef = tableFocusDef;
    }
}
