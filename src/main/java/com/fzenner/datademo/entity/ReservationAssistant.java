package com.fzenner.datademo.entity;

import com.kewebsi.controller.*;

import java.lang.reflect.InvocationTargetException;

public class ReservationAssistant extends DtoAssistant<Reservation> {

    public enum ReservationFields {
        reservationId,
        table,
        dateTime,
    }

    public void init() {
        //
        // id
        //
        {
            FieldAssistantLong<Reservation> idAssist =  FieldAssistant.longId(ReservationFields.reservationId);
            idAssist.setFieldLabel("Reservation ID");
            idAssist.setDefaultWidthInChar(10);
            idAssist.setDbColName("id");
            idAssist.setGetter(entity -> entity.getId());
            idAssist.setSetter((entity, val) -> entity.setId(val));
            addFieldAssistant(idAssist);
        }

        //
        // table
        //
        {
            FieldAssistantInt<Reservation> tableAssist = FieldAssistant.intType(ReservationFields.table);
            tableAssist.setFieldLabel("Table");
            tableAssist.setDbColName("res_table");
            tableAssist.setCanBeNull(true);
            tableAssist.setDefaultWidthInChar(10);
            tableAssist.setGetter(entity -> entity.getTableNr());
            tableAssist.setSetter((entity, val) -> entity.setTableNr(val));
            addFieldAssistant(tableAssist);
        }

        //
        //
        // dateTime
        {
            FieldAssistantLocalDateTime<Reservation> dateTime = FieldAssistant.dateTime(ReservationFields.dateTime);
            dateTime.setFieldLabel("Date");
            dateTime.setDbColName("res_datetime");
            dateTime.setDefaultWidthInChar(16);
            dateTime.setGetter(entity -> entity.getDateTime());
            dateTime.setSetter((entity, val) -> entity.setDateTime(val));
            dateTime.setCanBeNull(false);
            addFieldAssistant(dateTime);
        }

    }


    /**
     * Boilerplate from here.
     */


    @Override
    public Class<?> getEnumClass() {
        return ReservationFields.class;
    }

    @Override
    public Class<Reservation> getEntityClass() {
        return Reservation.class;
    }

    @Override
    public String getTableName() {
        return "reservation";
    }


    protected static ReservationAssistant globalInstance;
    public static ReservationAssistant getGlobalInstance() {
        if (globalInstance == null) {
            globalInstance = new ReservationAssistant();
            globalInstance.init();
        }
        return globalInstance;
    }

    public void getGlobalInstance(Class assistanceClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        assistanceClass.getDeclaredConstructor().newInstance();
    }

}
