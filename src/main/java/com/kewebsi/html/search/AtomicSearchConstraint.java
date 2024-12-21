package com.kewebsi.html.search;

import com.kewebsi.controller.FieldAssistant;
import com.kewebsi.controller.FieldAssistantEnum;
import com.kewebsi.controller.SimpleFieldAssistantEnum;
import com.kewebsi.html.PageState;
import com.kewebsi.service.PageStateVarColdLink;

/**
 * This class holds and manages the data fields for defining a single field constraint in the GUI for DB-like searches.
 * In its core it manages among other things two page variables (min and max).
 * @param <F>
 */
public class AtomicSearchConstraint<F> extends PageState {

    protected FieldAssistant<?,F> fieldAssistant;
    protected boolean constraintIsActive;
    protected ConstraintType constraintType;

    protected PageStateVarColdLink<F> equalOrMinVar;
    protected PageStateVarColdLink<F> maxVar;

    /**
     * @param fieldAssistant The field assistant of the field for which the search constraint is being build.
     * @param pageStateId
     */
    public AtomicSearchConstraint(FieldAssistant fieldAssistant, String pageStateId) {
        this.pageStateId = pageStateId;
        this.fieldAssistant = fieldAssistant;
        this.constraintIsActive = false;

        var fieldType = fieldAssistant.getFieldType();

        //
        // Set the default values for the constraintType
        //
        constraintType =
                switch (fieldType) {
                    case STR -> ConstraintType.LIKE;
                    case LONG -> ConstraintType.EQUALS;
                    case INT -> ConstraintType.EQUALS;
                    case BOOL -> ConstraintType.EQUALS;
                    case LOCALDATE -> ConstraintType.EQUALS;
                    case LOCALDATETIME -> ConstraintType.BETWEEN;
                    case LOCALTIME -> ConstraintType.BETWEEN;
                    case ENM -> ConstraintType.EQUALS;
                    case FLOAT -> ConstraintType.BETWEEN;

                };


        boolean maxValPossible = isMaxValPossible();

        if (fieldType == FieldAssistant.FieldType.ENM) {
            FieldAssistantEnum fae = (FieldAssistantEnum) fieldAssistant;
            SimpleFieldAssistantEnum<Enum> sfa = new SimpleFieldAssistantEnum(fae);
            sfa.setCanBeNull(false);  // When the field is active, it must not be null.
            equalOrMinVar =  PageStateVarColdLink.createPageStateVarColdLink(this, sfa);
        } else {
            equalOrMinVar = PageStateVarColdLink.createPageStateVarColdLink(this, fieldAssistant);
        }
        equalOrMinVar.setCheckRelevance(pageStateVarIntf -> {return isConstraintIsActive();});

        if (maxValPossible) {
            maxVar = PageStateVarColdLink.createPageStateVarColdLink(this, fieldAssistant, "max-");
            maxVar.setCheckRelevance(pageStateVarIntf -> {return (constraintType == ConstraintType.BETWEEN && isConstraintIsActive());});
        }

    }

    public boolean isMaxValPossible() {
        var fieldType = fieldAssistant.getFieldType();
        boolean maxValPossible =
                switch (fieldType) {
                    case STR, ENM -> false;
                    default -> true;
                };
        return maxValPossible;
    }


    public enum ConstraintType {BETWEEN, EQUALS, LIKE};

    public Enum<?> getFieldName() {
        return fieldAssistant.getFieldName();
    }


    public boolean isConstraintIsActive() {
        return constraintIsActive;
    }

    public void setConstraintIsActive(boolean constraintIsActive) {
        this.constraintIsActive = constraintIsActive;
//        if (!constraintIsActive) {   // CONTINUE HERE XXXXXXXXXXXXXXXXXXXXXXXXX check what to do in a fucntional way. calc error in HtmlPageVarField, in the PageVar or in the FieldAssistant
//            clearErrors();
//        }
    }

    public ConstraintType getConstraintType() {
        return constraintType;
    }

    public void setConstraintType(ConstraintType constraintType) {
        this.constraintType = constraintType;
    }


    public void setConstraintType(String constraintType) {
        this.constraintType = ConstraintType.valueOf(constraintType);
    }

    public void setConstraintTypeByEnumClearErrors(Enum<?> constraintType) {
        this.constraintType = (ConstraintType) constraintType;
        if (maxVar != null) {
            maxVar.clear();
        }
    }


    public FieldAssistant.FieldType getFieldType() {
        return fieldAssistant.getFieldType();
    }


    public FieldAssistant getFieldAssistant() {
        return fieldAssistant;
    }

    public void setFieldAssistant(FieldAssistant fieldAssistant) {
        this.fieldAssistant = fieldAssistant;
    }

    public PageStateVarColdLink getEqualOrMinVar() {
        return equalOrMinVar;
    }

    public PageStateVarColdLink getMaxVar() {
        return maxVar;
    }


}
