package com.kewebsi.html;

public class ClientEventHandler {


	public static final String CEH_standardButtonClicked = "CEH_standardButtonClicked";
	public static final String CEH_escapeKeyPressed = "CEH_escapeKeyPressed";


	protected EventType eventType;
	protected String functionName;
	
	public enum EventType {click};
	
	public ClientEventHandler(EventType eventType, String functionName) {
		this.eventType = eventType;
		this.functionName = functionName;
	}
	
	public EventType getEventType() {
		return eventType;
	}
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	
	
}
