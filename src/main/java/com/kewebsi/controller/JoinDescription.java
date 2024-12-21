package com.kewebsi.controller;

public class JoinDescription {

    enum JoinType {ONE_TO_MANY, MANY_TO_ONE, MANY_TO_MANY};

    JoinType joinType;


    DtoAssistant left;
    DtoAssistant right;

    String joinTableName;

    public JoinDescription(JoinType joinType, DtoAssistant left, DtoAssistant right, String joinTableName) {
        this.joinType = joinType;
        this.left = left;
        this.right = right;
        this.joinTableName = joinTableName;
    }

    public JoinType getJoinType(DtoAssistant perspective) {

        JoinType result;
        if (joinType == JoinType.MANY_TO_MANY) {
            result = JoinType.MANY_TO_MANY;
        } else {
            if (perspective.getSymbol().equals(left.getSymbol())) {
                result = joinType;
            } else {
                if (joinType == JoinType.ONE_TO_MANY) {
                    result = JoinType.MANY_TO_ONE;
                } else {
                    result = JoinType.ONE_TO_MANY;
                }
            }
        }
        return result;
    }

    public DtoAssistant getLeft() {
        return left;
    }

    public DtoAssistant getRight() {
        return right;
    }

    public String getJoinTableName() {
        return joinTableName;
    }
}
