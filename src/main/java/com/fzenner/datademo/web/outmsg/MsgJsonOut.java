package com.fzenner.datademo.web.outmsg;


/**
 * Marker interface in order to tag commands that ought to be processed via JSON
 * @author Master
 *
 */

public class MsgJsonOut {
	

	protected String msgName;
	
	
	public MsgJsonOut() {
	}
	
	
	public MsgJsonOut(String msgName) {
		this.msgName = msgName;
	}

	@SuppressWarnings("rawtypes")
	public MsgJsonOut(Enum msgName) {
		this.msgName = msgName.toString();
	}

	
	
	public String getMsgName() {
		return msgName;
	}
	
	
}
