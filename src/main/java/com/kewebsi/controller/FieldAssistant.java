package com.kewebsi.controller;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * E Type of Entity
 *
 * @param <E> Type of Entity
 * @param <F> Type of Field
 */
public abstract class FieldAssistant<E, F> extends SimpleFieldAssistant<F> {


	protected boolean isKey;

	public String dbColName = null;

	public int defaultWidthInChar = 10;

	Function<E, F> getter;

	BiConsumer<E, F> setter;


	public enum FieldType {STR, ENM, INT, LONG, FLOAT, BOOL, LOCALDATETIME, LOCALDATE, LOCALTIME};


	public FieldAssistant(Enum<?> fieldName, FieldType fieldType) {
		super(fieldName, fieldType, true);
	}
	
	public FieldAssistant(Enum<?> fieldName, FieldType fieldType, boolean editable) {
		super(fieldName, fieldType, editable);
	}

	public FieldAssistant(Enum<?> fieldName, String fieldLabel, FieldType fieldType, boolean editable) {
		super(fieldName, fieldType, editable);
		this.fieldLabel = fieldLabel;
	}



	
	public static FieldAssistantLong longId(Enum <?> fieldName) {
		FieldAssistantLong result = new FieldAssistantLong(fieldName);
		result.setIsKey(true);
		result.setEditable(false);
		return result;
	}

	public static FieldAssistantLong longType(Enum <?> fieldName) {
		FieldAssistantLong result = new FieldAssistantLong(fieldName);
		return result;
	}

	public static <E> FieldAssistantInt intType(Enum <?> fieldName) {
		FieldAssistantInt<E> result = new FieldAssistantInt<E>(fieldName);
		return result;
	}

	public static <E> FieldAssistantLocalDateTime dateTime(Enum <?> fieldName) {
		FieldAssistantLocalDateTime<E> result = new FieldAssistantLocalDateTime<E>(fieldName);
		return result;
	}
	
	
	public static FieldAssistantStr str(Enum <?> fieldName) {
		return new FieldAssistantStr(fieldName, true);
	}
	
	public static FieldAssistantLocalDateTime localDateTime(Enum <?> fieldName) {
		return new FieldAssistantLocalDateTime(fieldName);
	}
	
	public static FieldAssistant enm(Enum <?> fieldName, Class<? extends Enum> enumClass) {
		var fa = new FieldAssistantEnum(fieldName);
		fa.setEnumClass(enumClass);
		return fa;
	}
	
	public FieldAssistant setReadOnly() {
		editable = false;
		return this;
	}









	public FieldAssistant setDbColName(String dbColName) {
		this.dbColName = dbColName;
		return this;
	}



	public String getDbColName() {
		if (dbColName != null) {
			return dbColName;
		}
		return fieldName.name();
	}


	public int getDefaultWidthInChar() {
		return defaultWidthInChar;
	}

	public FieldAssistant setDefaultWidthInChar(int defaultWidthInChar) {
		this.defaultWidthInChar = defaultWidthInChar;
		return this;
	}



	public boolean isKey() {
		return isKey;
	}

	public FieldAssistant setIsKey(boolean key) {
		isKey = key;
		return this;
	}

	public boolean isCanBeEmpty() {
		return canBeNull;
	}





	public FieldAssistant setSetter(BiConsumer<E, F> setter) {
		this.setter = setter;
		return this;
	}


	public void setGetter(Function<E,F> getter) {
		this.getter = getter;
	}


	public F getValue(E entity) {
		F val = getter.apply(entity);
		return val;
	}

	public void clear(E entity) {
		setter.accept(entity, null);
	}


	public void setValue(E entity, F value) {
		setter.accept(entity, value);
	}

	public void setUnconvertedValue(E entity, Object value) {
		setter.accept(entity, (F) value);
	}


	@Override
	public String toString() {
		return fieldLabel;
	}

}
