package com.kewebsi.html.table;

import com.kewebsi.html.HtmlTagClientAction;


public class HtmlButtonSaveTableData extends HtmlTagClientAction {


	public HtmlButtonSaveTableData(String id, String label, HtmlPowerTable<?> powerTable) {
		init("button", id, label, "CEH_saveTableData", "tableId", powerTable.getId());
		// setEventHandler_2("mousedown", "CEH_preventDefault");
	}


}
