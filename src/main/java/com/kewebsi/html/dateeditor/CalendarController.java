package com.kewebsi.html.dateeditor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.inmsg.MsgClientActionAndTag;
import com.fzenner.datademo.web.inmsg.MsgInvokServiceWithParams;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.GuiCallbackRegistrar;
import com.kewebsi.controller.StaticGuiDelegate;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.errorhandling.StringParsingError;
import com.kewebsi.html.*;
import com.kewebsi.util.KewebsiDateUtils;
import com.kewebsi.util.JsonUtils;

import java.time.LocalDate;


public class CalendarController {

	
	public static final String CALENDAR_POPUP_CONTENT_ID_ATTR = "calendarPopupContentId";
	public static final String DAX_OF_MONTH_ATTR = "data-dayofmonth";
	public static final String DATE_INPUT_FIELD_ATTR = "data-date-input-field";

	public enum YEAR_DIGITS {Required, Optional, Forbidden};








}
