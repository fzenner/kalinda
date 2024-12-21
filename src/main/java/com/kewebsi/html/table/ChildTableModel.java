package com.kewebsi.html.table;

import com.kewebsi.controller.DtoAssistant;
import com.kewebsi.controller.LinkAssistant;
import com.kewebsi.html.search.EntityProvider;
import org.springframework.transaction.annotation.Transactional;

public class ChildTableModel<T> extends PowerTableModelImpl<T> {

    DtoAssistant parentDtoAssistant;
    protected LinkAssistant parentLinkAssistant;

    // protected Long parentId;

    protected EntityProvider parentIdProvider;


    public ChildTableModel(DtoAssistant dtoAssistant, LinkAssistant parentLinkAssistant, EntityProvider parentIdProvider) {
        this.parentDtoAssistant = parentLinkAssistant.getOppositeDtoAssistant(dtoAssistant);
        this.dtoAssistant = dtoAssistant;
        this.parentLinkAssistant = parentLinkAssistant;
        this.parentIdProvider = parentIdProvider;
    }



    public void setData(Iterable<T> dataList) {
        super.setData(dataList);
    }

//
//    public void setParentLinkAssistant(LinkAssistant parentLinkAssistant) {
//        this.parentLinkAssistant = parentLinkAssistant;
//    }

    public LinkAssistant getParentLinkAssistant() {
        return parentLinkAssistant;
    }

    public Long getParentId() {
        return parentIdProvider.getEntityId();
    }

//    public Object getParentEntity() {
//        return parentEntityProvider.getEntity();
//    }

    @Override
    @Transactional
    public T insertRowWithBlankEntity(int rowIdx ) {
        var newEntity = dtoAssistant.createEntity();
//
//        if (parentLinkAssistant instanceof ManyToManyLinkAssistant<?,?>) {
//            ManyToManyLinkAssistant la = (ManyToManyLinkAssistant) parentLinkAssistant;
//
//            DtoAssistant oppositeDtoAssistant = la.getOppositeDtoAssistant(dtoAssistant);
//
//            Object parentEntity = parentIdProvider.getEntity();
//            if (parentEntity != null) {
//                la.link(parentEntity, newEntity);
//            } else {
//                Long parentEntityId = parentIdProvider.getEntityId();
//                parentEntity = EntityService.searchForEntity(oppositeDtoAssistant, parentEntityId);
//                la.link(parentEntity, newEntity);
//            }
//        }
//
//        ManyToManyLinkAssistant linkAssistant = dtoAssistant.getManyToManyLinkAssistantTo(parentDtoAssistant);
//

        insertRowWithNewEntity(rowIdx, newEntity);
        notifyRowsInsertedOrDeleted();
        return newEntity;
    }


}
