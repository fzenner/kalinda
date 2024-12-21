package com.kewebsi.html;

import com.fzenner.datademo.web.outmsg.GuiDef;

import java.util.ArrayList;

public interface HtmlElement {

    public String getId();

    public HtmlElement getParent();

    public ArrayList<HtmlElement> getChildren();

    public GuiDef getGuiDef();

    public void updateChildList(); // TODO: Check if necessary


    public HtmlElement getRoot();

    public AbstractHtmlPage getPage();

    public PageState getPageState();

    // TODO: Move to helper class
    public HtmlBody getBody();

    public void setParent(BasicHtmlParent parent);

    public boolean isNewChild();

    public void setNotModified();

    public boolean isContentOrGuiDefModified();

    public void setContentOrGuiDefNotModified();


    public String getTagName();

    // TODO: Review
    // public boolean isAttributesModified();

    // TODO: Move into subclass
    // public boolean isCssClassesModified();

    public boolean isVisibilityModified();


    public GuiDef getGuiDefUpdate();

    public enum Visibility {VISIBILITY_HIDDEN, VISIBILITY_VISIBLE, DISPLAY_NONE, DISPLAY_BLOCK}

    public Visibility getVisibility();

    public void setVisibility(Visibility visibility);

	public void setVisibilityNotModified();

	public boolean isVisible();


    // TODO: DELETE
    public enum UpdateProcedure {HTML, GUI_DEF};

    // TODO: DELETE
    public static final String APPLY_CENTRAL_EVENT_HANDLER_CSS_CLASS = "aceh";

    // TODO: REview
    public UpdateProcedure getUpdateProcedure();

    public HtmlElement getDescendent(String htmlId);

    public void remove();

}
