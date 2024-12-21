package com.fzenner.datademo.entity.ingredient;

import static com.kewebsi.util.CommonUtils.modelToHtml;

import java.util.LinkedHashMap;
import java.util.function.Function;

import com.fzenner.datademo.entity.ReservationAssistant;
import com.fzenner.datademo.entity.taco.Taco;
import com.kewebsi.controller.*;
import com.kewebsi.errorhandling.BackendDataException;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.errorhandling.ExpectedClientDataError;
import com.kewebsi.errorhandling.MalformedClientDataException;
import com.kewebsi.service.FieldError;
import com.kewebsi.util.CommonUtils;
import com.kewebsi.util.EnumUtils;

public class IngredientAssistant extends DtoAssistant<Ingredient> {

	private IngredientAssistant() {

	}

	public enum IngredientFields {
		ingredientId, 
		ingredientName, 
		ingredientType
	}
	
	
	public void init() {
		//
		// Id
		//
		FieldAssistantLong<Ingredient> idAssist = FieldAssistant.longId(IngredientFields.ingredientId);
		idAssist.setFieldLabel("Ingredient Id");
		idAssist.setDbColName("id").setIsKey(true);
		idAssist.setSetter((entity, val) -> entity.setId(val));
		idAssist.setGetter( entity -> entity.getId());
		addFieldAssistant(idAssist);

		//
		// name
		//
		// addFieldAssistant(FieldAssistant.str(IngredientFields.ingredientName).setDbColName("name"));
		FieldAssistantStr<Ingredient> nameAssist = new FieldAssistantStr(IngredientFields.ingredientName);
		nameAssist.setDbColName("name").setEditable(true);
		nameAssist.setSetter((entity, val) -> entity.setName(val));
		nameAssist.setGetter(ingredient -> ingredient.getName());
		addFieldAssistant(nameAssist);

		//
		// type
		//
		//addFieldAssistant(FieldAssistant.enm(IngredientFields.ingredientType, Ingredient.IngredientType.class).setEditable(true).setDbColName("type"));
		FieldAssistantEnum<Ingredient, Ingredient.IngredientType> typeAssist = new FieldAssistantEnum(IngredientFields.ingredientType);
		typeAssist.setEnumClass(Ingredient.IngredientType.class).setEditable(true);
		typeAssist.setDbColName("type");
		typeAssist.setSetter((entity, val) -> entity.setType(val));
		typeAssist.setGetter(entity -> entity.getType());
		addFieldAssistant(typeAssist);

		assert(selfCheck());
	}



//	@Override
//	public FieldAssistant getFieldAssistant(String fieldName) {   // TODO: Create analogous method with enum as parameter and use that one everywhere. Eleminate string usage.
//		IngredientFields field = IngredientFields.valueOf(fieldName);
//		FieldAssistant fieldAssistant = getFieldAssistant(field);
//		return fieldAssistant;
//	}


	/**
	 * Boilerplate
	 */
	
	protected static IngredientAssistant globalInstance;

	public static IngredientAssistant getGlobalInstance() {
		if (globalInstance == null) {
			globalInstance = new IngredientAssistant();
			globalInstance.init();
		}
		return globalInstance;
	}
	
	
//	@Override
//	public Enum<?>[] getFieldNames() {
//		return IngredientFields.values();
//	}

	@Override
	public Class<?> getEnumClass() {
		return IngredientFields.class;
	}

	@Override
	public Class<Ingredient> getEntityClass() {
		return Ingredient.class;
	}

	@Override
	public String getTableName() {
		return "Ingredient";
	}

}

