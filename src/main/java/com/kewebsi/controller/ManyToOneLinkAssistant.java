package com.kewebsi.controller;

import java.util.Objects;

public class ManyToOneLinkAssistant implements LinkAssistant {

    protected DtoAssistant<?> linkFrom;
    protected DtoAssistant<?> linkTo;

    protected FieldAssistant mappedBy;

    public ManyToOneLinkAssistant(DtoAssistant<?> linkFrom, DtoAssistant<?> linkTo, FieldAssistant mappedBy) {
        this.linkFrom = linkFrom;
        this.linkTo = linkTo;
        this.mappedBy = mappedBy;
        assert(linkFrom.getFieldAssistants().contains(mappedBy));
    }


    public DtoAssistant<?> getLinkFrom() {
        return linkFrom;
    }

    public DtoAssistant<?> getLinkTo() {
        return linkTo;
    }

    public DtoAssistant<?> getOppositeDtoAssistant(DtoAssistant oppositeTo) {
        if (linkFrom.getTableName().equals(oppositeTo.getTableName())) {
            assert(!oppositeTo.getTableName().equals(linkTo.getTableName()));
            return linkTo;
        } else {
            assert(!oppositeTo.getTableName().equals(linkFrom.getTableName()));
            return linkFrom;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManyToOneLinkAssistant that = (ManyToOneLinkAssistant) o;
        return isSynonymous(that);
    }

    @Override
    public int hashCode() {
        String tableName1;
        String tableName2;

        tableName1 = linkFrom.getTableName();
        tableName2 = linkTo.getTableName();
        return Objects.hash(tableName1, tableName2, mappedBy);
    }

    public boolean isSynonymous(ManyToOneLinkAssistant other) {

        if (other == this) {
            return true;
        }

        if (!other.linkFrom.equals(linkFrom)) {
            return false;
        }

        if (!other.linkTo.equals(linkTo)) {
            return false;
        }

        if (!other.mappedBy.equals(mappedBy)) {
            return false;
        }

        return true;
    }

    public FieldAssistant getMappedBy() {
        return mappedBy;
    }


}
