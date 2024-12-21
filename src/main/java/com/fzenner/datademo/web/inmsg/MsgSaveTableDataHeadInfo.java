package com.fzenner.datademo.web.inmsg;



/**
 * We do not parse the payload data that is contained in the JSON data that we receive from the client with this message.
 *
 * That is because the payload data is inherently generic and we currently do not handle parsing of generic data with JSON nor do we plan to.
 * As a result, we use this class to retrieve the head (administrative) info from the table data and parse the table date with a 
 * payload type specific function.
 * 
 * The attribute / member variable that is contained in the JSON date but not parsed with this message is: 
 * <p>
 *  
 *     <code>public CudAndDto[] cudAndDtos; </code>
 * 
 * </p>
 * 
 * However, in the list of field names, this member is contained for type-safe-ish accessabiltiy of this data.
 *     
 */
public class MsgSaveTableDataHeadInfo {
	public String msgName;
	public String pageName;
	public String tableId;

	
	// public CudAndDto[] cudAndDtos; </code>  // See comment on class level.
	
	public enum FieldNames {
		msgName, pageName, tableId, cudAndDtos
	}
	
}

