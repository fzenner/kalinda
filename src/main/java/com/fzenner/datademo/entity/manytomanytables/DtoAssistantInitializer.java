package com.fzenner.datademo.entity.manytomanytables;

import com.fzenner.datademo.entity.TableNames;
import com.fzenner.datademo.entity.ingredient.IngredientAssistant;
import com.fzenner.datademo.entity.taco.TacoAssistant;
import com.kewebsi.controller.DtoAssistant;
import com.kewebsi.controller.ManyToManyLinkAssistant;

import java.util.ArrayList;

public class DtoAssistantInitializer {


    protected static ArrayList<ManyToManyLinkAssistant> m2mLinkAssistants;

    public static void performInitialization() {
        m2mLinkAssistants = new ArrayList<>();

        TacoAssistant       tacoAssistant       = TacoAssistant.getGlobalInstance();
        IngredientAssistant ingredientAssistant = IngredientAssistant.getGlobalInstance();

        var la = createManyToManyLinkAssistant(tacoAssistant, ingredientAssistant, TableNames.Taco_Ingredients, "taco_id", "ingredients_id");
        m2mLinkAssistants.add(la);
    }


    public static <A, B> ManyToManyLinkAssistant<A, B> createManyToManyLinkAssistant(DtoAssistant<A> dtoA, DtoAssistant<B> dtoB, String manyToManyTableName, String fkColNameDtoA, String fkColNameDtoB) {
        ManyToManyLinkAssistant la = new ManyToManyLinkAssistant(dtoA, dtoB, manyToManyTableName, fkColNameDtoA, fkColNameDtoB);
        dtoA.addLinkAssistant(la);
        dtoB.addLinkAssistant(la);
        return la;
    }


}
