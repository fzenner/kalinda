package com.kewebsi.lab;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@NoRepositoryBean
@Transactional   // TODO REMOVE
public interface CustomRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
    @Transactional
    void refresh(T t);

    @Override
    @Transactional   // TODO REMOVE
    public T getById(ID id);


}