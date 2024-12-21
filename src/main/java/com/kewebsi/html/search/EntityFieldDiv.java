package com.kewebsi.html.search;

import com.kewebsi.controller.SimpleFieldAssistant;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.html.*;

import java.util.Collection;

public class EntityFieldDiv<T> extends HtmlDiv2 {

    // protected String idPrefix;

    // protected EntityEditorState entityEditorState; // For thy data content.
    protected Collection<PageStateVarIntf> entityPageVars;

    public EntityFieldDiv(Collection<PageStateVarIntf> entityPageVars) {
        // this.id = id;
        this.addCssClass("divVertikalInputFieldGrid2Cols");
        this.entityPageVars = entityPageVars;
        // this.idPrefix = idPrefix;

        createChildren();


    }

    private void createChildren() {
        for (var pageStateVar : this.entityPageVars) {
            createInputField(pageStateVar);
        }
    }

    public void createInputField(PageStateVarIntf pageStateVar) {

        var fieldType = pageStateVar.getFieldAssistant().getFieldType();
        switch (fieldType) {

            case LONG -> createAndAppendSimpleInputField(pageStateVar);
            case INT -> createAndAppendSimpleInputField(pageStateVar);
            case STR -> createAndAppendSimpleInputField(pageStateVar);
            case LOCALDATETIME -> createAndAppendDateTimeEditor((PageStateVarDateTimeIntf) pageStateVar);
            case ENM -> createAndAppendDropDownInputField(pageStateVar);
            default -> throw new CodingErrorException("Unhandled FieldType: " + fieldType.name());
        }


    }

    public void createAndAppendSimpleInputField(PageStateVarIntf pageStateVar) {
        SimpleFieldAssistant fieldAssistant = pageStateVar.getFieldAssistant();
        String fieldId = id + "-" +fieldAssistant.getFieldName().name();



        // HtmlPageVarFieldV4 pageVarEditor = new HtmlPageVarFieldV4(fieldId , pageStateVar, fieldAssistant.isEditable());
        HtmlPageVarField pageVarEditor = new HtmlPageVarField(pageStateVar);
        // var errorWrapper = new HtmlErrorWrapper(pageVarEditor);
        var htmlLabel = new HtmlLabel(fieldAssistant.getFieldLabel(), pageVarEditor).setForTag(pageVarEditor).addCssClass("nowrapLabel");

        this.addChild(htmlLabel);
        this.addChild(pageVarEditor);
    }

    public HtmlPageVarFieldDateTime createAndAppendDateTimeEditor(PageStateVarDateTimeIntf pageStateVar) {
        SimpleFieldAssistant fieldAssistant = pageStateVar.getFieldAssistant();
        var pageVarEditor = new HtmlPageVarFieldDateTime(pageStateVar, this.id + "-");
        var htmlLabel = new HtmlLabel(fieldAssistant.getFieldLabel(), pageVarEditor).setForTag(pageVarEditor).addCssClass("nowrapLabel");
        this.addChild(htmlLabel);
        this.addChild(pageVarEditor);
        return pageVarEditor;
    }

    public void createAndAppendDropDownInputField(PageStateVarIntf<Enum> pageStateVar) {

        // PageVarEnumColdLink pageVarEnum = (PageVarEnumColdLink) pageStateVar;


        SimpleFieldAssistant fieldAssistant = pageStateVar.getFieldAssistant();
        String fieldId = id + "-" +fieldAssistant.getFieldName().name();

        var htmlSelect = new HtmlSelectV3(fieldId, pageStateVar);

        var htmlLabel = new HtmlLabel(fieldAssistant.getFieldLabel(), htmlSelect).setForTag(htmlSelect).addCssClass(".noWrapLabel");

        this.addChild(htmlLabel);

        var wrappedElement = new HtmlErrorWrapper(htmlSelect);

        // this.addChild(htmlSelect);
        this.addChild(wrappedElement);
    }




}

