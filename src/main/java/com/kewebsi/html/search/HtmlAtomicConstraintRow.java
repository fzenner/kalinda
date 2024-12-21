package com.kewebsi.html.search;

import com.kewebsi.controller.FieldAssistant;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.html.*;
import com.kewebsi.service.PageVarLocalDateTimeColdLink;
import com.kewebsi.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;

public class HtmlAtomicConstraintRow {


    public FieldAssistant fieldAssistant;

    protected AtomicSearchConstraint atomicSearchConstraint;
    public String localFieldIdPrefix;


    protected HtmlTag parentTag;   // TODO: Move into methods.
    protected HtmlSearchCheckbox searchCheckbox;
    protected HtmlLabel label;
    protected HtmlDiv radioButtonDiv;
    protected HtmlTag equalsOrMinField;
    protected HtmlTag maxField = null;

    private static Logger LOG = LoggerFactory.getLogger(HtmlAtomicConstraintRow.class);

    public HtmlAtomicConstraintRow(FieldAssistant fieldAssistant, AtomicSearchConstraint atomicSearchConstraint, HtmlTag parentTag) {
        String parentId = parentTag.getId();
        if (! CommonUtils.hasInfo(parentId)) {
            LOG.warn("Parent of HtmlAtomicConstraintRow has no id.");
        }
        this.parentTag = parentTag;
        this.fieldAssistant = fieldAssistant;
        this.atomicSearchConstraint = atomicSearchConstraint;
        this.localFieldIdPrefix = getLocalFieldIdPrefix(parentId, fieldAssistant);

        switch (fieldAssistant.getFieldType()) {

            case LONG -> createChildrenForIntConstraints();
            case INT -> createChildrenForIntConstraints();
            case STR -> createChildrenForStringConstraints();
            case LOCALDATETIME -> createChildrenForDateConstraints();
            case ENM -> createChildrenForEnumConstraints();
            default -> throw new CodingErrorException("Unhandled FieldType: " + fieldAssistant.getFieldType().name());
        }
    }

    public void createChildrenForIntConstraints() {

        //
        // Input Elements
        //
        searchCheckbox = new HtmlSearchCheckbox(getCheckBoxId(), atomicSearchConstraint, this);

        RadioButtonGroup radioButtonGroup = new RadioButtonGroup(getConstraintChoiceName());

        HtmlRadioButton2 radioButtonEquals = new HtmlRadioButton2(
                getConstraintChoiceEqualsId(),
                radioButtonGroup,
                atomicSearchConstraint,
                AtomicSearchConstraint.ConstraintType.EQUALS
        );

        HtmlRadioButton2 radioButtonBetween = new HtmlRadioButton2(
                getConstraintChoiceBetweenId(),
                radioButtonGroup,
                atomicSearchConstraint,
                AtomicSearchConstraint.ConstraintType.EQUALS.BETWEEN
        );


        equalsOrMinField = new HtmlPageVarField(atomicSearchConstraint.getEqualOrMinVar()) {
            @Override
            public boolean isDisabled() { return ! atomicSearchConstraint.isConstraintIsActive();
            }

            @Override
            public boolean isRequired() {
                return super.isRequired();
            }
        };

        maxField = new HtmlPageVarField(atomicSearchConstraint.getMaxVar()) {
            @Override
            public Visibility getVisibility() {
                if(atomicSearchConstraint.isMaxValPossible() && atomicSearchConstraint.getConstraintType() == AtomicSearchConstraint.ConstraintType.BETWEEN) {
                    return Visibility.VISIBILITY_VISIBLE;
                } else {
                    return Visibility.VISIBILITY_HIDDEN;
                }
            }

            @Override
            public boolean isDisabled() { return ! (atomicSearchConstraint.isConstraintIsActive() && isVisible());
            }

            @Override
            public boolean isRequired() {
                return super.isRequired();
            }
        };

        // var equalsOrMinFieldWrapped = new HtmlErrorWrapper(equalsOrMinField);
        // var maxFieldWrapped = new HtmlErrorWrapper(maxField);

        //
        // Labels and Layout
        //
        label = new HtmlLabel(fieldAssistant.getFieldLabel()).setForTag(searchCheckbox).addCssClass("nowrapLabel");
        radioButtonDiv = new HtmlDiv("divRadioButtons");
        HtmlLabel labelEquals = new HtmlLabel("equals").setForTag(radioButtonEquals);
        HtmlLabel labelBetween = new HtmlLabel("between").setForTag(radioButtonBetween);
        radioButtonDiv.addChildren(radioButtonEquals, labelEquals, radioButtonBetween, labelBetween);

        parentTag.addChildren(
                searchCheckbox,
                label,
                radioButtonDiv,
                equalsOrMinField,
                maxField);
    }



    public void createChildrenForStringConstraints() {
        //
        // IDs
        //
        // String searchStringFieldId  = localFieldIdPrefix + "lowbound";


        //
        // Input Elements
        //
        searchCheckbox = new HtmlSearchCheckbox(getCheckBoxId(), atomicSearchConstraint, this);
        equalsOrMinField = new HtmlPageVarField(atomicSearchConstraint.getEqualOrMinVar()) {
            @Override
            public boolean isDisabled() { return ! atomicSearchConstraint.isConstraintIsActive(); };
        };
        maxField = new HtmlDiv2() {
            @Override
            public HtmlTag.Visibility getVisibility() {
                if(atomicSearchConstraint.isMaxValPossible() && atomicSearchConstraint.getConstraintType() == AtomicSearchConstraint.ConstraintType.BETWEEN) {
                    return HtmlTag.Visibility.VISIBILITY_VISIBLE;
                } else {
                    return HtmlTag.Visibility.VISIBILITY_HIDDEN;
                }
            }
        };



        //
        // Labels and Layout
        //
        label = new HtmlLabel(fieldAssistant.getFieldLabel()).setForTag(searchCheckbox).addCssClass("nowrapLabel");

        parentTag.addChildren(
                searchCheckbox,
                label,
                new HtmlDiv2(),
                equalsOrMinField,
                maxField
                );

    }


    public void createChildrenForEnumConstraints() {
        //
        // IDs
        //
        String searchStringFieldId  = localFieldIdPrefix + "lowbound";

        //
        // Input Elements
        //
        searchCheckbox = new HtmlSearchCheckbox(getCheckBoxId(), atomicSearchConstraint, this);

        HtmlSelectV3 selectBox = new HtmlSelectV3(searchStringFieldId, (PageStateVarIntf<Enum>) atomicSearchConstraint.getEqualOrMinVar());


        equalsOrMinField = selectBox;
        // var equalsOrMinFieldWrapped = new HtmlErrorWrapper(equalsOrMinField);

        maxField = new HtmlDiv2() {
            @Override
            public HtmlTag.Visibility getVisibility() {
                if(atomicSearchConstraint.isMaxValPossible() && atomicSearchConstraint.getConstraintType() == AtomicSearchConstraint.ConstraintType.BETWEEN) {
                    return HtmlTag.Visibility.VISIBILITY_VISIBLE;
                } else {
                    return HtmlTag.Visibility.VISIBILITY_HIDDEN;
                }
            }
        };

        //
        // Labels and Layout
        //
        label = new HtmlLabel(fieldAssistant.getFieldLabel()).setForTag(searchCheckbox).addCssClass("nowrapLabel");

        parentTag.addChildren(
                searchCheckbox,
                label,
                new HtmlDiv2(),
                equalsOrMinField,
                maxField
        );

    }


    public void createChildrenForDateConstraints() {

        //
        // Input Elements
        //
        searchCheckbox = new HtmlSearchCheckbox(getCheckBoxId(), atomicSearchConstraint, this);

        RadioButtonGroup radioButtonGroup = new RadioButtonGroup(getConstraintChoiceName());

        HtmlRadioButton2 radioButtonEquals = new HtmlRadioButton2(
                getConstraintChoiceEqualsId(),
                radioButtonGroup,
                atomicSearchConstraint,
                AtomicSearchConstraint.ConstraintType.EQUALS
        );

        HtmlRadioButton2 radioButtonBetween = new HtmlRadioButton2(
                getConstraintChoiceBetweenId(),
                radioButtonGroup,
                atomicSearchConstraint,
                AtomicSearchConstraint.ConstraintType.EQUALS.BETWEEN
        );


        var minVarGen = atomicSearchConstraint.getEqualOrMinVar();
        var minVarDateTime = (PageVarLocalDateTimeColdLink) minVarGen;
        var equalsOrMinField = new HtmlPageVarFieldDateTime(minVarDateTime, this.localFieldIdPrefix + "-date") {
            @Override
            public boolean isDisabled() { return ! atomicSearchConstraint.isConstraintIsActive();
            }

            @Override
            public boolean isRequired() {
                return super.isRequired();
            }
        };




        equalsOrMinField.setDefaultTimeWhenDateIsSet(LocalTime.of(0,0,0));

        var maxVarGen = atomicSearchConstraint.getMaxVar();
        var maxVarDateTime =  (PageVarLocalDateTimeColdLink) maxVarGen;
        HtmlPageVarFieldDateTime maxField = new HtmlPageVarFieldDateTime(maxVarDateTime, this.localFieldIdPrefix + "-time") {
            @Override
            public HtmlTag.Visibility getVisibility() {
                if(atomicSearchConstraint.isMaxValPossible() && atomicSearchConstraint.getConstraintType() == AtomicSearchConstraint.ConstraintType.BETWEEN) {
                    return Visibility.VISIBILITY_VISIBLE;
                } else {
                    return Visibility.VISIBILITY_HIDDEN;
                }
            }

            @Override
            public boolean isDisabled() { return ! (atomicSearchConstraint.isConstraintIsActive() && isVisible());}

            @Override
            public boolean isRequired() {
                return super.isRequired();
            }


        };
        maxField.setDefaultTimeWhenDateIsSet(LocalTime.of(23,59,59));


        //
        // Labels and Layout
        //
        HtmlLabel label = new HtmlLabel(fieldAssistant.getFieldLabel()).setForTag(searchCheckbox).addCssClass("nowrapLabel");
        radioButtonDiv = new HtmlDiv("divRadioButtons");
        HtmlLabel labelEquals = new HtmlLabel("equals").setForTag(radioButtonEquals);
        HtmlLabel labelBetween = new HtmlLabel("between").setForTag(radioButtonBetween);
        radioButtonDiv.addChildren(radioButtonEquals, labelEquals, radioButtonBetween, labelBetween);

        parentTag.addChildren(
                searchCheckbox,
                label,
                radioButtonDiv,
                equalsOrMinField,
                maxField);
    }

    private boolean showMaxField() {
        if (atomicSearchConstraint.isMaxValPossible()) {
            if (atomicSearchConstraint.getConstraintType().equals(AtomicSearchConstraint.ConstraintType.EQUALS)) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Creates the string (without quotes) "parentId-fieldName-"
     * @param parentId
     * @param fieldAssistant
     * @return
     */
    public static String getLocalFieldIdPrefix(String parentId, FieldAssistant fieldAssistant) {
        return parentId + '-' + fieldAssistant.getFieldName().name() + '-';
    }

    public HtmlSearchCheckbox getSearchCheckbox() {
        return searchCheckbox;
    }

    public HtmlLabel getLabel() {
        return label;
    }

    public HtmlDiv getRadioButtonDiv() {
        return radioButtonDiv;
    }

    public HtmlTag getEqualsOrMinField() {
        return equalsOrMinField;
    }

    public HtmlTag getMaxField() {
        return maxField;
    }


    /*******************************************************
     * IDs
     *******************************************************/

    private String getConstraintChoiceBetweenId() {
        return localFieldIdPrefix + "constraintchoicebetween";
    }

    private String getConstraintChoiceEqualsId() {
        return localFieldIdPrefix + "constraintchoicequals";
    }

    private String getConstraintChoiceName() {
        return localFieldIdPrefix + "constraintchoice";
    }

//    private String getMaxFieldId() {
//        return localFieldIdPrefix + "highbound";
//    }
//
//    private String getEqualsOrMinFieldId() {
//        return localFieldIdPrefix + "lowbound";
//    }

    private String getCheckBoxId() {
        return localFieldIdPrefix + "searchactive";
    }


}
