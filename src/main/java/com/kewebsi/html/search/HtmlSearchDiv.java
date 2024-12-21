package com.kewebsi.html.search;

import com.kewebsi.controller.DtoAssistant;
import com.kewebsi.controller.FieldAssistant;
import com.kewebsi.html.*;

/**
 * This class displays the search options for a given entity.
 */
public class HtmlSearchDiv<T> extends HtmlDiv2 {

    protected String idPrefix;

    protected DtoAssistant<T> dtoAssistant;   // For the static html structure.

    protected EntityNavigatorState entityNavigatorState; // For thy data content.

    public HtmlSearchDiv(DtoAssistant<T> dtoAssistant, EntityNavigatorState entityNavigatorState, String id) {
        this.id = id;
        this.addCssClass("searchFieldDiv");
        this.dtoAssistant = dtoAssistant;
        this.entityNavigatorState = entityNavigatorState;
        this.idPrefix = idPrefix;

        createChildren(dtoAssistant);


    }

    private void createChildren(DtoAssistant<T> dtoAssistant) {
        var fieldAssistants = dtoAssistant.getFieldAssistants();

        for (var  fieldAssist : fieldAssistants) {
            String htmlId = getLocalFieldIdPrefix(fieldAssist);
            AtomicSearchConstraint asc = entityNavigatorState.getAtomicSearchConstraint(fieldAssist);
            HtmlAtomicConstraintRow constraintRow = new HtmlAtomicConstraintRow(fieldAssist, asc,this);
        }
    }


    public String getLocalFieldIdPrefix(FieldAssistant fieldAssistant) {
        return idPrefix + '-' + fieldAssistant.getFieldName().name() + '-';
    }

}
