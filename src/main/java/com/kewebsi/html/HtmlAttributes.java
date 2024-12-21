package com.kewebsi.html;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.util.CommonUtils;
import com.kewebsi.util.JsonUtils;

public class HtmlAttributes {

	public LinkedHashMap<String, String> keyValues;

	protected boolean modified;

	public HtmlAttributes() {
	}

	public HtmlAttributes(HtmlAttributes original) {
		keyValues = new LinkedHashMap<>(original.keyValues);
		modified = original.modified;
	}


	public HtmlAttributes(String... keyValuesToAdd) {
		put(keyValuesToAdd);
	}


	public HtmlAttributes(ArrayList<String> keyValuesToAdd) {
		putAll(keyValuesToAdd);
	}



	public void put(String key, String value) {
		initHashMapIfNecessary();
		keyValues.put(key, value);
		modified = true;
	}


	public void addIfValueNotNull(String key, String value) {
		if (value != null) {
			initHashMapIfNecessary();
			put(key, value);
		}
	}

	public void initHashMapIfNecessary(int initalSize) {
		if (keyValues == null) {
			keyValues = new LinkedHashMap<>(initalSize/2);
			modified = true;
		}

	}

	public void initHashMapIfNecessary() {
		if (keyValues == null) {
			keyValues = new LinkedHashMap<>();
		}
		modified = true;
	}

	public void putAll(ArrayList<String> keyValuesToAdd) {

		int newSize = keyValuesToAdd.size();
		if (newSize % 2 > 0) {
			throw new CodingErrorException("Array of key-values must be dividable by 2 but has size " + newSize);
		}
		initHashMapIfNecessary(newSize/2);
		for (int idx = 0; idx < newSize; ) {
			String key = keyValuesToAdd.get(idx++);
			String value = keyValuesToAdd.get(idx++);
			put(key, value);
		}
	}

	public void put(String[] keyValuesToAdd) {
		int length = keyValuesToAdd.length;
		if (length % 2 > 0) {
			throw new CodingErrorException("Array of key-values must be dividable by 2 but has size " + length);
		}
		initHashMapIfNecessary(length/2);

		for (int idx = 0; idx < length; ) {
			String key = keyValuesToAdd[idx++];
			String value = keyValuesToAdd[idx++];
			put(key, value);
		}

	}

	public void put(HtmlAttributes toAdd) {
		if (toAdd != null) {
			if (toAdd.keyValues != null) {
				this.keyValues.putAll(toAdd.keyValues);
				modified = true;
			}
		}
	}

	public String remove(String key) {
		modified = true;
		return keyValues.remove(key);

	}


	public void addToNode(ObjectNode node) {

	}


	@Override
	public String toString() {
		return JsonUtils.createHtmlAttributes(keyValues);
	}



	/**
	 * Creates a string of the format required for HTML tag attributes.
	 * That is a space separated list off attribute value pairs with the value
	 * put in single quotes. Example (without outer quotes):
	 * "attrName1='value1' attr2='value2'"
	 * If the key is null, empty or consists of only blanks, the attribute-value pair is not generated.
	 * If the value is null, the attribute-value pair is also not generated.
	 * However, if the value is an empty or only-blanks string, the attribute-value pair is generated (if the also the key is valid of course).
	 * This way, unnecessary pairs are for non-info values are not generated, unless you provide intentionally the combination of a real key and an empty string.
	 * @param keyValue List of alternating attribute names and values
	 * @return As described above.
	 */
	public static String createJsonKeyValue(ArrayList<String> keyValue) {
		String out = "";

		if (keyValue != null) {
			int size = keyValue.size();
			if (size % 2 > 0) {
				throw new CodingErrorException("Incorrect key value pairs");
			}
			int i = 0;
			while (i<size) {
				String key = keyValue.get(i++);
				String value = keyValue.get(i++);
				if (CommonUtils.hasInfo(key)) {
					if (value != null) {
						out += key + "='" + value + "' ";
					}
				}
			}
		}
		return out;
	}

	public boolean isModified() {
		return modified;
	}


	public void setNotModified() {
		modified = false;
	}

}
