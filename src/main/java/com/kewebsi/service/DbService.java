package com.kewebsi.service;

import com.kewebsi.controller.*;
import com.kewebsi.errorhandling.BackendDataException;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.util.DebugUtils;
// import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.BasicTypeReference;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class DbService {

    @Autowired EntityManager entityManagerAutoWired;

    @Transactional
    public <T> void persistDto(DtoAssistant<T> dtoAssistant, T dto) {
        Query query;
        EntityManager em = entityManagerAutoWired;
        BaseValLong id = dtoAssistant.getId(dto);
        if (id == null || id.getVal() <= 0 ) {
            // Element is new. Insert it
            query = createInsertQuery(em, dtoAssistant, dto);

        } else {
            query = DbService.createUpdateQuery(em, dtoAssistant, dto);
        }
        int updateCount = query.executeUpdate();   // TODO Verify expected result
    }


    public <T> void persistManagedEntity(DtoAssistant<T> dtoAssistant, ManagedEntity<T> managedEntity) {
        Query query;
        EntityManager em = entityManagerAutoWired;
        var dto = managedEntity.getEntity();
        BaseValLong id = dtoAssistant.getId(dto);
        if (id == null || id.getVal() <= 0 ) {
            // Element is new. Insert it
            query = createInsertQuery(em, dtoAssistant, managedEntity);

        } else {
            query = DbService.createUpdateQuery(em, dtoAssistant, dto);
        }
        int updateCount = query.executeUpdate();   // TODO Verify expected result
    }



    @Transactional
    public <T> void deleteDto(DtoAssistant<T> dtoAssistant, T dto) {

        EntityManager em = entityManagerAutoWired;;

        var m2mlaCollection = dtoAssistant.getManyToManyLinkAssistants();
        if (m2mlaCollection != null) {

            for (var m2mla : m2mlaCollection) {
                String joinTableName = m2mla.getManyToManyTableName();
                String thisFkColName = m2mla.getFkColNameFor(dtoAssistant);
                String idStr = String.valueOf(dtoAssistant.getId(dto).getVal());

                String deleteLinksStr = "delete from " + joinTableName + " where " + thisFkColName + " = " + idStr;

                Query deleteLinksQuery = em.createNativeQuery(deleteLinksStr);
                int linkDelCount = deleteLinksQuery.executeUpdate();   // TODO Verify expected result
            }
        }

        String queryStr = dtoAssistant.createDeleteQuery(dto);
        Query query = em.createNativeQuery(queryStr);
        int updateCount = query.executeUpdate();   // TODO Verify expected result


    }


    public <T, U> void persistManyToManyLink(DtoAssistant<T> dtoAssistantA, T dtoA, DtoAssistant<U> dtoAssistantB, U dtoB) {
        ManyToManyLinkAssistant m2mla = dtoAssistantA.getManyToManyLinkAssistantTo(dtoAssistantB);

        Long dtoIdA = dtoAssistantA.getId(dtoA).getVal();
        Long dtoIdB = dtoAssistantB.getId(dtoB).getVal();

        if (exists(dtoAssistantA, dtoIdA, dtoAssistantB, dtoIdB)) {
            // Do nothing.
        } else {
            // Insert it. Updates are never required. Deletion of links must be handled by the service/business logic.
            insertManyToManyLink(dtoAssistantA, dtoIdA, dtoAssistantB, dtoIdB);
        }
    }

    @Transactional
    public <T, U> boolean exists(DtoAssistant<T> dtoAssistantA, Long dtoIdA, DtoAssistant<U> dtoAssistantB, Long dtoIdB) {
        ManyToManyLinkAssistant m2mla = dtoAssistantA.getManyToManyLinkAssistantTo(dtoAssistantB);
        String joinTableName = m2mla.getManyToManyTableName();
        String fkColNameA = m2mla.getFkColNameFor(dtoAssistantA);
        String fkColNameB = m2mla.getFkColNameFor(dtoAssistantB);

        String queryStr = "select count(*) " + joinTableName + " where " + fkColNameA + " = " + dtoIdA
                + " and " + fkColNameB + " = " + dtoIdB;

        EntityManager em = entityManagerAutoWired;
        Query query = em.createQuery(queryStr);
        Long dbCount = (Long) query.getSingleResult();

        if (dbCount.longValue() == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Transactional
    public <T, U> void insertManyToManyLink(DtoAssistant<T> dtoAssistantA, Long dtoIdA, DtoAssistant<U> dtoAssistantB, Long dtoIdB) {
        ManyToManyLinkAssistant m2mla = dtoAssistantA.getManyToManyLinkAssistantTo(dtoAssistantB);
        String joinTableName = m2mla.getManyToManyTableName();
        String fkColNameA = m2mla.getFkColNameFor(dtoAssistantA);
        String fkColNameB = m2mla.getFkColNameFor(dtoAssistantB);

        String queryStr = "insert into " + joinTableName + "(" + fkColNameA + ", " + fkColNameB
                + ") values (" + dtoIdA + ", " + dtoIdB + ")";

        EntityManager em = entityManagerAutoWired;;
        Query query = em.createNativeQuery(queryStr);
        int updateCount = query.executeUpdate();

    }


    @Transactional
    public <T, U> void deleteManyToManyLink(DtoAssistant<T> dtoAssistantA, Long dtoIdA, DtoAssistant<U> dtoAssistantB, Long dtoIdB) {
        ManyToManyLinkAssistant m2mla = dtoAssistantA.getManyToManyLinkAssistantTo(dtoAssistantB);
        String joinTableName = m2mla.getManyToManyTableName();
        String fkColNameA = m2mla.getFkColNameFor(dtoAssistantA);
        String fkColNameB = m2mla.getFkColNameFor(dtoAssistantB);

        String queryStr = "delete from " + joinTableName + "(" + fkColNameA + ", " + fkColNameB
                + ") values (" + dtoIdA + ", " + dtoIdB + ")";

        EntityManager em = entityManagerAutoWired;;
        Query query = em.createNativeQuery(queryStr);
        int updateCount = query.executeUpdate();

    }


    public static Long getNextCustomId(EntityManager em) {
        String queryStr = "select next value for kewebsiids";
        // EntityManager em = StaticContextAccessor.getEntityManager();
        Query query = em.createNativeQuery(queryStr);
//        BigInteger result = (BigInteger) query.getSingleResult();
//        return result.longValue();

        Long result = (Long) query.getSingleResult();
        return result.longValue();

    }

    public static <T> Query createInsertQuery(EntityManager em, DtoAssistant<T> dtoAssistant, ManagedEntity<T> managedEntity) {
        return createInsertQueryCore(em, dtoAssistant, null, managedEntity);
    }

    public static <T> Query createInsertQuery(EntityManager em, DtoAssistant<T> dtoAssistant, T dto) {
        return createInsertQueryCore(em, dtoAssistant, dto, null);
    }

    /**
     * Either dto or managedEntity must be non null. One of them must be non-null.
     * This looks slightly odd. If the dto is managed, that is, if there is a managed entity, some additional
     * things need to happen that are too tightly coupled to factor them out in separate methods.
     * @param em
     * @param dtoAssistant
     * @param dto
     * @param managedEntity
     * @return
     * @param <T>
     */
    protected static <T> Query createInsertQueryCore(EntityManager em, DtoAssistant<T> dtoAssistant, T dto, ManagedEntity<T> managedEntity) {

        if (DebugUtils.DEBUG_CHECKS_ON) {
            if (dto != null) {
                assert (managedEntity == null);
                assert  (dtoAssistant.getId(dto) == null);
            } else {
                assert (managedEntity != null);
                assert (dtoAssistant.getId(managedEntity.getEntity()) == null);
            }
        }

        if (managedEntity != null) {
            dto = managedEntity.getEntity();
        }

        Long id = getNextCustomId(em);
        if (managedEntity != null) {
            dtoAssistant.setId(id, managedEntity);
        } else {
            dtoAssistant.setId(id, dto);
        }

        String firstPartOfQuery = "insert into " + dtoAssistant.getTableName() ;

        String columnList = " (";
        String valueList = " (";

        boolean isFistElement = true;
        for (FieldAssistant faRun : dtoAssistant.getFieldAssistants()) {
            Enum fieldName = faRun.getFieldName();
            BaseVal val = dtoAssistant.getValueAsBaseType(dto, fieldName);
            String colName = faRun.getDbColName();

            if (! isFistElement) {
                columnList += ", ";
                valueList += ", ";
            }

            columnList += colName;
            valueList += ":" + colName;// val.toDbFormat();
            isFistElement = false;
        }

        columnList += ")";
        valueList += ")";

        String queryStr = firstPartOfQuery + columnList + " values " + valueList;


        // EntityManager em = StaticContextAccessor.getEntityManager();
        Query query = em.createNativeQuery(queryStr);

        for (FieldAssistant faRun : dtoAssistant.getFieldAssistants()) {
            FieldAssistant.FieldType fieldType = faRun.getFieldType();
            Enum fieldName = faRun.getFieldName();
            BaseVal val;
            if (isCreationTimeStamp(faRun)) {
                LocalDateTime now = LocalDateTime.now();
                if (managedEntity != null) {
                    dtoAssistant.setUnconvertedValueFromBackend(fieldName, now, managedEntity);
                } else {
                    dtoAssistant.setUnconvertedValueFromBackend(fieldName, now, dto);
                }
                val = BaseVal.of(now);
            } else {
                val = dtoAssistant.getValueAsBaseType(dto, fieldName);
            }
            String colName = faRun.getDbColName();
            setQueryParam(query, colName, fieldType, val);
        }
        return query;
    }

    public static boolean isCreationTimeStamp(FieldAssistant fa) {
        if (fa.getFieldType() == FieldAssistant.FieldType.LOCALDATETIME) {
            FieldAssistantLocalDateTime faldt = (FieldAssistantLocalDateTime) fa;
            if (faldt.isCreationTimeStamp()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public static <T> Query createUpdateQuery(EntityManager em, DtoAssistant dtoAssistant, T dto) {

        String s = "update " + dtoAssistant.getTableName() + " set ";

        boolean isFistElement = true;

        Collection<FieldAssistant> fieldAssistants = dtoAssistant.getFieldAssistants();
        Long id = dtoAssistant.getId(dto).getVal();

        ArrayList<FieldAssistant> fieldsToBind = new ArrayList<>(fieldAssistants.size());

        for (FieldAssistant faRun : fieldAssistants) {
            //
            // We never overwrite the creationTimeStamp
            //
            if (! isCreationTimeStamp(faRun)) {
                fieldsToBind.add(faRun);
            }

            //
            // We do not assign the id (which is in the where clause) and the createTimeStamp
            //
            if (! faRun.isKey() && ! isCreationTimeStamp(faRun)) {
                String colName = faRun.getDbColName();

                if (!isFistElement) {
                    s += ", ";
                }

                s += colName + " = :" + colName;
                isFistElement = false;
            }
        }

        if (isFistElement) {
            throw new CodingErrorException("DTO has no fields except for key.");
        }
        String idColname = dtoAssistant.getIdAssistant().getDbColName();
        s+= " where " + idColname + " = :" + idColname;

        // EntityManager em = StaticContextAccessor.getEntityManager();
        Query query = em.createNativeQuery(s);

        for (FieldAssistant faRun : fieldsToBind) {
            FieldAssistant.FieldType fieldType = faRun.getFieldType();
            Enum fieldName = faRun.getFieldName();
            BaseVal val = dtoAssistant.getValueAsBaseType(dto, fieldName);
            String colName = faRun.getDbColName();
            setQueryParam(query, colName, fieldType, val);
        }

        return query;
    }

    /**
     * We pass in the type togehter with the BaseVal, since we need to know it even if the val is null.
     */
    private static void setQueryParam(Query query, String colName, FieldAssistant.FieldType fieldType, BaseVal val) {

        if (val != null) {
                assert(val.getType() == fieldType);
        }

        BasicTypeReference hibernateType =
                switch (fieldType) {
                    case STR -> StandardBasicTypes.STRING;
                    case LONG -> StandardBasicTypes.LONG;
                    case INT -> StandardBasicTypes.INTEGER;
                    case ENM -> StandardBasicTypes.STRING;
                    case BOOL -> StandardBasicTypes.BOOLEAN;
                    case LOCALDATE -> StandardBasicTypes.DATE;
                    case LOCALTIME -> StandardBasicTypes.TIME;
                    case LOCALDATETIME -> StandardBasicTypes.TIMESTAMP;
                    case FLOAT -> StandardBasicTypes.FLOAT;
                };

        Object coreVal;
        if (val == null) {
            coreVal = null;
        } else {
            if (fieldType == FieldAssistant.FieldType.LOCALDATE) {
                BaseValLocalDate baseValLocalDate = (BaseValLocalDate) val;
                LocalDate localDate = baseValLocalDate.getVal();
                coreVal = java.sql.Date.valueOf(localDate);
            } else if (fieldType == FieldAssistant.FieldType.LOCALDATETIME) {
                BaseValLocalDateTime baseValLocalDate = (BaseValLocalDateTime) val;
                LocalDateTime localDateTime = baseValLocalDate.getVal();
                coreVal = java.sql.Timestamp.valueOf(localDateTime);
            } else if (fieldType == FieldAssistant.FieldType.ENM) {
                BaseValEnm baseValLocalDate = (BaseValEnm) val;
                Enum enm = baseValLocalDate.getVal();
                coreVal = enm.name();
            } else {
                coreVal = val.getVal();
            }
        }

        query.setParameter(colName,new TypedParameterValue(hibernateType, coreVal));
    }


    public <T> ArrayList<T> selectFromDb(DtoAssistant<T> dtoAssistant, String joinClause, String whereClause) {
        String queryStr = dtoAssistant.createSelectQuery(joinClause, whereClause);
        // EntityManager em = StaticContextAccessor.getEntityManager();
        EntityManager em = entityManagerAutoWired;
        Query query = em.createNativeQuery(queryStr);

        Collection<FieldAssistant> fieldAssistantsCollection = dtoAssistant.getFieldAssistants();
        int fieldCount = fieldAssistantsCollection.size();
        FieldAssistant[] faArray = fieldAssistantsCollection.toArray(new FieldAssistant[fieldCount]);

        List<Object[]> dbResultList = query.getResultList();
        ArrayList<T> dtoResultList = new ArrayList<T>(dbResultList.size());
        for (Object[] run: dbResultList) {
            T dto = dtoAssistant.createEntity();
            for (int i = 0; i< run.length; i++ ) {
                FieldAssistant fa = faArray[i];
                dtoAssistant.setUnconvertedValueFromBackend(fa.getFieldName(), run[i], dto);
            }
            dtoResultList.add(dto);
        }
        return dtoResultList;
    }

    public <T> T findById(DtoAssistant<T> dtoAssistant, Long id) {

        String idColName = dtoAssistant.getIdAssistant().getDbColName();
        String whereClause = idColName + " = " + id;

        ArrayList<T> resultList = selectFromDb(dtoAssistant, null, whereClause);
        int resultCount = resultList.size();
        if (resultCount > 1) {
            throw new BackendDataException("More than one search result ( " + resultCount + " elements found) executing a search via id.");
        }

        if (resultCount == 0) {
            return null;
        } else {
            return resultList.get(0);
        }
    }
}
