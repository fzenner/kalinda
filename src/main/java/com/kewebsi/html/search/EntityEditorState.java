package com.kewebsi.html.search;

import com.kewebsi.controller.DtoAssistant;
import com.kewebsi.controller.LinkAssistant;
import com.kewebsi.html.PageState;
import com.kewebsi.html.table.ChildTableModel;
import com.kewebsi.service.PageStateVarColdLink;

import java.util.ArrayList;

public class EntityEditorState<T> extends PageState implements EntityProvider {

    protected DtoAssistant<T> dtoAssistant;

    /** This is in a way a redundant field. Its purpose is to allow for greater robustness.
     * The value of this field should be always the same as the PageStateVar holding the entity id.
     * So if the user presses on "New", this value should be null. If the user saves client data,
     * then the entity id coming from the client should match this value here. If not, it is an illegal attempt to
     * save date from an invalid or illegal key.
     */


    // protected T entity;

    protected Long entityId;

    protected ArrayList<ChildTableModel> childrenTableStates;

    public EntityEditorState(DtoAssistant<T> dtoAssistant, String pageStateId, LinkAssistant... childLinks) {
        this.pageStateId = pageStateId;
        initEntityEditorState(dtoAssistant, childLinks);
    }


    private void initEntityEditorState(DtoAssistant<T> dtoAssistant, LinkAssistant... childLinks) {
        this.dtoAssistant = dtoAssistant;

        for (var fa : dtoAssistant.getFieldAssistants()) {
            var psv = PageStateVarColdLink.createPageStateVarColdLink(this, fa, dtoAssistant.getAssistantId());
        }

        for (LinkAssistant linkAssistant : childLinks) {
            addChildTableState(linkAssistant);
        }
    }


    public void clear() {
        clearPageVars();
        // entity = null;
    }

    public boolean isReferringToExistingEntity() {
        return entityId != null;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getEntityId() {
        return entityId;
    }

    /**
     * We never have the entity, only the ID. But fetching the corresponding entity is not done here.
     * @return
     */
    public Object getEntity() {
        return null;
    }

    public boolean entityIsNew() {
        if (entityId == null) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<ChildTableModel> getChildrenTableStates() {
        return childrenTableStates;
    }

//    public Collection<ChildrenTableState> getChildrenTableStates() {
//        if (childrenTableStates == null) return null;
//        return childrenTableStates.values();
//    }

//
//    public void addChildTableStateForManyToOneLink(ManyToOneLinkAssistant manyToOneLinkAssistant) {
//        // DEBUG_MODE
//        var linkFrom = manyToOneLinkAssistant.getLinkFrom();
//        if (! linkFrom.getSymbol().equals(dtoAssistant.getSymbol())) {
//            throw new CodingErrorException("Logical error linking DtoAssistants!");
//        }
//
//        var linkTo = manyToOneLinkAssistant.getLinkTo();
//        addChildTableState(linkTo);
//    }
//
//    public void addChildTableStateForManyToManyLink(ManyToManyLinkAssistant manyToManyLinkAssistant) {
//        DtoAssistant linkTo = manyToManyLinkAssistant.getOppositeDtoAssistant(dtoAssistant);
//        addChildTableState(linkTo);
//    }

    private void addChildTableState(LinkAssistant linkTo) {
        DtoAssistant childDtoAssistant = linkTo.getOppositeDtoAssistant(dtoAssistant);
        // var cts = new ChildrenTableState(dtoAssistant, childDtoAssistant, linkTo);
        var ctl = new ChildTableModel<>(childDtoAssistant, linkTo, this);
        if (childrenTableStates == null) {
            childrenTableStates = new ArrayList<>();
        }
        childrenTableStates.add(ctl);
    }

}
