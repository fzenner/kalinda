package com.fzenner.datademo.web.outmsg;

import com.kewebsi.errorhandling.ErrorInfo;
import com.kewebsi.html.HtmlTag;
import com.kewebsi.util.CommonUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class GuiDef {

    public String tag;
    public String id;

    /**
     * Null, if it is not an update. "true", when it is an update.
     */
    public enum UpdateMode {REPLACE, FULL, MODIFICATIONS_ONLY };


    /**
     * Is null when we define a new element. Not null, when we update.
     */
    public UpdateMode updateMode;

    public String text;
    public ArrayList<String> cssClasses;
    public LinkedHashMap<String, String> attrs;  // Attributes
    public Object state;
    public ErrorInfo errorInfo;
    public Object tagSpecificData;             // Special static data defining the given tag. E.g. placing info for child tags if this component contains nested html tags that are not provideds as child-GuiDefs.
    public ArrayList<GuiDef> children;
    public String clientEventHandler;
    public String customUpdateOperation;

    // public enum Visibility {NORMAL, INVISIBLE, COLLAPSED}
    public HtmlTag.Visibility visibility;


    /**
     * Can hold for example information under which element this element should be placed.
     */
    public String relatedHtmlElementId;

    /**
     * Possible values: Currently only "UNDER_LEFT_ALIGNED".
     */
    public String relatedPlacing;


    public GuiDef(String tag, String id) {
        this.tag = tag;
        this.id = id;
    }

    public GuiDef(String tag, String id, HtmlTag.Visibility visibility, ErrorInfo errorInfo) {
        this.tag = tag;
        this.id = id;
        this.visibility = visibility;
        this.errorInfo = errorInfo;
    }

    public void appendChild(GuiDef newChild) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(newChild);
    }


    public void appendChildren(List<HtmlTag> newChildren) {

        if (newChildren == null) {
            return;
        }

        if (newChildren.isEmpty()) {
            return;
        }

        if (children == null) {
            children = new ArrayList<>();
        }

        for (var newChild : newChildren) {
            children.add(newChild.getGuiDef());
        }
    }

    public void addAttribute(String key, String value) {
        if (attrs == null) {
            attrs = new LinkedHashMap<>();
        }
        attrs.put(key, value);
    }


    public void addCssClass(String cssClass) {
        assert(CommonUtils.hasInfo(cssClass));
        if (cssClasses == null) {
            cssClasses = new ArrayList<>(2);
        }
        cssClasses.add(cssClass);
    }

    public UpdateMode getUpdateMode() {
        return updateMode;
    }

    public void setUpdateMode(UpdateMode updateMode) {
        this.updateMode = updateMode;
    }

    public void setTagSpecificData(Object tagSpecificData) {
        this.tagSpecificData = tagSpecificData;
    }


}
