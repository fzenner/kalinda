package com.kewebsi.html.table;

import com.fzenner.datademo.web.inmsg.ClientEventHandlerConsts;
import com.kewebsi.html.HtmlTagClientAction;

/**
 * Create-Button that invokes the standard save function on a PowerTable
 * Note: The server-side save function invoked by the client side function
 *       handling this button click is linked to the table and defined at creation time of the table.
 *       In other words, the client handler reads the server-side callback function to be invoked 
 *       from an attribute of the HTML table.
 */
public class HtmlButtonInsertNewEntityIntoTable extends HtmlTagClientAction {
	public HtmlButtonInsertNewEntityIntoTable(String id, String label, HtmlPowerTable<?> powerTable) {
		init(	"button",
				id, 
				label, 
				ClientEventHandlerConsts.CLIENT_EVENT_HANDLER_INSERT_ENTITY_FOR_TABLE_EDIT, 
				ClientEventHandlerConsts.TABLE_ID_ATTR_NAME, powerTable.getId()
		);

		setEventHandler_2("mousedown", "CEH_preventDefault");

	}		
}