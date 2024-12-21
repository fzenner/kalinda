

package com.kewebsi.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.html.search.AtomicSearchConstraint;

import java.util.HashMap;

public class HtmlRadioButton2 extends HtmlTag implements RadioButtonClickHandler  {



    /**
     * The name of the radio button also denotes the variable that is set with this element.
     */
    // protected String name;
    protected RadioButtonGroup buttonGroup;
    protected Enum<?> valueIfSelected;
    protected AtomicSearchConstraint atomicSearchConstraint;

    protected boolean checkedOldValue = false;
    protected boolean disabledOldValue;



    public HtmlRadioButton2(
            String id,
            RadioButtonGroup buttonGroup,
            AtomicSearchConstraint atomicSearchConstraint,
            Enum<?> valueIfSelected) {
        this.id = id;
        this.buttonGroup = buttonGroup;
        this.atomicSearchConstraint = atomicSearchConstraint;
        buttonGroup.add(this);
        this.valueIfSelected = valueIfSelected;
        this.disabledOldValue = isDisabled();


    }

    @Override
    public GuiDef getGuiDef() {
        GuiDef guiDef = new GuiDef("kewebsi-radiobutton" , id);
        guiDef.cssClasses = this.getCssClasses();

        guiDef.addAttribute("name", buttonGroup.getName());

        if (!isEditable()) {
            guiDef.addAttribute("readonly", "true");
        }

        if (isChecked()) {
            guiDef.addAttribute("checked", "true");
        }

        if (isDisabled()) {
            guiDef.addAttribute("disabled", "true");
        }

        return guiDef;
    }


    public boolean isChecked() {

        var valInModel = atomicSearchConstraint.getConstraintType();
        if (valInModel.equals(valueIfSelected)) {
            return true;
        }
        return false;
    }

    public boolean isDisabled() {
        return ! atomicSearchConstraint.isConstraintIsActive();
    }



    public AtomicSearchConstraint getAtomicSearchConstraint() {
        return atomicSearchConstraint;
    }


    public boolean isEditable() {
        return atomicSearchConstraint.isConstraintIsActive();
    }


    public Enum<?> getValueIfSelected() {
        return valueIfSelected;
    }

    public void setValueIfSelected(Enum<?> valueIfSelected) {
        this.valueIfSelected = valueIfSelected;
    }

    public String getName() {
        return  buttonGroup.getName();
    }

    @Override
    public boolean isAttributesModified() {
        return isChecked() != checkedOldValue
                || isDisabled() != disabledOldValue;
    }

    @Override
    public void setAttributesNotModified() {
        checkedOldValue = isChecked();
        disabledOldValue = isDisabled();
    }


    @Override
    public HashMap<String, AttributeModification> getAttributeModifications() {

        if (isChecked() != checkedOldValue) {
            final String attributeName = "checked";
            HashMap<String, AttributeModification> result = new HashMap<>(1);
            AttributeModification mod;
            if (isChecked()) {
                mod = new AttributeModification(attributeName, AttributeModification.Modification.MODIFIED, "yes");
            } else {
                mod = new AttributeModification(attributeName, AttributeModification.Modification.MODIFIED, "null");
            }
            result.put(attributeName, mod);
            return result;
        }

        if (isDisabled() != disabledOldValue) {
            final String attributeName = "disabled";
            HashMap<String, AttributeModification> result = new HashMap<>(1);
            AttributeModification mod;
            if (isDisabled()) {
                mod = new AttributeModification(attributeName, AttributeModification.Modification.MODIFIED, "yes");
            } else {
                mod = new AttributeModification(attributeName, AttributeModification.Modification.REMOVED, "null");
            }
            result.put(attributeName, mod);
            return result;
        }



        return null;
    }




    @Override
    public boolean isContentOrGuiDefModified() {
        return false;
    }

    @Override
    public void setContentOrGuiDefNotModified() {

    }


    public RadioButtonGroup getButtonGroup() {
        return buttonGroup;
    }

    @Override
    public MsgAjaxResponse handleClick(boolean checked, JsonNode rootNode, UserSession userSession) {
//        MsgRadioButtonClicked msg = JsonUtils.parseJsonFromClient(rootNode, MsgRadioButtonClicked.class);
//        HtmlRadioButton2 radioButton = (HtmlRadioButton2) page.getElementById(msg.tagId);
//
//
        AtomicSearchConstraint asc = getAtomicSearchConstraint();
        asc.setConstraintTypeByEnumClearErrors(getValueIfSelected());
        // asc.setConstraintIsActive(true);


        getButtonGroup().doNotUpdate();

        //radioButton.getAtomicConstraintDiv().updateChildrenAccordingToConstraint();

        return MsgAjaxResponse.createSuccessMsg();


    }
}

