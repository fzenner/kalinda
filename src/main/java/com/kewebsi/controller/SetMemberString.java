package com.kewebsi.controller;

import java.util.ArrayList;

public class SetMemberString extends PowerString {

	public static ArrayList<SetMemberString> allowedValues = new ArrayList<>();

	public static final SetMemberString one = new SetMemberString("one");
	public static final SetMemberString two = new SetMemberString("two");

	
	
	private SetMemberString(String additionalCore) {
		if (allowedValues == null) {
			allowedValues = new ArrayList<>();
		}
		this.core = additionalCore;
		allowedValues.add(this);
		
	}
	
	
	
	
	public static SetMemberString getMemberFor(String plainString) {
		for (var run : allowedValues) {
			if (run.getCore().equals(plainString)) {
				return run;
			}
		}
		return null;
	}
	
}
