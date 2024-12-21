package com.fzenner.datademo.web.outmsg;

import com.kewebsi.util.JsonUtils;

public class SuccessMsg extends MsgJsonOut {
	
	public static SuccessMsg fixedInstance;
	
	public SuccessMsg() {
		super(MsgKewebsiOut.SUCCESS);
	}
	
	public static SuccessMsg getMsg() {
		if (fixedInstance == null) {
			fixedInstance = new SuccessMsg();
		}
		return fixedInstance;
	}

	@Override
	public String toString() {
		return JsonUtils.generateJson(this);
	}
	
	
}
