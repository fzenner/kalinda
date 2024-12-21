package com.kewebsi.html.table;

import com.kewebsi.controller.ManagedEntity;

@FunctionalInterface
public interface EntityForDetailDisplayProvider<T> {

    public ManagedEntity<T> getManagedEntityForDetailDisplay();



}
