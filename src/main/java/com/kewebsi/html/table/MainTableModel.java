package com.kewebsi.html.table;

import com.kewebsi.controller.DtoAssistant;

/**
 * A table which is not linked to a parent entity.
 */
public class MainTableModel<T> extends PowerTableModelImpl<T>{


    public MainTableModel(Iterable<T> entity, DtoAssistant<T> dtoConstraints) {
        initPowerTableModelImpl(entity, dtoConstraints);
    }

    public MainTableModel(DtoAssistant<T> dtoConstraints) {
        initPowerTableModelImpl(null, dtoConstraints);
    }


    public void initPowerTableModelImpl(DtoAssistant<T> dtoConstraints) {
        initPowerTableModelImpl(null, dtoConstraints);
    }

    public void initPowerTableModelImpl(Iterable<T> dataList, DtoAssistant<T> dtoConstraints) {

        setData(dataList);
        this.dtoAssistant = dtoConstraints;

    }

    @Override
    public void setData(Iterable<T> dataList) {
        super.setData(dataList);
    }
}
