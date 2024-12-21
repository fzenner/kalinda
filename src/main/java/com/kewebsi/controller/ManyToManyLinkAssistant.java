package com.kewebsi.controller;

import com.kewebsi.errorhandling.CodingErrorException;

import java.util.Objects;

public class ManyToManyLinkAssistant<A, B> implements LinkAssistant{

    protected DtoAssistant<A> dtoAssistentA;
    protected DtoAssistant<B> dtoAssistentB;

    // protected BiConsumer<A, B> linkAtoB;

    /**
     * The column name of the jointable which points to the table A and which has typically a foreign key constraint on it.
     */
    protected String fkColnameA;
    /**
     * The column name of the jointable which points to the table B and which has typically a foreign key constraint on it.
     */

    protected String fkColNameB;
    protected String manyToManyTableName;

//    public void link(A entityA, B entityB) {
//        entityA.addLinkTo(entityB);
//    }

//    public void link(A entityA, B entityB) {
//        linkAtoB.accept(entityA, entityB);
//    }


    public String getFkColNameA() {
        if (fkColnameA != null) {
            return fkColnameA;
        } else {
            return dtoAssistentA.getTableName() + "_id";
        }
    }


    public String getFkColNameB() {
        if (fkColnameA != null) {
            return fkColNameB;
        } else {
            return dtoAssistentB.getTableName() + "_id";
        }
    }

    public String getFkColNameFor(DtoAssistant dtoAssistant) {
        if (dtoAssistentA.hasSameTableNameAs(dtoAssistant)) {
            return getFkColNameA();
        }
        if (dtoAssistentB.hasSameTableNameAs(dtoAssistant)) {
            return getFkColNameB();
        }
        String errorMsg = String.format("Cannot find column name in table %s of foreign to table %s", manyToManyTableName, dtoAssistant.getTableName());
        throw new CodingErrorException(errorMsg);
    }

    public ManyToManyLinkAssistant(DtoAssistant<A> dtoAssistentA, DtoAssistant<B> dtoAssistantB, String manyToManyTableName) {
        this.dtoAssistentA = dtoAssistentA;
        this.dtoAssistentB = dtoAssistantB;
        this.manyToManyTableName = manyToManyTableName;
    }

    public ManyToManyLinkAssistant(DtoAssistant<A> dtoAssistentA, DtoAssistant<B> dtoAssistantB, String manyToManyTableName, String fkColnameA, String fkColNameB) {
        this.dtoAssistentA = dtoAssistentA;
        this.dtoAssistentB = dtoAssistantB;
        this.manyToManyTableName = manyToManyTableName;
        this.fkColnameA = fkColnameA;
        this.fkColNameB = fkColNameB;
        // this.linkAtoB = linkAtoB;

    }

    public DtoAssistant<?> getDtoAssistentA() {
        return dtoAssistentA;
    }

    public DtoAssistant<?> getDtoAssistantB() {
        return dtoAssistentB;
    }

    public DtoAssistant<?> getOppositeDtoAssistant(DtoAssistant oppositeTo) {
        if (dtoAssistentA.getTableName().equals(oppositeTo.getTableName())) {
            assert(!oppositeTo.getTableName().equals(dtoAssistentB.getTableName()));
            return dtoAssistentB;
        } else {
            assert(!oppositeTo.getTableName().equals(dtoAssistentA.getTableName()));
            return dtoAssistentA;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManyToManyLinkAssistant that = (ManyToManyLinkAssistant) o;
        return isSynonymous(that);
    }

    @Override
    public int hashCode() {
        String tableName1;
        String tableName2;
        String linkToTableName = dtoAssistentB.getTableName();
        String linkFromTableName = dtoAssistentA.getTableName();

        if (String.CASE_INSENSITIVE_ORDER.compare(dtoAssistentA.getTableName(), dtoAssistentB.getTableName()) > 0) {
            tableName1 = dtoAssistentB.getTableName();
            tableName2 = dtoAssistentA.getTableName();
        } else {
            tableName1 = dtoAssistentA.getTableName();
            tableName2 = dtoAssistentB.getTableName();
        }
        return Objects.hash(tableName1, tableName2, manyToManyTableName);
    }

    public boolean isSynonymous(ManyToManyLinkAssistant other) {

        if (other == this) {
            return true;
        }

        if (! this.manyToManyTableName.equals(other.getManyToManyTableName())) {
            return false;
        }

        String otherFromTableName = other.dtoAssistentA.getTableName();
        String otherToTableName = other.dtoAssistentB.getTableName();
        String thisFromTableName = dtoAssistentA.getTableName();
        String thisToTableName = dtoAssistentB.getTableName();

        if (otherFromTableName.equals(thisFromTableName)) {
            if (otherToTableName.equals(thisToTableName)) {
                if (other.manyToManyTableName.equals(manyToManyTableName)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        // There might be already an identical many to many relationship with switched tables.
        if (otherFromTableName.equals(thisToTableName)) {
            if (otherToTableName.equals(thisFromTableName)) {
                if (other.manyToManyTableName.equals(manyToManyTableName)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        // The table names of both LinkAssistants are disjunctive (differ completely, no match at all)
        return false;
    }


    public String getManyToManyTableName() {
        return manyToManyTableName;
    }
}
