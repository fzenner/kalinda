package com.fzenner.datademo.gui;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.controller.DataDemoController;
import com.fzenner.datademo.entity.ingredient.Ingredient;
import com.fzenner.datademo.entity.ingredient.IngredientAssistant;
import com.fzenner.datademo.entity.ingredient.IngredientPowerTable;
import com.fzenner.datademo.entity.taco.TacoAssistant;
import com.fzenner.datademo.entity.taco.TacoPowerTable;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.inmsg.ClientEventHandlerConsts;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.PowerTableController;
import com.kewebsi.html.*;
import com.kewebsi.html.table.HtmlButtonInsertNewEntityIntoTable;
import com.kewebsi.html.table.HtmlButtonSaveTableData;
import com.kewebsi.service.PageVarStringColdLink;

public class DataDemoPage extends HtmlPage {

	DataDemoPageState pageState;


	public DataDemoPage(UserSession userSession) {
		super("Kewebsi", PAGENAME);
		this.userSession = userSession;
		userSession.registerPage(this);
		pageState = new DataDemoPageState();

		createHtmlObjects();

	}


	public static final String  TACO_POWERSEARCHTABLE_HTML_ID = "tacoPowerSearchTable";
	public static final String  INGREDIENT_POWERSEARCHTABLE_HTML_ID = "ingredientPowerSearchTable";
	
	public static final String MODAL_DIALOG_ID = "modalDialogId";

	public static final String PAGENAME = "dataDemoPage";

	public static final String CALENDAR_FIELD_ID = "calendartestfield";


	public void createHtmlObjects() {
		
		
		
		//
		// Taco value display fields
		//

		var htmlFieldTacoId = new HtmlInput(DataDemoPageState::getTacoId, TacoAssistant.TacoFields.tacoId).setReadOnly(true);
		var htmlFieldTacoName = new HtmlInput(DataDemoPageState::getTacoName, TacoAssistant.TacoFields.tacoName);
		var htmlText = new HtmlDynPlainText(DataDemoPageState::getTacoCreatedAt, TacoAssistant.TacoFields.tacoCreatedAt);
		TacoPowerTable tacoPowerTable = new TacoPowerTable(pageState.getTacoPowerTableModel(),	TACO_POWERSEARCHTABLE_HTML_ID , PowerTableController.handlePowertableAction);
		var htmlButtonSaveTacos = new HtmlButtonSaveTableData("saveTableData", "Save Table", tacoPowerTable);		
		
		
		//
		// Taco buttons
		//
		var htmlButtonInsertTaco = new HtmlButtonInvokeServiceWithInputFieldData(
				"insertTaco", 
				"Insert taco", 
				DataDemoController.insertTacoAndUpdatePage, 
				TacoAssistant.getGlobalInstance().getFieldNamesAsJsonArray());
		
		//
		// Table-related buttons
		var htmlButtonInsertTacoForTableEdit = new HtmlButtonInsertNewEntityIntoTable("insertTacoTableEdit2", "New Taco in Table", tacoPowerTable);
		
		var htmlButtonDeleteSelectedTacos = new HtmlButtonClientServerAction("openModaldeleteSelectedTacos" , "Delete selected elements with Modal", 
				"CEH_openModalDialogForDeleteSelectedRows",
				PowerTableController.handlePowertableAction,
				ClientEventHandlerConsts.TABLE_ID_ATTR_NAME, TACO_POWERSEARCHTABLE_HTML_ID);


		var htmlButtonDeleteSelectedTacos2 = new HtmlButtonStandard("openModaldeleteSelectedTacos", "Delete selected elements with Modal") {

			@Override
			public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
				return PowerTableController.openModalDeleteSelectedRows(tacoPowerTable);
			}
		};
		
		
	// 	var htmlButtonDeleteSelectedTacos = new HtmlButtonDeleteSelected("openModaldeleteSelectedTacos" , "Delete selected elements with Modal", tacoPowerTable);		
		var htmlButtonSearchForTacos = new HtmlButtonInvokeServiceWithInputFieldData("searchForTacos", "Find tacos", 
				DataDemoController.searchForTacos, 
				TacoAssistant.getGlobalInstance().getFieldNamesAsJsonArray());
		
		
		// var htmlFieldCalendar2 = new HtmlPageVarField<DataDemoPageState>(CALENDAR_FIELD_ID);


		String testDateEditorId = "testDateEditorId";
		// var htmlDateEditor = new HtmlDateEditor<DataDemoPageState>(testDateEditorId);
		new PageVarStringColdLink(testDateEditorId, getPageState());


		var htmlSelect = new HtmlSelect(DataDemoPageState::getPingo, "pingo").linkCallback(DataDemoController.setPingo);
		htmlSelect.addOption("a1", "s1");
		htmlSelect.addOption("a2", "s2");
		htmlSelect.addOption("a3", "s3");
		
		
		//
		// Ingredient fields
		// 
		var htmlFieldIngredientId = new HtmlInput(DataDemoPageState::getIngredientId, IngredientAssistant.IngredientFields.ingredientId).setReadOnly(true);
		var htmlFieldIngredientName = new HtmlInput(DataDemoPageState::getIngredientName, IngredientAssistant.IngredientFields.ingredientName);
		var htmlIngredientType = new HtmlSelect(DataDemoPageState::getIngredientType, IngredientAssistant.IngredientFields.ingredientType);
		htmlIngredientType.setOptions(HtmlSelectOption.createDefaultOptionList(Ingredient.IngredientType.class));
	
		var htmlButtonInsertIngredient = new HtmlButtonInvokeServiceWithInputFieldData(
				"insertIngredient", 
				"Insert Ingredient",
				DataDemoController.insertIngredientAndUpdatePage,
				IngredientAssistant.getGlobalInstance().getFieldNamesAsJsonArray());
		
		
		var htmlButtonSearchForIngredients = new HtmlButtonInvokeServiceWithInputFieldData("searchForIngredients", "Find ingredients", 
				DataDemoController.searchForIngredients, 
				TacoAssistant.getGlobalInstance().getFieldNamesAsJsonArray());
		
		
		IngredientPowerTable ingredientPowerTable = new IngredientPowerTable(pageState.getIngredientPowerTableModel(),	INGREDIENT_POWERSEARCHTABLE_HTML_ID , PowerTableController.handlePowertableAction);

		
		
		var htmlButtonSaveIngredient =  new HtmlButtonSaveTableData("saveIngredients", "Save Ingredients", ingredientPowerTable);		
		

		
		//
		// Layout Ingredient
		//
		HtmlDiv htmlDivIngredient = new HtmlDiv(HtmlDiv.cssVertikalInputFieldGrid2Cols);
		
		htmlDivIngredient.addLabelAndTag("Ingredient", htmlFieldIngredientId);
		htmlDivIngredient.addLabelAndTag("Ingredient Name", htmlFieldIngredientName);
		htmlDivIngredient.addLabelAndTag("Ingredient Type", htmlIngredientType);
		htmlDivIngredient.addChild(htmlButtonInsertIngredient);
		htmlDivIngredient.addChild(htmlButtonSearchForIngredients);
		htmlDivIngredient.addChild(htmlButtonSaveIngredient);
		

		body.addChild(htmlDivIngredient);
		body.addChild(ingredientPowerTable);

		
		//
		// Layout Taco
		//
		HtmlDiv htmlDivTaco = new HtmlDiv(HtmlDiv.cssVertikalInputFieldGrid2Cols);
		body.addChild(htmlDivTaco);
		htmlDivTaco.addLabelAndTag("TacoId", htmlFieldTacoId);
		htmlDivTaco.addLabelAndTag("Taco Name", htmlFieldTacoName);
		htmlDivTaco.addLabelAndTag("Created at", htmlText);
		htmlDivTaco.addChildren(
				htmlButtonInsertTaco, 
				htmlButtonInsertTacoForTableEdit, 
				htmlButtonDeleteSelectedTacos, 
				htmlButtonSearchForTacos);
		
		

		/*
		 * SearchResultTable
		 */
		body.addChild(new HtmlDiv(HtmlDiv.cssScrollOverflow, tacoPowerTable));
		body.addChild(htmlButtonSaveTacos);
		
		


	}

	protected ArrayList<HtmlSelectOption> selectOptionsForIngredientType() {
		return HtmlSelectOption.createDefaultOptionList(Ingredient.IngredientType.class);

	}

	@Override
	public PageState getPageState() {
		return pageState;
	}

}
