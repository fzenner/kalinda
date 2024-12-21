	package com.fzenner.datademo.gui;

import static com.fzenner.datademo.gui.DataDemoPage.CALENDAR_FIELD_ID;
import static com.kewebsi.util.CommonUtils.escape;

import com.fzenner.datademo.entity.ingredient.Ingredient;
import com.fzenner.datademo.entity.ingredient.IngredientAssistant;
import com.fzenner.datademo.entity.taco.Taco;
import com.fzenner.datademo.entity.taco.TacoAssistant;
import com.kewebsi.html.PageState;
import com.kewebsi.html.table.MainTableModel;
import com.kewebsi.html.table.PowerTableModelImpl;
import com.kewebsi.service.PageStateVarColdLink;
import com.kewebsi.service.PageVarStringColdLink;
import com.kewebsi.util.CommonUtils;

/**
 * The page state holds most data as string. This allows for handling 
 * not yet valid data server-side, until it is persisted.
 * @author A4328059
 *
 */
public class DataDemoPageState extends PageState {
	
	private String tacoId;
	private String tacoName;
	private String tacoCreatedAt;
	
	
	private String ingredientId;
	private String ingredientName;
	private String ingredientType;
	
	
	public MainTableModel<Taco> tacoPowerTableModel;
	public MainTableModel<Ingredient> ingredientPowerTableModel;
	
	protected String pingo;
	
	
	private PageVarStringColdLink testDateVar = new PageVarStringColdLink(CALENDAR_FIELD_ID, this);

	
	public String testYear;
	
	
	public DataDemoPageState() {
		this.pageStateId = "";
	}

	public String getIngredientId() {
		return escape(ingredientId);
	}

	public void setIngredientId(String ingredientId) {
		this.ingredientId = ingredientId;
	}
	
	
	public void setIngredientId(Long ingredientId) {
		this.ingredientId = Long.toString(ingredientId);
	}

	public String getIngredientName() {
		return escape(ingredientName);
	}

	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}

	public String getIngredientType() {
		return CommonUtils.modelToHtml(ingredientType);
	}

	public void setIngredientType(Ingredient.IngredientType ingredientType) {
		this.ingredientType = CommonUtils.modelToHtml(ingredientType);
	}
	
	public void setIngredientType(String ingredientType) {
		this.ingredientType = ingredientType;
	}


	public void setIngredients(Iterable<Ingredient> ingredients) {
		ingredientPowerTableModel = new MainTableModel<>(ingredients, IngredientAssistant.getGlobalInstance());
		// this.ingredients = ingredients;
	}
	
	
	public String getTacoId() {
		return escape(tacoId);
	}

	public void setTacoId(String tacoId) {
		this.tacoId = tacoId;
	}

	public void setTacoId(Long tacoId) {
		this.tacoId = tacoId.toString();
	}
	
	
	public String getTacoName() {
		return escape(tacoName);
	}

	public void setTacoName(String tacoName) {
		this.tacoName = tacoName;
	}

	public String getTacoCreatedAt() {
		return escape(tacoCreatedAt);
	}

	public void setTacoCreatedAt(String tacoCreatedAt) {
		this.tacoCreatedAt = tacoCreatedAt;
	}
	

	public void setTacos(Iterable<Taco> tacos) {
		getTacoPowerTableModel().setData(tacos);
	}
	


	public MainTableModel<Taco> getTacoPowerTableModel() {
		if (tacoPowerTableModel == null) {
			tacoPowerTableModel = new MainTableModel<Taco>(TacoAssistant.getGlobalInstance());
			tacoPowerTableModel.initPowerTableModelImpl(TacoAssistant.getGlobalInstance());
		}		
		return tacoPowerTableModel;
	}
	
	public PowerTableModelImpl<Ingredient> getIngredientPowerTableModel() {
		if (ingredientPowerTableModel == null) {
			ingredientPowerTableModel = new MainTableModel<Ingredient>(IngredientAssistant.getGlobalInstance());
			ingredientPowerTableModel.initPowerTableModelImpl(IngredientAssistant.getGlobalInstance());
		}		
		return ingredientPowerTableModel;
	}
	
	
	public String getTestYear() {
		return testYear;
	}
	
	
	public String getPingo() {
		return pingo;
	}
	
	
	public void setPingo(String pingo) {
		this.pingo = pingo;
	}
	
	
	public void setTestYear(String testYear) {
		this.testYear = testYear;
	}

	public PageStateVarColdLink getTestDateVar() {
		return testDateVar;
	}

	public void setTestDateVar(PageVarStringColdLink testDateVar) {
		this.testDateVar = testDateVar;
	}



}
