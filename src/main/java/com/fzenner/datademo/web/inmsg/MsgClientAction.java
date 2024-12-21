package com.fzenner.datademo.web.inmsg;


public class MsgClientAction {
	public String msgName;   // Technical qualifier.
	public String module;    // Currently not used. Will enable to provide independent modules.
	public String serverMsgHandler;  // Defines the handler of the module to invoke
	public String pageName;  // The page from which the message was sent.
	public String editPageId;
	public String command;    // Can be omitted if serverMsgHandler does not need it.
	public String subCommand;  // Optional
	public String param;
}
