package com.kewebsi.service;

import com.fzenner.datademo.web.UserSessionHandler;
import com.kewebsi.controller.*;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.html.PageState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PageStateVarColdLink<T> extends PageStateVarBase<T> /* implements PageStateVarColdLinkIntf<T> */ {

	protected T val;

	public T getVal() {
		if (hasError()) {
			throw new CodingErrorException("Illegal attempt to read the value of a PageVar with an error. Field: " + fieldAssistant.getFieldLabel() + " Error: " + getError().getErrorMsg());
		}
		return val;
	}


	/**
	 * Call this, when you expect the field to be non null and you do not re-validate the already entered value.
	 * @return
	 * @throws PageVarError
	 */
	public T getNonNullVal() throws PageVarError {
		if (val == null) {
			throw new PageVarError(this, "Field must not be empty!");
		}

		//
		// If there is an error on the field, we throw it (again) so that the GUI can create
		// a popup warning or so in addition to the error displayed on the field.
		//
		if (hasError()) {
			throw getError();
		}
		return val;
	}

	public T getValCore() {
		return val;
	}


	public void setValueCore(T val) {
//		if (val == null) {
//			throw new CodingErrorException(String.format("Attempt to set the value of %s to null. Prior value: ", fieldAssistant.getFieldName().name(), (val == null ? "null" : String.valueOf(val))));
//		}
		this.val = val;
	}


	public void clearValue() {
		val = null;
	}

	/**
	 * Returns the most helpful string to be displayed. Handles null values gracefully.
	 * @return
	 * If no error exists: The formatted String based on the current internal value.
	 * If an error exists: Last useful data (see implementation for details):
	 */
	public String getDisplayString() {
		if (hasError()) {
			if (getError().getErrorType() == PageVarError.ErrorType.SERVER_SIDE_PARSING) {
				if (unparsedStringValue == null) {
					// This situation should never occur.
					Logger LOG = LoggerFactory.getLogger(UserSessionHandler.class);
					LOG.info("Unexpected situation encountered: Parsing error occurred but no unparsedStringValue set!");
					return "";
				} else {
					return unparsedStringValue;
				}
			} else {
				if (unparsedStringValue == null) {  // If the error was set in the business logic, a parsed value exists.
					if (val == null) {
						return "";
					} else {
						return getValAsString();
					}
				} else {
					return unparsedStringValue;
				}
			}
		} else {
			if (val == null) {
				return "";
			} else {
				return getValAsString();
			}
		}
	}

	/**
	 * Returns the value as string. Assumes that the value is not null.
	 * @return
	 */
	public abstract String getValAsString();

	/**
	 * It is possible to have PageStateVars without html IDs.
	 * Their link to an HTML input field must then explicitly be provided
	 * by the controller and a lambda function returning the variable from
	 * the page state.
	 * This allows us to keep HTML-related info out of the PageState.
	 */
	public PageStateVarColdLink(PageState pageState) {
		initPageStateVar(pageState, null, null);
	}

	public PageStateVarColdLink(PageState pageState, SimpleFieldAssistant fieldAssistant, String htmlIdPrefix) {
		initPageStateVar(pageState, generateHtmlId(pageState, fieldAssistant, htmlIdPrefix), fieldAssistant);
	}

	public PageStateVarColdLink(PageState pageState, FieldAssistant fieldAssistant) {
		initPageStateVar(pageState, generateHtmlId(pageState, fieldAssistant, null), fieldAssistant);
	}

	public PageStateVarColdLink(String htmlId, PageState pageState) {
		initPageStateVar(pageState, htmlId, null);
	}





	abstract public void setValueFromBackend(BaseVal baseVal);


	public static PageStateVarColdLink createPageStateVarColdLink(PageState pageState, SimpleFieldAssistant fieldAssistant) {
		return createPageStateVarColdLink(pageState, fieldAssistant, null);
	}

	public static PageStateVarColdLink createPageStateVarColdLink(PageState pageState, SimpleFieldAssistant fieldAssistant, String fieldPrefix) {
		var fieldType = fieldAssistant.getFieldType();
		PageStateVarColdLink result;
		switch (fieldAssistant.getFieldType()) {
			case STR -> {
				result = new PageVarStringColdLink(pageState, fieldAssistant, fieldPrefix);
			}

			case INT -> {
				result = new PageVarIntColdLink(pageState, fieldAssistant, fieldPrefix);
			}

			case LONG -> {
				result = new PageVarLongColdLink(pageState, fieldAssistant, fieldPrefix);
			}

			case LOCALDATETIME -> {
				result = new PageVarLocalDateTimeColdLink(pageState, fieldAssistant, fieldPrefix);
			}

			case LOCALDATE -> {
				result = new PageVarLocalDateColdLink(pageState, fieldAssistant, fieldPrefix);
			}

			case ENM -> {
				result = new PageVarEnumColdLink(pageState, fieldAssistant, fieldPrefix);

			}

			default -> { throw new CodingErrorException("FieldType " + fieldType + " not implemented");
			}
		}
		return result;
	}

	public String toString() {
		return "pageVarId:" + getPageVarId() +  " fieldName: " + this.fieldAssistant.getFieldName();
	}
}
