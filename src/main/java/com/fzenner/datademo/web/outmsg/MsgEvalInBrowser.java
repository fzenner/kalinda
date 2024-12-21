package com.fzenner.datademo.web.outmsg;

public class MsgEvalInBrowser extends MsgJsonOut {
	
	private String jsExpressionToExecute;
	
	public MsgEvalInBrowser(String jsExpressionToExecute) {
		super(MsgKewebsiOut.EVAL_IN_BROWSER);
	}

	public String getJsExpressionToExecute() {
		return jsExpressionToExecute;
	}
	
	
	
}
