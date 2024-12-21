package com.kewebsi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.kewebsi.errorhandling.*;
import com.kewebsi.html.PageStateVarIntf;
import com.kewebsi.html.search.EntityEditorState;
import com.kewebsi.service.FieldError;
import com.kewebsi.service.PageStateVarColdLink;
import com.kewebsi.service.PageVarError;
import com.kewebsi.util.CommonUtils;
import com.kewebsi.util.JsonUtils;

import java.util.*;

/**
 * 
 * @author Frank Zenner
 * 
 * Provides metadata for the DTO T.
 * 
 * @param <T> Type of the DTO that this class provides metadata for.
 * 
 */
public abstract class DtoAssistant<T> {

	protected LinkedHashMap<Enum<?>, FieldAssistant> fieldAssistants;


	/**
	 * The hashmap has as key the foreign table name.
	 */
	protected ArrayList<ManyToOneLinkAssistant> manyToOneLinkAssistants;
	protected ArrayList<ManyToManyLinkAssistant> manyToManyLinkAssistants;

	protected FieldAssistant idAssistant;

	/*
	 *	Boilerplate to be implement by derived classes.
	 */

	// public abstract Enum<?>[] getFieldNames();

	public Enum<?>[] getFieldNames() {
		return (Enum[]) getEnumClass().getEnumConstants();
	}

	public abstract Class<T> getEntityClass();

	public abstract String getTableName();


	//public abstract String getHtmlValue(T entity, Enum<?> fieldName);
	public String getHtmlValue(T entity, Enum<?> fieldName) {
		if (entity == null) {
			return null;
		}

		BaseVal baseVal = getValueAsBaseType(entity, fieldName);
		if (baseVal == null) {
			return "";
		} else {
			return baseVal.toHtml();
		}
	}


	public String getGuiDefValue(T entity, Enum<?> fieldName) {
		if (entity == null) {
			return null;
		}

		BaseVal baseVal = getValueAsBaseType(entity, fieldName);
		if (baseVal == null) {
			return "";
		} else {
			return baseVal.toHtml();  //  XXXX TODO: Implement the right format for all supported types
		}
	}


	public String getGuiDefValue(ManagedEntity<T> managedEntity, Enum<?> fieldName) {
		BaseVal baseVal = getValueAsBaseType(managedEntity, fieldName);
		if (baseVal == null) {
			return "";
		} else {
			return baseVal.toHtml();  //  XXXX TODO: Implement the right format for all supported types
		}
	}

	public BaseVal getValueAsBaseType(T entity, Enum<?> fieldName) {
		assert(entity != null);

		var fa = getFieldAssistant(fieldName);
		var val = fa.getValue(entity);
		return BaseVal.of(val);
	}


	public Object getValue(T entity, Enum<?> fieldName) {
		assert(entity != null);
		var fa = getFieldAssistant(fieldName);
		return fa.getValue(entity);
	}


	public BaseVal getValueAsBaseType(ManagedEntity<T> managedEntity, Enum<?> fieldName) {
		if (managedEntity == null) {
			return null;
		} else {
			return getValueAsBaseType(managedEntity.getEntity(), fieldName);
		}
	}


	public BaseVal getValueAsBaseType(T entity, FieldAssistant fieldAssistant) {
		return getValueAsBaseType(entity, fieldAssistant.getFieldName());
	}

	public BaseValLong getId(T entity) {
		FieldAssistant idAssistant = getIdAssistant();
		assert(idAssistant.isKey);
		return (BaseValLong) getValueAsBaseType(entity, idAssistant);
	}

	public void setValueStr(FieldAssistant fieldAssistant, String value, T entity) throws ExpectedClientDataError {

		FieldError error = fieldAssistant.validateStr(value);
		if (error != null) {
			throw error;
		}

		try {
			Object untypedValueObject = fieldAssistant.parseToObject(value);
			setValueObjectCore(fieldAssistant, untypedValueObject, entity);
		} catch(StringParsingError ex) {
			throw new FieldError(fieldAssistant, ex.getMessage(), ex.getBadValue());
		}

	}

	public void setValueStr(Enum fieldName, String value, T entity) throws ExpectedClientDataError {
		FieldAssistant fieldAssistant = getFieldAssistant(fieldName);
		setValueStr(fieldAssistant, value, entity);
	}

	public void setValueByFieldAssistant(FieldAssistant fieldAssistant, Object untypedValueObject, ManagedEntity<T> managedEntity) throws ExpectedClientDataError {
		setValueByFieldAssistant(fieldAssistant, untypedValueObject, managedEntity.getEntity());
		managedEntity.notifyFieldUpdatedFrontToBack(fieldAssistant);
	}


	public void setValueByFieldAssistant(FieldAssistant fieldAssistant, Object untypedValueObject, T entity) throws ExpectedClientDataError {
		FieldError error = fieldAssistant.validate(untypedValueObject);
		if (error != null) {
			throw error;
		}
		setValueObjectCore(fieldAssistant, untypedValueObject, entity);
	}

	public void setValueByFieldEnum(Enum fieldName, Object untypedValueObject, T entity) throws ExpectedClientDataError {
		FieldAssistant fieldAssistant = getFieldAssistant(fieldName);

		FieldError error = fieldAssistant.validate(untypedValueObject);
		if (error != null) {
			throw error;
		}

		setValueObjectCore(fieldName, untypedValueObject, entity);

	}



	public FieldAssistant getFieldAssistant(String fieldName) {
		Enum enm = getFieldEnum(fieldName);
		var result = getFieldAssistant(enm);
		return result;
	}


	public FieldAssistant<T, ?> getFieldAssistant(Enum<?> en) {
		var result = fieldAssistants.get(en);
		if (result == null) {
			throw new CodingErrorException(String.format("FieldAssistant for %s in %s does not exist", en.name(), getEntityClass().getSimpleName()));
		}
		return result;
	}


	public void setValueObjectCore(String fieldName, Object value, Object entity) throws FieldError {
		FieldAssistant fa = getFieldAssistant(fieldName);
		fa.setValue(entity, value);
	}

	public void setValueObjectCore(Enum fieldName, Object value, Object entity)  {
		FieldAssistant fieldAssistant = getFieldAssistant(fieldName);
		fieldAssistant.setValue(entity, value);
	}

	public void setValueObjectCore(FieldAssistant fieldAssistant, Object value, Object entity)  {
		fieldAssistant.setValue(entity, value);
	}

	public void setValueObjectCore(FieldAssistant fieldAssistant, Object value, ManagedEntity managedEntity)  {
		fieldAssistant.setValue(managedEntity.getEntity(), value);
		managedEntity.notifyFieldUpdatedFrontToBack(fieldAssistant);
	}

	public void clearValue(Enum<?> fieldName, T entity) {
		FieldAssistant fa = getFieldAssistant(fieldName);
		fa.clear(entity);
	}

	public void setUnconvertedValueFromBackend(Enum fieldName, Object value, T entity) {
		FieldAssistant fa = getFieldAssistant(fieldName);
		fa.setUnconvertedValue(entity, value);
	}

	public void setUnconvertedValueFromBackend(Enum fieldName, Object value, ManagedEntity<T> managedEntity) {
		FieldAssistant fa = getFieldAssistant(fieldName);
		fa.setUnconvertedValue(managedEntity.getEntity(), value);
		managedEntity.notifyFieldUpdatedBackToFront(fa);
	}

	// public abstract void clearValue(Enum<?> fieldName, T entity);

	public void clearValue(Enum<?> fieldName, ManagedEntity<T> managedEntity) {
		clearValue(fieldName, managedEntity.getEntity());
		managedEntity.notifyFieldUpdatedFrontToBack(fieldName);
	}


	/*
	 * Boilerplate that should be overridden by derived class.
	 */
	public String getEntityNameSingular() {
		return getEntityClass().getSimpleName();
	}

	/*
	 * Boilerplate that should be overridden by derived class.
	 */
	public String getEntityNamePlural() {
		return getEntityNameSingular() + "s";
	}

	/**
	 * The symbol should be a short alphanumeric string that is unique amongst all DtoAssistants.
	 * @return
	 */
	public String getSymbol() {
		return getEntityNameSingular();
	}

	
	
	/*
	 *Boilerplate that typically does not need to be overridden by derived class. 
	 * (Implemented and generic.)    
	 */
	
	public T createEntity() {
		try {
			return getEntityClass().getDeclaredConstructor().newInstance();
			// return getEntityClass().getConstructor().newInstance();
		} catch (Exception e) {
			throw new CodingErrorException(e);
		}
	}
	

	
	
	public boolean isEditable(Enum<?> en) {
		FieldAssistant fa = fieldAssistants.get(en);
		return fa.isEditable();
	}
	
	


//
//	public FieldAssistant.FieldType getBaseType(Enum fieldName) {
//		IngredientAssistant.IngredientFields col = (IngredientAssistant.IngredientFields) fieldName;
//		return getFieldAssistant(fieldName).getFieldType();
//	}


	public LinkedHashMap<Enum<?>, FieldAssistant> getFieldAssistantHashMap() {
		return fieldAssistants;
	}


	public Collection<FieldAssistant> getFieldAssistants() {
		return fieldAssistants.values();
	}

	public FieldAssistant[] getFieldAssistantsAsArray() {
		int size = fieldAssistants.size();
		FieldAssistant[] array= new FieldAssistant[size];
		array = fieldAssistants.values().toArray(array);
		return array;
	}

	
	/**
	 * Retrieve the class of the central Enum
	 * Note: Although the class is not known here, we can use it for calling enum functionality.
	 *       The class of the enum will be known to the subclass. 
	 *       This is a central mechanism around classes and enums, which push (and violate?) the boundaries
	 *       of type logic. But it works.
	 * @return The class of the Enum depicting the fields of the corresponding entity.
	 */
//	public Class<?> getEnumClass() {
//		return getFieldNames()[0].getClass();
//	}

	public abstract Class<?> getEnumClass();

	/**
	 * This method is the best possible hack (that I found) to have a generic, that is not specific to a fixed Enum, method,
	 * that converts a string into an enum. Again. the enum is not known in this class but will be provided by a subclass.
	 * 
	 * @param fieldName
	 * @return	
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Enum<?> getFieldEnum(String fieldName) {
		return Enum.valueOf((Class) getEnumClass(), fieldName);
	}


	public int getColCount() {
		return getFieldNames().length;
	}
	
	public void mapDtoToEntity(JsonNode payload, T entity) throws ExpectedClientDataError  {
		var iterator = payload.fields();
		while (iterator.hasNext()) {
			Map.Entry<String, JsonNode> childRun = iterator.next();
			this.setValueStr(childRun.getKey(), childRun.getValue().asText(), entity);
		}
	}

	// TODO: We do not have to use this, if we have EntityBacked variables. XXXXXXXXXXXXXX
	public void mapPageStateToEntity(EntityEditorState entityEditorState, T entity) throws ExpectedClientDataError {
		// We need to validate all fields, because some fields might not have been edited and contain
		// illegel empty values. Also inter-field errors are checked here
		var err = validatePayloadForSaving(entityEditorState);
		if (err != null) {
			throw err;
		}

		for (FieldAssistant fieldAssistant : fieldAssistants.values()) {
			Enum <?> fieldName = fieldAssistant.getFieldName();
			PageStateVarIntf pageStateVar = entityEditorState.getPageStateVar(fieldAssistant);

			boolean transferField = false;

			Object value = pageStateVar.getVal();

			if (fieldAssistant.isEditable()) {
				transferField = true;
			} else {
				if (fieldAssistant.isKey()) {
					if (!(fieldAssistant.getFieldType().equals(FieldAssistant.FieldType.LONG))) {
						throw new CodingErrorException("Only FieldType LONG is supported as key.");
					}

					if (entityEditorState.entityIsNew()) {  // We need to create a new entity. The given ID does not matter.
						if (value != null) {
							throw new MalformedClientDataException("Attempt to provide an ID for a new entity: " + value);
						}
						// transferField == false, but is false already, hence commented out.
					} else {
						// When we are here, we know we need to update. We still check whether the client data is fishy.
						// (Strictly speaking, we do not need the client data, since we have the ID stored in the EntityState.)
						Long idFromClient = (Long)value;
						long originalId = entityEditorState.getEntityId();
						if (idFromClient != originalId) {
							throw new MalformedClientDataException(String.format("Illegal ID provided. Expected: %s, Received: %s", originalId, idFromClient));
						}
						transferField = true;
					}

				}
			}


			if (transferField) {
				try {
					setValueByFieldEnum(fieldName, value, entity);
				} catch (ExpectedClientDataError e) {
					PageVarError error = new PageVarError(pageStateVar, e.getMessage());
					pageStateVar.setError(error);

					// It is not enough to set the error on the variable. We also throw it up so the transaction
					// is cancelled.
					throw e;
				}
			}
		}
	}


	public void mapEntityToPageState(T entity, EntityEditorState entityEditorState)  {  // TODO Use resetToEntity()
		for (FieldAssistant fieldAssistant : fieldAssistants.values()) {
			Enum <?> fieldName = fieldAssistant.getFieldName();
			PageStateVarIntf pageStateVar = entityEditorState.getPageStateVar(fieldAssistant);
			var val = getValueAsBaseType(entity, fieldName);
			((PageStateVarColdLink) pageStateVar).setValueFromBackend(val);
			if (fieldAssistant.isKey()) {
				BaseValLong longVal = (BaseValLong) val;
				entityEditorState.setEntityId(longVal.getVal());
			}

			// We always set the key as returned from the DB, although it is not really necessary when we update.
			if (fieldAssistant.isKey()) {
				if (! (val instanceof BaseValLong) ) {
					throw new CodingErrorException("Only datatype long is supported as ID.");
				}
				BaseValLong bvl = (BaseValLong) val;
				entityEditorState.setEntityId(bvl.getVal());
			}
		}
	}

	/**
	 * We need to validate all fields, because some fields might not have been edited and contain
	 * illegel empty values. Also inter-field errors are checked here
	 */

	public ExpectedClientDataError validatePayloadForSaving(EntityEditorState entityEditorState) {
		for (FieldAssistant fieldAssistant : fieldAssistants.values()) {
			if (! fieldAssistant.isKey()) {
				PageStateVarIntf pageStateVar = entityEditorState.getPageStateVar(fieldAssistant);

				// If the pageVar has an error, we do not even read it.
				// Otherwise we might read a (background) value that is not displayed
				var pve = pageStateVar.getEffectiveError();
				if (pve != null) {
					return new ExpectedClientDataError(pve);
				}

				Object value = pageStateVar.getVal();
				FieldError error = fieldAssistant.validate(value);
				if (error != null) {
					pageStateVar.setError(new PageVarError(pageStateVar, error.getMessage()));
					return new ExpectedClientDataError(error);
				}
			}
		}
		return null;
	}


	public ArrayList<FieldError> validateExceptIdAndDbTimeStamp(ManagedEntity<T> managedEntity) {
		T dto = managedEntity.getEntity();
		ArrayList<FieldError> errors = null;
		for (FieldAssistant fa : fieldAssistants.values()) {
			if (fa == idAssistant || isCreationTimeStamp(fa)) {
				continue;
			}
			var val = getValue(dto, fa.getFieldName());
			FieldError error = fa.validate(val);
			if (error != null) {

				managedEntity.putError(fa.getFieldName(), error);

				if (errors == null) {
					errors = new ArrayList<>();
				}
				errors.add(error);
			}
		}
		return errors;
	}

	protected boolean isCreationTimeStamp(FieldAssistant fa) {
		if (fa instanceof FieldAssistantLocalDateTime<?>) {
			var faldt = (FieldAssistantLocalDateTime) fa;
			if (faldt.isCreationTimeStamp()) {
				return true;
			}
		}
		return false;
	}

	
	/**
	 * @param entity
	 * @param fieldName
	 * @return
	 */
	public String getHtmlValue(T entity, String fieldName) {
		Enum<?> en = getFieldEnum(fieldName);
		return getHtmlValue(entity, en);
	}
	
	public String getFieldNamesAsJsonArray() {
		return JsonUtils.generateJsonArray(getFieldNames());
	}

	public void setValueStr(String fieldName, String value, T entity) throws ExpectedClientDataError {
		var enm = getFieldEnum(fieldName);
		setValueStr(enm, value, entity);
	}

	public void setValueStr(Enum<?> fieldName, String value, ManagedEntity<T> managedEntity, FieldUpdateInfo.UpdateDirection updateDirection) throws ExpectedClientDataError {
		setValueStr(fieldName, value, managedEntity.getEntity());
		switch (updateDirection) {
			case FRONT_TO_BACK -> managedEntity.notifyFieldUpdatedFrontToBack(fieldName);
			case BACK_TO_FRONT -> managedEntity.notifyFieldUpdatedBackToFront(fieldName);
			case ERROR_INFO -> throw new CodingErrorException("Errors cannot be handled here.");
		}
	}

	public void setId(Long id, T dto) {
		try {
			setValueStr(getIdAssistant().getFieldName(), id.toString(), dto);
		} catch (ExpectedClientDataError e) {
			throw new CodingErrorException(e);
		}

	}

	public void setId(Long id, ManagedEntity<T> managedEntity) {
		var fieldName = getIdAssistant().getFieldName();
		try {
			setValueStr(fieldName, id.toString(), managedEntity, FieldUpdateInfo.UpdateDirection.BACK_TO_FRONT);

		} catch (ExpectedClientDataError e) {
			throw new CodingErrorException(e);
		}

	}

	public String getAssistantId() {
		String assistantId = getEntityClass().getName();
		return assistantId;
	}

	public boolean isNew(T dto) {
		if (getAssistantId() == null) {
			return true;
		} else {
			return false;
		}
	}


	public void addFieldAssistant(FieldAssistant fieldAssistant) {
		if (fieldAssistants == null) {
			fieldAssistants = new LinkedHashMap<>(getFieldNames().length);
		}

		Enum key = fieldAssistant.getFieldName();
		if (fieldAssistants.containsKey(key)) {
			throw new CodingErrorException("Attempt to add FieldAssistant twice: " + key.name());
		}
		fieldAssistants.put(fieldAssistant.getFieldName(), fieldAssistant);
		if (fieldAssistant.isKey()) {
			this.idAssistant = fieldAssistant;
		}
	}

	public Collection<ManyToOneLinkAssistant> getManyToOneLinkAssistants() {
		return manyToOneLinkAssistants;
	}

	public Collection<ManyToManyLinkAssistant> getManyToManyLinkAssistants() {
		return manyToManyLinkAssistants;
	}


	public boolean containsLinkAssistant(LinkAssistant linkAssistant) {
		if (linkAssistant instanceof ManyToOneLinkAssistant) {
			ManyToOneLinkAssistant la = (ManyToOneLinkAssistant) linkAssistant;
			if (manyToOneLinkAssistants.contains(la)) {
				return true;
			} else {
				return false;
			}
		} else {
			ManyToManyLinkAssistant la = (ManyToManyLinkAssistant) linkAssistant;
			if (manyToManyLinkAssistants.contains(la)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public ManyToOneLinkAssistant getManyToOneLinkAssistantTo(DtoAssistant dtoAssistantToLookFor) {
		for (ManyToOneLinkAssistant linkAssistRun : manyToOneLinkAssistants) {
			DtoAssistant other = linkAssistRun.getOppositeDtoAssistant(this);
			if (other.equals(dtoAssistantToLookFor)) {
				return linkAssistRun;
			}
		}
		return null;
	}


	public ManyToManyLinkAssistant getManyToManyLinkAssistantTo(DtoAssistant oppositeDtoAssistant) {
		for (ManyToManyLinkAssistant linkAssistRun : manyToManyLinkAssistants) {
			DtoAssistant other = linkAssistRun.getOppositeDtoAssistant(this);
			if (other.equals(oppositeDtoAssistant)) {
				return linkAssistRun;
			}
		}
		return null;
	}


	public boolean isNewEntity(T entity) {
		BaseVal val = getValueAsBaseType(entity, idAssistant.getFieldName());
		if (val == null) {
			return true;
		} else {
			return false;
		}
	}



//	public void addLinkAssistant(DtoAssistant childAssistant, JoinDescription joinDescription) {
//		if (linkAssistants == null) {
//			linkAssistants = new ArrayList<>();
//		}
//		var la = new LinkAssistant(LinkAssistant.LinkType.MANY_TO_ONE, childAssistant, this, );
//		addLinkAssistant(la);
//	}



	public boolean linkAlreadyExists(ManyToOneLinkAssistant potentialNewManyToOneLinkAssistant) {
		if (manyToOneLinkAssistants !=null) {
			for (var run : manyToOneLinkAssistants) {
				if (run.isSynonymous(potentialNewManyToOneLinkAssistant)) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	public boolean linkAlreadyExists(ManyToManyLinkAssistant potentialNewManyToManyLinkAssistant) {
		if (manyToManyLinkAssistants !=null) {
			for (var run : manyToManyLinkAssistants) {
				if (run.isSynonymous(potentialNewManyToManyLinkAssistant)) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	public void addLinkAssistant(ManyToOneLinkAssistant manyToOneLinkAssistant) {
		assert(!linkAlreadyExists(manyToOneLinkAssistant));
		if (manyToOneLinkAssistants == null) {
			manyToOneLinkAssistants = new ArrayList<>();
		}
		manyToOneLinkAssistants.add(manyToOneLinkAssistant);
	}

	public void addLinkAssistant(ManyToManyLinkAssistant manyToManyLinkAssistant) {
		assert(!linkAlreadyExists(manyToManyLinkAssistant));
		if (manyToManyLinkAssistants == null) {
			manyToManyLinkAssistants = new ArrayList<>();
		}
		manyToManyLinkAssistants.add(manyToManyLinkAssistant);
	}


	public void addChildAssistant(DtoAssistant childDto, FieldAssistant mappedBy) {
		ManyToOneLinkAssistant la = new ManyToOneLinkAssistant(childDto, this, mappedBy);
		addLinkAssistant(la);
	}


//	public <B> ManyToManyLinkAssistant<T, B> addManyToManyLinkAssistant(DtoAssistant<B> otherDto, String manyToManyTableName, String fkColNameThisDto, String fkColNameOtherDto) {
//		ManyToManyLinkAssistant la = new ManyToManyLinkAssistant(this, otherDto, manyToManyTableName, fkColNameThisDto, fkColNameOtherDto);
//		addLinkAssistant(la);
//		return la;
//	}

//	public <B> ManyToManyLinkAssistant<T, B> addManyToManyLinkAssistantOnBothAssistants(DtoAssistant<B> otherDto, String manyToManyTableName, String fkColNameThisDto, String fkColNameOtherDto) {
//		ManyToManyLinkAssistant la = new ManyToManyLinkAssistant(this, otherDto, manyToManyTableName, fkColNameThisDto, fkColNameOtherDto);
//		addLinkAssistant(la);
//		otherDto.addLinkAssistant(la);
//		return la;
//	}


	public FieldAssistant getIdAssistant() {
		return idAssistant;
	}

	public boolean hasSameTableNameAs(DtoAssistant dtoAssistant) {

		assert(this.getTableName() != null);

		if (dtoAssistant == this) {
			return true;
		}

		if (dtoAssistant.getTableName().equals(this.getTableName())) {
			return true;
		}

		return false;
	}





	public String createDeleteQuery(T dto) {
		String id = getId(dto).toDbFormat();
		FieldAssistant idFieldAssistant = getIdAssistant();
		String idColName = idFieldAssistant.getDbColName();
		String queryStr = "delete from " + getTableName() + " where " + idColName + " = " + id;
		return queryStr;
	}

	public String createSelectQuery(String joinClause, String whereClause) {
		String s = "select ";

		boolean isFistElement = true;
		for (FieldAssistant faRun : getFieldAssistants()) {
			String colName = faRun.getDbColName();

			if (!isFistElement) {
				s += ", ";
			}

			s += colName;
			isFistElement = false;
		}

		s += " from " + getTableName();

		if (CommonUtils.hasInfo(joinClause)) {
			s+= " " + joinClause;
		}

		if (CommonUtils.hasInfo(whereClause)) {
			s += " where " + whereClause;
		}

		return s;
	}

	public boolean selfCheck() {
		if (getFieldNames().length == fieldAssistants.size()) {
			return true;
		} else {
			return false;
		}
	}

	public String getDbCreateStatement() {
		// See also https://thorben-janssen.com/hibernate-5-date-and-time/

		String s = "create table " + getTableName() + " (\n";

		boolean isFirstCol = true;
		String primaryKeyPart = null;
		for (FieldAssistant fa : fieldAssistants.values()) {
			String columnName = fa.getDbColName();

			String columnTypeSql =
					switch (fa.getFieldType()) {
						case LOCALDATE -> "date";
						case LOCALDATETIME -> "timestamp";
						case INT -> "integer";
						case LONG -> "bigint";
						case BOOL -> "boolean";   // TODO: Test
						case FLOAT -> "float";
						case STR -> "varchar";
						case ENM -> "varchar";
						case LOCALTIME -> "time";
					};


			if (!isFirstCol) {
				s+= ",\n";
			}

			String primaryKey = null;

			s += columnName + " " + columnTypeSql;
			if (! fa.canBeEmpty()) {
				s += " not null";
			}



			if (fa.isKey()) {
				primaryKeyPart = ",\nprimary key(" + columnName + ")";
			}
			isFirstCol = false;
		}

		if (primaryKeyPart != null) {
			s += primaryKeyPart + "\n";
		}

		s += ")";

		return s;
	}



	public boolean equals(T left, T right) {
		for (var fa : fieldAssistants.values()) {
			var leftVal = fa.getValue(left);
			var rightVal = fa.getValue(right);
			if (java.util.Objects.equals(leftVal, rightVal)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * May return null if there is no field modified,
	 * @param left
	 * @param right
	 * @return Null, if no field was modified. Otherwise The list of modified fields in the order of the FieldAssistant.
	 */
	public ArrayList<Enum> getModifiedFields(T left, T right) {
		ArrayList<Enum> result = null;
		for (var fa : fieldAssistants.values()) {
			var leftVal = fa.getValue(left);
			var rightVal = fa.getValue(right);
			if (! java.util.Objects.equals(leftVal, rightVal)) {
				if (result == null) {
					result = new ArrayList<>();
				}
				result.add(fa.fieldName);
			}
		}
		return result;
	}

	public LinkedHashMap<Enum, Object> backupValues(T entity) {
		var hashMap = new LinkedHashMap<Enum, Object>(getColCount());
		for (var fa : fieldAssistants.values()) {
			hashMap.put(fa.getFieldName(), fa.getValue(entity));
		}
		return hashMap;
	}

}
