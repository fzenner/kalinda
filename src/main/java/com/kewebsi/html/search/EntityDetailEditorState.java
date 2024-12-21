package com.kewebsi.html.search;

import com.kewebsi.controller.DtoAssistant;
import com.kewebsi.controller.LinkAssistant;
import com.kewebsi.controller.ManagedEntity;
import com.kewebsi.html.PageState;
import com.kewebsi.html.table.ChildTableModel;
import com.kewebsi.html.table.EntityForDetailDisplayProvider;
import com.kewebsi.service.PageStateVarColdLink;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Entity editor to be used to display details of an entity that is selected in a related table.
 * @param <T>
 */


public class EntityDetailEditorState<T> extends PageState implements EntityProvider {


    public EntityDetailEditorState(EntityForDetailDisplayProvider entityForDetailDisplayProvider, DtoAssistant dtoAssistant, String pageStateId) {
        this.entityForDetailDisplayProvider = entityForDetailDisplayProvider;
        this.dtoAssistant = dtoAssistant;
        this.pageStateId = pageStateId;

    }

    protected  EntityForDetailDisplayProvider<T> entityForDetailDisplayProvider;
    // protected Supplier<T> masterEntityGetter;


//    protected void setMasterEntityGetter(Supplier<M> masterEntityGetter) {
//        this.masterEntityGetter = masterEntityGetter;
//    }

    protected ManagedEntity<T> getMasterEntity() {
        return entityForDetailDisplayProvider.getManagedEntityForDetailDisplay();
    }

    protected DtoAssistant<T> dtoAssistant;

    /** This is in a way a redundant field. Its purpose is to allow for greater robustness.
     * The value of this field should be always the same as the PageStateVar holding the entity id.
     * So if the user presses on "New", this value should be null. If the user saves client data,
     * then the entity id coming from the client should match this value here. If not, it is an illegal attempt to
     * save date from an invalid or illegal key.
     */
    protected Long entityId;



    protected LinkedHashMap<LinkAssistant, ChildTableModel<T>> childrenTableStatesHashMap;

    public EntityDetailEditorState(DtoAssistant<T> dtoAssistant, String pageStateId) {
        this.pageStateId = pageStateId;
        this.dtoAssistant = dtoAssistant;

        for (var fa : dtoAssistant.getFieldAssistants()) {
            var psv = PageStateVarColdLink.createPageStateVarColdLink(this, fa, dtoAssistant.getAssistantId());
            // this.pageStateVars.put(psv.getHtmlId(), psv);
            registerPageStateVar(psv);
        }

        var childAssistants = dtoAssistant.getManyToOneLinkAssistants();
        if (childAssistants != null) {
            for (var linkAssistant : dtoAssistant.getManyToOneLinkAssistants()) {
                addChildTableState(linkAssistant);
            }
        }


    }

    public boolean isReferringToExistingEntity() {
        return entityId != null;
    }

//    public Long getEntityId() {
//        T entity = entityForDetailDisplayProvider.getManagedEntityForDetailDisplay().getEntity();
//        return dtoAssistant.getId(entity).getVal();
//    }

    @Override
    public Long getEntityId() {
        return dtoAssistant.getId(getEntity()).getVal();
    }

    public T getEntity() {
        T entity = entityForDetailDisplayProvider.getManagedEntityForDetailDisplay().getEntity();
        return entity;
    }

//    public void setEntityId(Long entityId) {
//        if (this.entityId != null) {
//            if (entityId != null) {
//                if (this.entityId != entityId )
//                throw new CodingErrorException("Overwriting an ID is not allowed.");
//            }
//        }
//        this.entityId = entityId;
//    }

//    public Long getEntityId() {
//        return entityId;
//    }

    public LinkedHashMap<LinkAssistant, ChildTableModel<T>> getChildrenTableStatesHashMap() {
        return childrenTableStatesHashMap;
    }

    public Collection<ChildTableModel<T>> getChildrenTableStates() {
        if (childrenTableStatesHashMap == null) return null;
        return childrenTableStatesHashMap.values();
    }


    private void addChildTableState(LinkAssistant linkTo) {
        var cts = new ChildTableModel(dtoAssistant, linkTo, this);
        if (childrenTableStatesHashMap == null) {
            childrenTableStatesHashMap = new LinkedHashMap<>();
        }
        childrenTableStatesHashMap.put(linkTo, cts);
    }
//    public void addChildTableStateOld(ManyToOneLinkAssistant manyToOneLinkAssistant) {
//
//        // DEBUG_MODE
//        var linkFrom = manyToOneLinkAssistant.getLinkFrom();
//        if (! linkFrom.getSymbol().equals(dtoAssistant.getSymbol())) {
//            throw new CodingErrorException("Logical error linking DtoAssistants!");
//        }
//
//        var linkTo = manyToOneLinkAssistant.getLinkTo();
//        var cts = new ChildrenTableState(linkTo);
//        if (childrenTableStatesHashMap == null) {
//            childrenTableStatesHashMap = new LinkedHashMap<>();
//        }
//        childrenTableStatesHashMap.put(linkTo.getAssistantId(), cts);
//    }

}
