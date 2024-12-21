package com.kewebsi.controller;

import com.fzenner.datademo.entity.taco.TacoAssistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.codec.CodecException;

import java.util.HashMap;

public class AssistantRegistrar {

    public static AssistantRegistrar globalInstance;  // Might be replaced by one instance per module in the future,

    private static Logger LOG = LoggerFactory.getLogger(AssistantRegistrar.class);

    protected HashMap<String, DtoAssistant> assistantMap = new HashMap<>();




    public void registerDtoAssistant(DtoAssistant dtoAssistant) {
        String key = dtoAssistant.getAssistantId();
        if (assistantMap.containsKey(key)) {
            throw new CodecException("Double registration of DtoAssistant with id: " + key);
        }
        assistantMap.put(key, dtoAssistant);

        LOG.info("Registered " + key + " on " + dtoAssistant.toString());
    }


    public DtoAssistant getAsstistant(String assistantId) {
        return assistantMap.get(assistantId);
    }


    public static AssistantRegistrar getInstance() {
        if (globalInstance == null) {
            globalInstance = new AssistantRegistrar();
            globalInstance.registerDtoAssistant(TacoAssistant.getGlobalInstance());
        }
        return globalInstance;
    }

}
