package com.fzenner.datademo.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.inmsg.ClientEventHandlerConsts;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.*;
import com.kewebsi.html.*;
import com.kewebsi.html.search.EntityFieldDiv;
import com.kewebsi.html.search.EntityNavigatorState;
import com.kewebsi.html.search.HtmlSearchDiv;
import com.kewebsi.html.search.LinkedEntityEditorState;
import com.kewebsi.html.table.*;


public class  EntityNavigatorDiv<T> extends SmartTag {

    public DtoAssistant<T> entityAssistant ;
    protected EntityNavigatorState entityNavigatorState;

    protected UserSession userSession;

    public EntityNavigatorDiv(UserSession userSession, Long entityId, DtoAssistant<T> dtoAssistant) {
        this.userSession = userSession;
        this.entityAssistant = dtoAssistant;
        this.entityNavigatorState = new EntityNavigatorState(dtoAssistant, "navigator");

        createHtmlObjects();

    }

    @Override
    public String getTag() {
        return "div";
    }

    public void createHtmlObjects() {

        //
        // Page elements
        //
        String idPrefix = entityNavigatorState.getPageStateId() + "-";

        var buttonTest = new HtmlButtonStandard("myTest", "Test It") {
            public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
                return StandardController.makeATest();
            }


        };


        var searchDiv = new HtmlSearchDiv(entityAssistant, entityNavigatorState, entityAssistant.getEntityNameSingular()+ "-search");
        var htmlButtonSearch = new HtmlButtonServerAction(idPrefix + "searchbutton", "Search", SearchDivController.executeEntityEditorSearch);
        PowerTableModel<T> model = entityNavigatorState.getTableModel();
        HtmlPowerTable<T> table = new HtmlPowerTable<>(model, idPrefix + "resulttable", PowerTableController.handlePowertableAction);
        var htmlButtonSaveTacos = new HtmlButtonSaveTableData(idPrefix +"savetablebutton", "Save Table", table);


        var htmlDeleteTableEntry = new HtmlButtonClientServerAction(idPrefix + "deletebutton" , "Delete selected elements",
                "CEH_openModalDialogForDeleteSelectedRows",
                PowerTableController.handlePowertableAction,
                ClientEventHandlerConsts.TABLE_ID_ATTR_NAME, table.getId());

        var htmlButtonInsertEntityForTableEdit = new HtmlButtonInsertNewEntityIntoTable(idPrefix + "insertbutton", "New Element", table);


        //
        //Layout
        //
        var twoColumnDiv = new HtmlDiv2("twocol").addCssClass("horizontalDivFlow");
        var leftColDiv = new HtmlDiv2("leftcol");
        var rightColDiv = new HtmlDiv2("rightcol").addCssClass("rightColDiv");

        addChild(twoColumnDiv);
        twoColumnDiv.addChildren(leftColDiv,rightColDiv);

        leftColDiv.addChild(buttonTest);
        // XXX3 leftColDiv.addChild(buttonSave);
        // XXX3 leftColDiv.addChild(entityFieldDiv);  // XXXXXXXXXXXXX
        leftColDiv.addChild(searchDiv);	      // XXXXXXXXXXXXX
        leftColDiv.addChild(htmlButtonSearch);
        leftColDiv.addChild(htmlButtonInsertEntityForTableEdit);
        leftColDiv.addChild(htmlButtonSaveTacos);
        leftColDiv.addChild(htmlDeleteTableEntry);

        leftColDiv.addChild(table);

        var buttonDummy = new HtmlButtonStandard(idPrefix+"dummy", "Dummy") {
            @Override
            public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
                System.out.println("This is just a love song.");
                return MsgAjaxResponse.createSuccessMsg();
            }
        };

        rightColDiv.addChild(buttonDummy);

        EntityForDetailDisplayProvider entityProvider = new EntityForDetailDisplayProvider() {
            @Override
            public ManagedEntity<T> getManagedEntityForDetailDisplay() {
                var entity =  model.getManagedEntityForDetailDisplay();
                if (entity != null) {
                    return entity;
                } else {
                    return null;
                }
            }
        };


        LinkedEntityEditorState lees = new LinkedEntityEditorState(entityProvider, entityAssistant, entityNavigatorState.getPageStateId() + "-detail");

        EntityFieldDiv efd = new EntityFieldDiv(lees.getPageStateVars());
        rightColDiv.addChild(efd);

    }


    public DtoAssistant getEntityAssistant() {
        return entityAssistant;
    }

    public EntityNavigatorState getEntityEditorState() {
        return entityNavigatorState;
    }

//	public String idPrefix() {
//		return entityAssistant.getEntityNameSingular() + "-";
//	}

}


