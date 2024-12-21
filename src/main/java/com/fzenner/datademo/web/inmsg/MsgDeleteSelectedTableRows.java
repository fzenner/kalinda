package com.fzenner.datademo.web.inmsg;

public class MsgDeleteSelectedTableRows {   // TODO: Unify with MsgSaveTableData
	public String msgName;      // Allways CS_MESSAGE
	public String module;    // Currently not used. Will enable to provide independent modules.
	public String serverMsgHandler;  // Defines the handler of the module to invoke
	public String pageName;  // The page from which the message was sent.
	public String tableId;
	public CudAndDto[] cudAndDtos;
}
