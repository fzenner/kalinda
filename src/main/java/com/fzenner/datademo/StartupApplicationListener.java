package com.fzenner.datademo;

import com.fzenner.datademo.entity.manytomanytables.DtoAssistantInitializer;
import com.fzenner.datademo.web.AjaxDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Initializations to be performed after all Spring Initializiations were done.
 */
@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

static final Logger LOG = LoggerFactory.getLogger(AjaxDispatcher.class);
    public static int counter;

    @Override public void onApplicationEvent(ContextRefreshedEvent event) {

        DtoAssistantInitializer.performInitialization();;
        LOG.info("Increment counter");
        counter++;
    }
}