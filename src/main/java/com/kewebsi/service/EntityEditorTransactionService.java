package com.kewebsi.service;

import com.fzenner.datademo.StaticContextAccessor;
import com.fzenner.datademo.service.DataDemoService;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.controller.DtoAssistant;
import com.kewebsi.errorhandling.ExpectedClientDataError;
import com.kewebsi.html.search.EntityEditorState;
import com.kewebsi.html.table.ChildTableModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * In this class we put the functions that should be the all-encompassing transaction for entitiy editor operations
 */
@Service
public class EntityEditorTransactionService {

    @Transactional
    public <T> MsgAjaxResponse insertOrUpdateEntity(DtoAssistant<T> entityAssistant, EntityEditorState<T> entityEditorState) throws ExpectedClientDataError {
        T entity;

        //
        // Save the main entity
        //
        if (entityEditorState.entityIsNew()) {
            entity = entityAssistant.createEntity();
        } else {
            entity = StaticEntityService.searchForEntity(entityAssistant,  entityEditorState.getEntityId());
        }

        MsgAjaxResponse errorMsg = null;
        try {
            entityAssistant.mapPageStateToEntity(entityEditorState, entity);
        } catch (ExpectedClientDataError ex) {
            errorMsg = MsgAjaxResponse.createErrorMsg(ex.getMessage());
        }

        if (errorMsg != null) {
            return errorMsg;
        }

        if (entityEditorState.hasError()) {
            return MsgAjaxResponse.createErrorMsg(entityEditorState.getError().getErrorMsg());
        }

        DataDemoService dataDemoService = StaticContextAccessor.getBean(DataDemoService.class);
        entity = dataDemoService.saveEntity(entity, entityAssistant);
        entityAssistant.mapEntityToPageState(entity, entityEditorState);

        //
        // Save the child tables
        //
        Collection<ChildTableModel> childrenTableStates = entityEditorState.getChildrenTableStates();
        if (childrenTableStates != null) {
            for (var childTableModel : childrenTableStates) {
                PowertablePersistService ptps = StaticContextAccessor.getBean(PowertablePersistService.class);
                ptps.persist(childTableModel);
            }
        }

        return MsgAjaxResponse.createSuccessMsg();
    }


}
