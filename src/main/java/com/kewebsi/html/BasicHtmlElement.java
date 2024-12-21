package com.kewebsi.html;

import com.fzenner.datademo.web.outmsg.GuiDef;
import com.kewebsi.errorhandling.CodingErrorException;

import java.util.ArrayList;

public abstract class BasicHtmlElement implements HtmlElement {

    protected String id = null;

    private BasicHtmlParent parent;

    protected Visibility visibility;
    protected Visibility visibilityOld;


    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setParent(BasicHtmlParent parent) {
        this.parent = parent;
    }

    @Override
    public BasicHtmlParent getParent() {
        return null;
    }

    @Override
    public boolean isNewChild() {
		BasicHtmlParent parent = getParent();
		if (parent == null) {
			return false;  // Actually, if there is no parent, calling this method makes no sense.
		}

		return parent.isNewChild(this);

	}

    @Override
    public ArrayList<HtmlElement> getChildren() {
        return null;
    }

//    @Override
//    public GuiDef getGuiDef() {
//        String errorInfo = "Class missing implementation: " + this.getClass().getName();
//        throw new NotImplementedException(errorInfo);
//    }
//
    @Override
    public GuiDef getGuiDefUpdate() {
        GuiDef guiDef = getGuiDef();
        guiDef.setUpdateMode(GuiDef.UpdateMode.REPLACE);
        return guiDef;
    }

    @Override
    public void updateChildList() {

    }

    @Override
    public void remove() {
		getParent().removeChild(this);
	}

    @Override
	public UpdateProcedure getUpdateProcedure() {
		return UpdateProcedure.GUI_DEF;
	}


    @Override
    public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

    @Override
	public Visibility getVisibility() {
		return visibility;
	}

    @Override
	public boolean isVisibilityModified() {
		if (visibilityOld != getVisibility()) {
			return true;
		} else {
			return false;
		}
	}

    @Override
	public void setVisibilityNotModified() {
		visibilityOld = getVisibility();
	}

    @Override
	public boolean isVisible() {
		var vis = getVisibility();
		return (vis != Visibility.VISIBILITY_HIDDEN && vis != Visibility.DISPLAY_NONE);
	}

	public HtmlBody getBody() {
		if (parent == null) {
			return null;
		}
		return parent.getBody();
	}


	public HtmlElement getRoot() {
		if (parent == null) {
			return this;
		} else {
			return parent.getRoot();
		}
	}

    // TODO: Review
    @Override
    public PageState getPageState() {
        throw new CodingErrorException("Error found!");
    }

    @Override
    public AbstractHtmlPage getPage() {
		if (parent == null) {
			return null;
		}
		return parent.getPage();
	}

    @Override
    public HtmlElement getDescendent(String htmlId) {
        return null;
    }

    public void setNotModified() {
//		setAttributesNotModified();
//		setContentOrGuiDefNotModified();
//		setStyleNotModified();
//		setCssClassesNotModified();
		setVisibilityNotModified();
	}


}
