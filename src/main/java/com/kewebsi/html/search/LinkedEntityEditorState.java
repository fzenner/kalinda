package com.kewebsi.html.search;

import com.kewebsi.controller.DtoAssistant;
import com.kewebsi.controller.FieldAssistant;
import com.kewebsi.controller.LinkAssistant;
import com.kewebsi.html.PageState;
import com.kewebsi.html.table.ChildTableModel;
import com.kewebsi.html.table.EntityForDetailDisplayProvider;
import com.kewebsi.service.PageVarEntityProviderBacked;
import com.kewebsi.service.PageVarEntityProviderBackedDateTime;

import java.util.Collection;
import java.util.LinkedHashMap;

public class LinkedEntityEditorState<T> extends PageState implements EntityProvider {

    protected DtoAssistant<T> dtoAssistant;

    protected EntityForDetailDisplayProvider<T> entityForDetailDisplayProvider;

    protected LinkedHashMap<String, ChildTableModel<T>> childrenTableStatesHashMap;

    public LinkedEntityEditorState(EntityForDetailDisplayProvider entityForDetailDisplayProvider, DtoAssistant<T> dtoAssistant, String pageStateId) {
        this.dtoAssistant = dtoAssistant;
        this.entityForDetailDisplayProvider = entityForDetailDisplayProvider;
        this.pageStateId = pageStateId;

        for (var fa : dtoAssistant.getFieldAssistants()) {

            PageVarEntityProviderBacked psv;
            if (fa.getFieldType() == FieldAssistant.FieldType.LOCALDATETIME) {
                psv = new PageVarEntityProviderBackedDateTime(this, entityForDetailDisplayProvider, dtoAssistant, fa);
            } else {
                psv = new PageVarEntityProviderBacked<>(this, entityForDetailDisplayProvider, dtoAssistant, fa);
            }


        }

        var m2mChildAssistants = dtoAssistant.getManyToManyLinkAssistants();
        if (m2mChildAssistants != null) {
            for (var linkAssistant : m2mChildAssistants) {
                addChildTableState(linkAssistant);
            }
        }

        var m2oneChildAssistants = dtoAssistant.getManyToOneLinkAssistants();
        if (m2oneChildAssistants != null) {
            for (var linkAssistant : m2oneChildAssistants) {
                addChildTableState(linkAssistant);
            }
        }

//        var childAssistants = dtoAssistant.getManyToOneLinkAssistants();
//        if (childAssistants != null) {
//            for (var linkAssistant : dtoAssistant.getManyToManyLinkAssistants()) {
//                addChildTableState(linkAssistant);
//            }
//        }

    }


    public void clear() {
        clearPageVars();
        // entity = null;
    }

//    public boolean isReferringToExistingEntity() {
//        return entityId != null;
//    }
//
//    public void setEntityId(Long entityId) {
//        if (this.entityId != null) {
//            if (entityId != null) {
//                if (! this.entityId.equals(entityId) )
//                throw new CodingErrorException("Overwriting an ID is not allowed.");
//            }
//        }
//        this.entityId = entityId;
//    }

    @Override
    public Object getEntity() {
        return entityForDetailDisplayProvider.getManagedEntityForDetailDisplay().getEntity();
    }

    @Override
    public Long getEntityId() {
        T entity = entityForDetailDisplayProvider.getManagedEntityForDetailDisplay().getEntity();
        return dtoAssistant.getId(entity).getVal();
    }

//    public boolean entityIsNew() {
//        if (entityId == null) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    public LinkedHashMap<String, ChildTableModel<T>> getChildrenTableStatesHashMap() {
        return childrenTableStatesHashMap;
    }

    public Collection<ChildTableModel<T>> getChildrenTableStates() {
        if (childrenTableStatesHashMap == null) return null;
        return childrenTableStatesHashMap.values();
    }


    public void addChildTableState(LinkAssistant linkAssistant) {

        DtoAssistant childAssistant = linkAssistant.getOppositeDtoAssistant(dtoAssistant);

        var ctm = new ChildTableModel(childAssistant, linkAssistant, this);
        if (childrenTableStatesHashMap == null) {
            childrenTableStatesHashMap = new LinkedHashMap<>();
        }
        childrenTableStatesHashMap.put(childAssistant.getAssistantId(), ctm);
    }

}
