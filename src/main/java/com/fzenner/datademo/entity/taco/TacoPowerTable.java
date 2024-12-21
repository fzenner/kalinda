package com.fzenner.datademo.entity.taco;

import com.fzenner.datademo.gui.DataDemoPageState;
import com.kewebsi.controller.StaticGuiDelegate;
import com.kewebsi.html.StringModelGetter;
import com.kewebsi.html.table.HtmlPowerTable;
import com.kewebsi.html.table.PowerTableModel;
import com.kewebsi.html.table.PowerTableModelImpl;

public class TacoPowerTable extends HtmlPowerTable<Taco>{

	public TacoPowerTable(PowerTableModel<Taco> modelGetter, String id, StaticGuiDelegate serverMsgHandler) {
		super(modelGetter, id, serverMsgHandler);
		setDefaultColWidth();
	}
	
	public void setDefaultColWidth() {
		setColWidth(TacoAssistant.TacoFields.tacoId, "50px");
		setColWidth(TacoAssistant.TacoFields.tacoName, "125px");
		setColWidth(TacoAssistant.TacoFields.tacoCreatedAt, "200px");
	}


//	@Override
//	protected PowerTableModel<Taco> getModelForNoPageState() {
//		return new PowerTableModelImpl<>(TacoAssistant.getGlobalInstance());
//	}
	

	
	
}
