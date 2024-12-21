package com.kewebsi.html;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fzenner.datademo.web.outmsg.EventHandlerDef;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.kewebsi.controller.GuiDelegate;
import com.kewebsi.util.CommonUtils;
import com.kewebsi.util.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class SmartTag extends HtmlTag {

    protected String text;
    protected String oldText;

    // protected String serverCallBackId;

    // protected ArrayList<EventHandlerDef> eventHandlerDefArrayList;

    protected String clientEventHandler;

    /**
     * All attributes except for id and class. It also does not contain the generic attributes wrapped into
     * one JSON.
     */
    protected HtmlAttributes nonStandardAttributes = new HtmlAttributes();  // TODO Make initially null for performance reasons.

    protected boolean eventHandlerClassSet = false;

    /**
     * Fixed attributes defined on the server side wrapped in on single JSON.
     */
    // protected HtmlComplexAttribute genericAttributes;


    protected HtmlAttributes dynamicAttributesOld;


    public HtmlTag putNonStandardAttribute(String key, String value) {
        addEventHandlerClass();
        nonStandardAttributes.put(key, value);
        return this;
    }

    protected void addEventHandlerClass() {
        if (! eventHandlerClassSet) {
            eventHandlerClassSet = true;
        }
    }

    public void addAttributes(String[] keyValues) {
        nonStandardAttributes.put(keyValues);
    }

    public void addAttribute(String key, String value) {
        nonStandardAttributes.put(key, value);
    }



    public abstract String getTag();

    public boolean hasClosingTag() {
        return true;
    }

    @Override
    public GuiDef getGuiDef() {
        GuiDef guiDef = new GuiDef(getTag(), getId());
        guiDef.text = text;
        guiDef.cssClasses = getCssClasses();
        guiDef.clientEventHandler = clientEventHandler;
        addChildrenGuiDefs(guiDef);
        return guiDef;
    }

    public HtmlAttributes getDynamicAttributes() {
        return null;
    }


    public boolean dynamicAttributesModified() {
        var dynamicAttributes = getDynamicAttributes();
        if (dynamicAttributes == null) {
            if (dynamicAttributesOld == null) {
                return false;
            } else {
                return true;
            }
        } else {
            if (dynamicAttributesOld == null) {
                return true;
            } else {
                if (dynamicAttributes.equals(dynamicAttributesOld)) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }


    @Override
    public boolean isAttributesModified() {
        return dynamicAttributesModified();
    }

    @Override
    public void setAttributesNotModified() {
        var currentAttributes = getDynamicAttributes();
        if (currentAttributes == null) {
            dynamicAttributesOld = null;
        } else {
            dynamicAttributesOld = new HtmlAttributes(getDynamicAttributes());
        }
    }

    @Override
    public HashMap<String, AttributeModification> getAttributeModifications() {
        HashMap<String, AttributeModification> result = null;
        var currentAttributes = getDynamicAttributes();
        var currentAttributesHashset = currentAttributes != null ? currentAttributes.keyValues : null;
        var oldAttributesHashset = dynamicAttributesOld != null ? dynamicAttributesOld.keyValues : null;
        if (oldAttributesHashset == null) {
            if (currentAttributesHashset != null) {
                for (var run : currentAttributesHashset.entrySet()) {
                    if (result == null) {
                        result = new HashMap<>();
                    }
                    result.put(run.getKey(), new AttributeModification(run.getKey(), AttributeModification.Modification.NEW, run.getValue()));
                }
            }
        } else {
            if (currentAttributesHashset == null) {
                for (var run : oldAttributesHashset.entrySet()) {
                    if (result == null) {
                        result = new HashMap<>();
                    }
                    result.put(run.getKey(), new AttributeModification(run.getKey(), AttributeModification.Modification.REMOVED, run.getValue()));
                }
            } else {
                // When here, both lists are non-null
                for (var oldRun : oldAttributesHashset.entrySet()) {
                    var key = oldRun.getKey();
                    if (currentAttributesHashset.containsKey(key)) {
                        var oldValue = oldRun.getValue();
                        var newValue = currentAttributesHashset.get(key);
                        if (!CommonUtils.equals(oldValue, newValue)) {
                            if (result == null) {
                                result = new HashMap<>();
                            }
                            result.put(key, new AttributeModification(key, AttributeModification.Modification.MODIFIED, newValue));
                        }
                    } else {
                        if (result == null) {
                            result = new HashMap<>();
                        }
                        result.put(key, new AttributeModification(key, AttributeModification.Modification.REMOVED, ""));
                    }
                }

                for (var currentRun : currentAttributesHashset.entrySet()) {
                    var key = currentRun.getKey();
                    if (oldAttributesHashset.containsKey(key)) {
                        var currentValue = currentRun.getValue();
                        var oldValue = oldAttributesHashset.get(key);
                        if (!CommonUtils.equals(currentValue, oldValue)) {
                            if (result == null) {
                                result = new HashMap<>();
                            }
                            result.put(key, new AttributeModification(key, AttributeModification.Modification.MODIFIED, oldValue));
                        }
                    } else {
                        if (result == null) {
                            result = new HashMap<>();
                        }
                        result.put(key, new AttributeModification(key, AttributeModification.Modification.NEW, ""));
                    }
                }
            }
        }
        return result;
    }

    public SmartTag txt(String text) {
        this.text = text;
        return this;
    }



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean isContentOrGuiDefModified() {
        if (CommonUtils.equals(getText(), oldText)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setContentOrGuiDefNotModified() {
        oldText = getText();
    }


    public void setClientEventHandler(String clientEventHandler) {
        this.clientEventHandler = clientEventHandler;
    }
}
