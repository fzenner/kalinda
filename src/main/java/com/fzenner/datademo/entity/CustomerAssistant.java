package com.fzenner.datademo.entity;

import com.kewebsi.controller.DtoAssistant;
import com.kewebsi.controller.FieldAssistant;
import com.kewebsi.controller.FieldAssistantLong;
import com.kewebsi.controller.FieldAssistantStr;

public class CustomerAssistant extends DtoAssistant<Customer> {


    public enum CustomerFields {
        customerId,
        title,
        fistName,
        lastName,
    };


    public void init() {
        // fieldAssistants = new LinkedHashMap<>(CustomerFields.values().length);

        //
        // id
        //
        {
            FieldAssistantLong<Customer> idAssist =  FieldAssistant.longId(CustomerFields.customerId);
            idAssist.setFieldLabel("Customer ID");
            idAssist.setDefaultWidthInChar(10);
            idAssist.setDbColName("id");
            idAssist.setGetter(entity -> entity.getId());
            idAssist.setSetter((entity, val) -> entity.setId(val));
            addFieldAssistant(idAssist);
        }

        //
        // title
        //
        {
            FieldAssistantStr<Customer> titleAssist = FieldAssistant.str(CustomerFields.title);
            titleAssist.setFieldLabel("Title");
            titleAssist.setDbColName("title");
            titleAssist.setDefaultWidthInChar(3);
            titleAssist.setGetter(entity -> entity.getTitle());
            titleAssist.setSetter((entity, val) -> entity.setTitle(val));
            addFieldAssistant(titleAssist);
        }

        //
        // firstName
        //
        {
            FieldAssistantStr<Customer> firstNameAssist = FieldAssistant.str(CustomerFields.fistName);
            firstNameAssist.setFieldLabel("First Name");
            firstNameAssist.setDbColName("firstname");
            firstNameAssist.setDefaultWidthInChar(20);
            firstNameAssist.setGetter(entity -> entity.getFirstName());
            firstNameAssist.setSetter((entity, val) -> entity.setFirstName(val));
            addFieldAssistant(firstNameAssist);
        }

        //
        // lastName
        //
        {
            FieldAssistantStr<Customer> lastNameAssist = FieldAssistant.str(CustomerFields.lastName);
            lastNameAssist.setFieldLabel("Last Name");
            lastNameAssist.setDbColName("lastname");
            lastNameAssist.setDefaultWidthInChar(20);
            lastNameAssist.setGetter(entity -> entity.getLastName());
            lastNameAssist.setSetter((entity, val) -> entity.setLastName(val));
            addFieldAssistant(lastNameAssist);
        }
        this.selfCheck();

    }

    public Class getEnumClass2() {
        return CustomerFields.class;
    }


    public Enum[] getFieldNames2() {
        return (Enum[]) getEnumClass().getEnumConstants();
    }

//    public void test() {
//        var es =EnumSet.allOf(CustomerFields.class);
//        var es2 =EnumSet.allOf(getEnumClass2());
//
//        var ecs = CustomerFields.class.getEnumConstants();
//        var ecs2 = getEnumClass2().getEnumConstants();
//
//        var esa = es.toArray();
//    }
//
////    @Override
////    public Enum<?>[] getFieldNames() {
////        return CustomerFields.values();
////    }

    @Override
    public Class<?> getEnumClass() {
        return CustomerFields.class;
    }

    @Override
    public Class<Customer> getEntityClass() {
        return Customer.class;
    }

    @Override
    public String getTableName() {
        return "customer";
    }

    protected static CustomerAssistant globalInstance;
    public static CustomerAssistant getGlobalInstance() {
        if (globalInstance == null) {
            globalInstance = new CustomerAssistant();
            globalInstance.init();
        }
        return globalInstance;
    }

}
