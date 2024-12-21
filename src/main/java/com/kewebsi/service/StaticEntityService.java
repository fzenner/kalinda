package com.kewebsi.service;

import com.fzenner.datademo.StaticContextAccessor;
import com.kewebsi.controller.DtoAssistant;

public class StaticEntityService {

    public static <T> Iterable<T> searchForEntities(DtoAssistant<T> dtoAssistant, String joinClause, String whereClause) {
        DbService dbService = StaticContextAccessor.getBean(DbService.class);
        return dbService.selectFromDb(dtoAssistant, joinClause, whereClause);
    }

    public static <T> T searchForEntity(DtoAssistant<T> dtoAssistant, Long id) {
        DbService dbService = StaticContextAccessor.getBean(DbService.class);
        return dbService.findById(dtoAssistant, id);
    }

}