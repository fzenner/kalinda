package com.fzenner.datademo;

import com.fzenner.datademo.entity.ingredient.Ingredient;
import com.fzenner.datademo.entity.ingredient.IngredientAssistant;
import com.fzenner.datademo.entity.taco.Taco;
import com.fzenner.datademo.entity.taco.TacoAssistant;
import com.fzenner.datademo.service.RepositorySafe;
import com.kewebsi.controller.DtoAssistant;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.lab.CustomRepository;
import com.kewebsi.service.DbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;

@Component
public class StaticContextAccessor {

    private static ApplicationContext context;

    private static EntityManagerFactory entityManagerFactory;


    protected HashMap<Class, DtoAssistant> dtoRegister = new HashMap<>();


    @Autowired
    public StaticContextAccessor(ApplicationContext applicationContext, EntityManagerFactory entityManagerFactory) {
        context = applicationContext;
        this.entityManagerFactory = entityManagerFactory;
    }

    public static <T> T getBean(Class<T> clazz) {
        T bean = context.getBean(clazz);
        System.out.println("\nXXXXXXXXXXXXXXXXXXX Bean:" + bean);
        return bean;
    }


    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }


    protected static RepositorySafe getRepositorySafe() {
        return StaticContextAccessor.getBean(RepositorySafe.class);
    }



    public static DtoAssistant getDtoAssistantFor(Object dto) {
       if (dto instanceof Taco) {
           return TacoAssistant.getGlobalInstance();
       }

       if (dto instanceof Ingredient) {
           return IngredientAssistant.getGlobalInstance();
       }

       throw new CodingErrorException("Unexpected DTO");
    }


}