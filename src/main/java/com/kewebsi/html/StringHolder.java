package com.kewebsi.html;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class StringHolder {

	protected String value;
	
	private PropertyChangeSupport support;
	
	public StringHolder() {
		support = new PropertyChangeSupport(this);
		this.value = null;
	}
	
//	public StringHolder(String id, String value) {
//		support = new PropertyChangeSupport(this);
//		this.id = id; 
//		this.value = value;
//	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		support.firePropertyChange("value", this.value, value);
		this.value = value;
	}
	
//	
//	public static StringHolder byId(String id) {
//		StringHolder stringHolder = new StringHolder();
//		return stringHolder;
//	}
//	
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

}
