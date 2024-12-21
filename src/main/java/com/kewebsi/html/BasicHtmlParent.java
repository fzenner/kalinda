package com.kewebsi.html;

import com.fzenner.datademo.web.outmsg.GuiDef;
import com.kewebsi.errorhandling.ErrorHelper;
import com.kewebsi.util.ListUtils;
import com.kewebsi.util.Out;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public abstract class BasicHtmlParent extends BasicHtmlElement {

    protected ArrayList<HtmlElement> children;

    protected ArrayList<HtmlElement> oldChildList;

    static final Logger LOG = LoggerFactory.getLogger(BasicHtmlParent.class);

    /**
     * null parameter will be silently ignored
     *
     * @param child
     */
    public void addChild(HtmlElement child) {
        if (child != null) {
            if (children == null) {
                children = new ArrayList<HtmlElement>();
            }
            storeCurrentChildListAsOldChildlist();
            children.add(child);
            child.setParent(this);

            if (ErrorHelper.debbugingIsActive()) {
                HtmlElementHelper.checkForDupicateIds(getRoot());
            }
        }
    }

    /**
     * null parameters will be silently ignored
     *
     * @param children
     */
    public void addChildren(HtmlElement... children) {
        for (var tagRun : children) {
            addChild(tagRun);
        }
    }

    public void addChildren(ArrayList<HtmlElement> children) {
        for (var tagRun : children) {
            addChild(tagRun);
        }
    }


    public void setChildren(HtmlElement... children) {
        removeAllChildren();
        addChildren(children);
    }


    public void setChildren(ArrayList<HtmlElement> children) {
        removeAllChildren();
        addChildren(children);
    }


    public void removeChild(HtmlElement child) {
        int idxOfChild = children.indexOf(child);
        if (idxOfChild < 0) {
            String childString = child.toString();
            LOG.warn(String.format("Child %s was not in list of children of %s", childString.substring(0, Integer.max(80, childString.length())), this.toString().substring(0, 80)));
            return;
        }
        storeCurrentChildListAsOldChildlist();
        children.remove(idxOfChild);
        child.setParent(null);
    }

    public void removeAllChildren() {
        storeCurrentChildListAsOldChildlist();
        if (children != null) {   // children can be null during initialization routines
            for (var childRun : children) {
                childRun.setParent(null);
            }
        }
        children = null;
    }

    public boolean removeChildIfExists(HtmlElement child) {
        int idxOfChild = children.indexOf(child);
        if (idxOfChild < 0) {
            return false;
        }
        storeCurrentChildListAsOldChildlist();
        children.remove(idxOfChild);
        return true;
    }

    public ArrayList<HtmlElement> getRemovedChildren() {

		ArrayList<HtmlElement> result = new ArrayList<>();
		if (oldChildList != null) {
			for (var oldChildRun : oldChildList) {
				if (!children.contains(oldChildRun)) {
					result.add(oldChildRun);
				}
			}
		}

		return result;
	}


    /**
     * ***********************************************************************
     * Modified child list detection mechanism:
     * <p>
     * All HtmlTags maintain a childList.
     * <p>
     * After the generation of HTML (for the browser), the oldChildList is set to null via resetOldChildlist().
     * <p>
     * When during an Ajax-request, a child is added or removed from the child list, we store a copy of the current child
     * list as old child list. This way, we are able to compare the old child list (the child list at the beginning of the
     * Ajax request) with the new child list (the child list at the end of the Ajax request).
     * <p>
     * So when the oldChildList is null, we know that there was no change in the child list. If it is not null, we
     * have a copy of the old child list to calculate the difference between old and new.
     * <p>
     * <p>
     * ***********************************************************************
     */
    protected void storeCurrentChildListAsOldChildlist() {
        if (oldChildList == null) {
            if (children != null) {  // During initialization it is possible that the current children are null.

//				//XXXXXXXXXXXXXXX Debug.
//				if (this instanceof HtmlPage) {
//					LOG.debug("Gotcha!");
//				}

                oldChildList = new ArrayList<HtmlElement>(children);
            }
        }
    }

    /**
     * To be invoked after generation of HTML for and sending of HTML to the client.
     */
    public void resetOldChildlist() {
        oldChildList = null;
    }




    public boolean getChildListDiffs(Out<ArrayList<HtmlElement>> addedChildren, Out<ArrayList<HtmlElement>> removedChildren, Out<ArrayList<Integer>> newPositions) {
        return ListUtils.getListListDiffs(oldChildList, children, addedChildren, removedChildren, newPositions);
    }


    public boolean isNewChild() {
        BasicHtmlParent parent = getParent();
        if (parent == null) {
            return false;  // Actually, if there is no parent, calling this method makes no sense.
        }

        return parent.isNewChild(this);

    }


    public boolean isNewChild(HtmlElement child) {
        if (oldChildList == null) {
            // There was no modification on the child list.
            return false;
        } else {
            if (oldChildList.contains(child)) {
                return false;
            } else {
                // This is just a sanity check. Strictly speaking not required, if we assume that the given child IS a child of the parent
                if (children.contains(child)) {
                    return true;
                } else {
                    LOG.debug("Invoking HtmlTag.isNewChild(HtmlTag child) on an unconnected HtmlTag");
                    ;
                    return false;
                }
            }
        }
    }



    public boolean childListModified() {
        return oldChildList != null;
    }


	public HtmlElement getDescendent(String htmlId) {
		if (children != null) {
			for (HtmlElement run: children) {
				String childId = run.getId();
				if (htmlId.equals(childId)) {
					return run;
				}
			}
			for (HtmlElement run: children) {
                HtmlElement descendentOfChild = run.getDescendent(htmlId);
				if (descendentOfChild != null) {
					return descendentOfChild;
				}
			}
		}
		return null;
	}

    public void addChildrenGuiDefs(GuiDef parentGuiDef) {
        if (children != null) {
            for (HtmlElement run : children) {
                parentGuiDef.appendChild(run.getGuiDef());
            }
        }
    }

}
