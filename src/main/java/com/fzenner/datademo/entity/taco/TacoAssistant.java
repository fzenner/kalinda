package com.fzenner.datademo.entity.taco;

import static com.kewebsi.util.CommonUtils.modelToHtml;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.fzenner.datademo.entity.TableNames;
import com.fzenner.datademo.entity.ingredient.IngredientAssistant;
import com.kewebsi.controller.*;


public class TacoAssistant extends DtoAssistant<Taco>  {

	public enum TacoFields {
		tacoId,
		tacoName,
		tacoCreatedAt,
	}

	public void init() {
		// fieldAssistants = new LinkedHashMap<>(TacoFields.values().length);

		//
		// id
		//
		FieldAssistantLong<Taco> idAssist = (FieldAssistantLong<Taco>)FieldAssistant.longId(TacoFields.tacoId).setFieldLabel("Taco ID");
		idAssist.setDefaultWidthInChar(10).setDbColName("id").setIsKey(true);
		idAssist.setGetter(entity -> entity.getId());
		idAssist.setSetter((entity, val) -> entity.setId(val));
		addFieldAssistant(idAssist);

		//
		// name
		//
		FieldAssistantStr<Taco> nameAssist = (FieldAssistantStr<Taco>) FieldAssistant.str(TacoFields.tacoName).setFieldLabel("Name").setDefaultWidthInChar(20).setDbColName("Name").setMinLength(2).setMaxLength(20);
		nameAssist.setGetter(entity -> entity.getName());
		nameAssist.setSetter((entity, val) -> entity.setName(val));
		addFieldAssistant(nameAssist);

		//
		// createdAt
		//
		FieldAssistantLocalDateTime<Taco> createdAtAssist = FieldAssistant.localDateTime(TacoFields.tacoCreatedAt).setIsCreationTimeStamp(true);
		createdAtAssist.setFieldLabel("Created at");
		// createdAtAssist.setReadOnly().setDefaultWidthInChar(15).setDbColName("created_at");
		createdAtAssist.setDefaultWidthInChar(15).setDbColName("created_at");
		createdAtAssist.setGetter(entity -> entity.getCreatedAt());
		createdAtAssist.setSetter((entity, val) -> entity.setCreatedAt(val));
		addFieldAssistant(createdAtAssist);

		// addManyToManyLinkAssistantOnBothAssistants(IngredientAssistant.getGlobalInstance(), TableNames.Taco_Ingredients, "taco_id", "ingredients_id");

		assert(selfCheck());
	}
	


//	public FieldAssistant getFieldAssistant(String fieldName) {
//		TacoFields field = TacoFields.valueOf(fieldName);
//		return getFieldAssistant(field);
//	}


	@Override
	public Class<?> getEnumClass() {
		return TacoFields.class;
	}

	/**
	 * Boilerplate from here.
	 */
	

	
	
//	@Override
//	public Enum<?>[] getFieldNames() {
//		return TacoFields.values();
//	}




	protected static TacoAssistant globalInstance;
	public static TacoAssistant getGlobalInstance() {
		if (globalInstance == null) {
			globalInstance = new TacoAssistant();
			globalInstance.init();
		}
		return globalInstance;
	}


	@Override
	public Class<Taco> getEntityClass() {
		return Taco.class;
	}


	@Override
	public String getTableName() {
		return "Taco";
	}



}
