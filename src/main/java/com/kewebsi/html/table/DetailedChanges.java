package com.kewebsi.html.table;

import com.fzenner.datademo.web.outmsg.table.RowSelectionChange;
import com.fzenner.datademo.web.outmsg.table.RowStateChange;
import com.fzenner.datademo.web.outmsg.table.TableCellUpdate;
import org.javatuples.Pair;

import java.util.ArrayList;

public class DetailedChanges {
    ArrayList<TableCellUpdate> tableCellUpdates;
    ArrayList<RowStateChange> rowStateChanges;
    ArrayList<RowSelectionChange> rowSelectionChanges;

    public DetailedChanges(ArrayList<TableCellUpdate> tableCellUpdates, ArrayList<RowStateChange> rowStateChanges, ArrayList<RowSelectionChange> rowSelectionChanges) {
        this.tableCellUpdates = tableCellUpdates;
        this.rowStateChanges = rowStateChanges;
        this.rowSelectionChanges = rowSelectionChanges;
    }


}

