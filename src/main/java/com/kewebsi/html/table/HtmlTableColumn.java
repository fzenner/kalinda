package com.kewebsi.html.table;

public class HtmlTableColumn {

	public final static String DEFAULT_COL_WIDTH = "50px";
	
	
	protected Enum<?> fieldName;
	protected String colWidth;
	
	public HtmlTableColumn(Enum<?> fieldName, String colWidth) {
		super();
		this.fieldName = fieldName;
		this.colWidth = colWidth;
	}
	
	public HtmlTableColumn(Enum<?> fieldName) {
		super();
		this.fieldName = fieldName;
		this.colWidth = DEFAULT_COL_WIDTH;
	}

	public String getColWidth() {
		return colWidth;
	}

	public void setColWidth(String colWidth) {
		this.colWidth = colWidth;
	}

	public Enum<?> getFieldName() {
		return fieldName;
	}

	
	
	
}
