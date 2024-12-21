package com.kewebsi.service;

import com.kewebsi.controller.*;
import com.kewebsi.html.table.ChildTableModel;

import com.fzenner.datademo.StaticContextAccessor;
import com.fzenner.datademo.service.RepositorySafe;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.html.table.HtmlPowerTable;
import com.kewebsi.html.table.PowerTableModel;
import com.kewebsi.html.table.PowerTableModelRow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class PowertablePersistService {

	@Transactional
	public <E> void persist(PowerTableModel<E> tableModel) throws FieldError {
		if (tableModel.getRows() == null) {
			return;
		}

		DtoAssistant<E> dtoAssistant = tableModel.getDtoAssistant();

		for (var modelRowRun: tableModel.getRows()) {
			var entityState = modelRowRun.getEntityState();
			if (entityState == ManagedEntity.EntityState.NEW || entityState == ManagedEntity.EntityState.UPDATED) {
				ArrayList<FieldError> errors = dtoAssistant.validateExceptIdAndDbTimeStamp(modelRowRun);
				if (errors != null && ! errors.isEmpty()) {
					throw errors.get(0);
				}
			}
		}


		DbService dbService = StaticContextAccessor.getBean(DbService.class);
		System.out.println("\nXXXXXXXXXXXXXXXXXXXDbService:" + dbService);


		
		for (var modelRowRun: tableModel.getRows()) {
			E entity = modelRowRun.getEntity();

			switch (modelRowRun.getEntityState()) {
				case UPDATED: {
					dbService.persistManagedEntity(dtoAssistant, modelRowRun);
					modelRowRun.setEntityState(ManagedEntity.EntityState.UNMODIFIED);
					tableModel.touch();
				}
				break;
				case NEW: {  // Actually the same as UPDATED, since we created a new entity managed by Spring.
					dbService.persistManagedEntity(dtoAssistant, modelRowRun);


					if (tableModel instanceof ChildTableModel<?>) {
						ChildTableModel cmodel = (ChildTableModel) tableModel;
						LinkAssistant parentLinkAssistant = cmodel.getParentLinkAssistant();
						if (parentLinkAssistant instanceof ManyToManyLinkAssistant) {
							ManyToManyLinkAssistant m2mla = (ManyToManyLinkAssistant) parentLinkAssistant;
							DtoAssistant childAssistant = dtoAssistant;
							DtoAssistant parentAssistant = m2mla.getOppositeDtoAssistant(childAssistant);
							Long parentId = cmodel.getParentId();
							Long childId = childAssistant.getId(entity).getVal();
							dbService.insertManyToManyLink(parentAssistant, parentId, childAssistant, childId );
						}
					}

					modelRowRun.setEntityState(ManagedEntity.EntityState.UNMODIFIED);
					tableModel.touch();
				}
				break;
				case DELETED: {
					// repository.delete(entity);
					dbService.deleteDto(dtoAssistant, entity);
					tableModel.touch();
				}
				break;
				case UNMODIFIED: {
					// Do nothing
				}
				break;
			}
		}
		
		// Remove deleted elements from model
		int lastIdx = tableModel.getRows().size()-1;
		for (int idxRun = lastIdx; idxRun >=0; idxRun--) {
			var modelRowRun = tableModel.getRow(idxRun);
			if (modelRowRun.getEntityState() == ManagedEntity.EntityState.DELETED) {
				tableModel.removeRow(idxRun);
			}
		}
	}
	
	
	
	public static <E> MsgAjaxResponse deleteSelectedRows(HtmlPowerTable<E> table)
	{
		PowerTableModel<E> tableModel = table.getModel();
		DtoAssistant dtoAssistant = tableModel.getDtoAssistant();

		int lastIdx = tableModel.getRows().size()-1;
		for (int idxRun = lastIdx; idxRun >=0; idxRun--) {
			var modelRowRun = tableModel.getRow(idxRun);
			if (modelRowRun.isSelected()) {
				
				E entity = modelRowRun.getEntity();
				DbService dbService = StaticContextAccessor.getBean(DbService.class);
				dbService.deleteDto(dtoAssistant, entity);
				tableModel.removeRow(idxRun);
			}
		}
		
		return MsgAjaxResponse.createSuccessMsg();
	}

	
	protected static RepositorySafe getRepositorySafe() {
		return StaticContextAccessor.getBean(RepositorySafe.class);
	}
	
	
	
	public static <E> Class<E> getEntityType(PowerTableModel<E> tableModel) {
		Class<E> entityClass = tableModel.getDtoAssistant().getEntityClass();
		return entityClass;
	}
	
}
