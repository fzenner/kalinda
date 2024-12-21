package com.kewebsi.html.search;

import com.kewebsi.controller.DtoAssistant;
import com.kewebsi.controller.FieldAssistant;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.html.PageState;
import com.kewebsi.html.table.MainTableModel;
import com.kewebsi.html.table.PowerTableModelImpl;

import java.util.ArrayList;

public class EntityNavigatorState<T> extends PageState  {

    protected DtoAssistant<T> dtoAssistant;
    // protected EntityEditorState entityEditorState;

    protected EntityDetailEditorState<T> entityDetailEditorState;
    protected ArrayList<AtomicSearchConstraint> atomicSearchConstraints;
    protected MainTableModel<T> tableModel;


    public EntityNavigatorState(DtoAssistant<T> dtoAssistant, String pageStateId) {
        initEntityDetailEditorState(dtoAssistant, pageStateId);
    }

//    public EntityNavigatorState(DtoAssistant<T> dtoAssistant) {
//        initEntityDetailEditorState(dtoAssistant, dtoAssistant.getSymbol());
//    }

    public void initEntityDetailEditorState(DtoAssistant<T> dtoAssistant, String pageStateId) {
        this.dtoAssistant = dtoAssistant;
        this.pageStateId = pageStateId;

//        entityDetailEditorState = new EntityDetailEditorState<T>(() -> {
//            return tableModel.getEntityForDetailDisplay();},
//            dtoAssistant);

        entityDetailEditorState = new EntityDetailEditorState<T>(tableModel, dtoAssistant, pageStateId + "-entitiydetaileditor");
    }


//    public EntityEditorState getEntityEditorState() {
//        if (entityEditorState == null) {
//            entityEditorState = new EntityEditorState(dtoAssistant, pageStateId + "entityeditor");
//        }
//        return entityEditorState;
//    }

    public  ArrayList<AtomicSearchConstraint> getAtomicSearchConstraints() {
        if (atomicSearchConstraints == null) {
            var fieldAssistants = dtoAssistant.getFieldAssistants();
            atomicSearchConstraints = new ArrayList<>(fieldAssistants.size());
            for (var  fieldAssist : fieldAssistants) {
                AtomicSearchConstraint sc = new AtomicSearchConstraint(fieldAssist, pageStateId + "-searchconstraints");
                atomicSearchConstraints.add(sc);
            }
        }
        return atomicSearchConstraints;
    }


    public AtomicSearchConstraint getAtomicSearchConstraint(FieldAssistant fieldAssistant) {  // TODO: Maybe equality on FieldAssistant is better.
        for (var run:  getAtomicSearchConstraints()) {
            if (run.getFieldName().equals(fieldAssistant.getFieldName())) {
                return run;
            }
        }
        throw new CodingErrorException("Could not find constraint for fieldName" + fieldAssistant.getFieldName());
    }


    public MainTableModel<T> getTableModel() {
        if (tableModel == null) {
            tableModel = new MainTableModel<>(dtoAssistant);
        }
        return tableModel;
    }


    public DtoAssistant<T> getDtoAssistant() {
        return dtoAssistant;
    }



}
