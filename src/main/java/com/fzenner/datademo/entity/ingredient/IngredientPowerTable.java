package com.fzenner.datademo.entity.ingredient;

import com.fzenner.datademo.gui.DataDemoPageState;
import com.kewebsi.controller.StaticGuiDelegate;
import com.kewebsi.html.StringModelGetter;
import com.kewebsi.html.table.HtmlPowerTable;
import com.kewebsi.html.table.PowerTableModel;
import com.kewebsi.html.table.PowerTableModelImpl;



public class IngredientPowerTable extends HtmlPowerTable<Ingredient>{

	public IngredientPowerTable(PowerTableModel<Ingredient> modelGetter, String id, StaticGuiDelegate serverMsgHandler) {
		super(modelGetter, id, serverMsgHandler);
		setDefaultColWidth();
	}
	
	public void setDefaultColWidth() {
		setColWidth(IngredientAssistant.IngredientFields.ingredientId, "50px");
		setColWidth(IngredientAssistant.IngredientFields.ingredientName, "125px");
		setColWidth(IngredientAssistant.IngredientFields.ingredientType, "125px");
	}


//	@Override
//	protected PowerTableModel<Ingredient> getModelForNoPageState() {
//		return new PowerTableModelImpl<>(IngredientAssistant.getGlobalInstance());
//	}
	

	
	
}
