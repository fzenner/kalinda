package com.kewebsi.html.table;


import com.kewebsi.controller.ManagedEntity;

/**
 * Page variables that expose variables of managed entities can handle the change of the underlying entity.
 * There are use cases, where that
 */
public class FixedManagedEntityProvider implements EntityForDetailDisplayProvider {
    @Override
    public ManagedEntity getManagedEntityForDetailDisplay() {
        return null;
    }
}
