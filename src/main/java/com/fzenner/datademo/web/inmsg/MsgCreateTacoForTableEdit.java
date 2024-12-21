package com.fzenner.datademo.web.inmsg;

public class MsgCreateTacoForTableEdit {
	public String msgName;      // Should always be "CREATE_TACO_FOR_TABLE_EDIT".
	public String module;    // Currently not used. Will enable to provide independent modules.
	public String serverMsgHandler;  // Defines the handler of the module to invoke
	public String pageName;     
	public String tableId;
	public int tablePosition;
}

