package com.kewebsi.html.search;

public interface EntityProvider<T> {

    public Long getEntityId();
    /**
     If the entity is known, we return the entity. Otherwise you have to make due with the ID.
     */
    public T getEntity();

}
