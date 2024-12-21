package com.kewebsi.lab;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.io.Serializable;

/**
 * The purpose of this class is to provide a DB interface that allows to read the entire entity from the DB
 * after saving the entity.
 * That is useful a) if the database creates values (e.g. last_updated), if another thread has made a DB modification
 * or - which is the reason why we introducedit -  if we update only some rows of the database by populating only
 * some members of an entity, then we want to have the complete filled entity after performing the update.
 * See https://stackoverflow.com/questions/45491551/refresh-and-fetch-an-entity-after-save-jpa-spring-data-hibernate
 * @param <T>
 * @param <ID>
 */
@Transactional
public class CustomRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
        implements CustomRepository<T, ID> {

    private final EntityManager entityManager;

    public CustomRepositoryImpl(JpaEntityInformation entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }


    @Override
    @Transactional
    public void refresh(T t) {
        entityManager.refresh(t);
    }
}