package com.kewebsi.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.fzenner.datademo.web.outmsg.MsgClientUpdate;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.errorhandling.ErrorHelper;
import com.kewebsi.errorhandling.ErrorInfo;
import com.kewebsi.util.JsonUtils;
import com.kewebsi.util.ListUtils;
import com.kewebsi.util.Out;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.tomcat.util.buf.StringUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.kewebsi.util.CommonUtils;

public abstract class HtmlTag {
	
	static final Logger LOG = LoggerFactory.getLogger(HtmlTag.class);

	public enum Visibility {VISIBILITY_HIDDEN, VISIBILITY_VISIBLE, DISPLAY_NONE, DISPLAY_BLOCK}

	public enum UpdateProcedure {HTML, GUI_DEF};

	/**
	 * Marker class to identify HTML tags that should be linked to the central
	 * event handler.
	 */
	public static final String TEXT_NODE = "TEXT_NODE";

	public static final String TAGNAME = "tagname";
	public static final String ATTRS = "attrs";
	public static final String CLIENT_ELEMENTE_DATA = "cld";
	public static final String CHILDREN = "children";
	public static final String HTML_TEXT = "text";

	protected HtmlTag parent;


	protected ArrayList<HtmlTag> children;
	
	protected String id = null;
	
	private ArrayList<String> cssClasses;

	protected ClientDataOrError clientDataOrError;

	protected PageVarEditor.ClientSyncState clientSyncState;

	protected ArrayList<String> dynamicCssClassesOld;


	protected ArrayList<HtmlTag> oldChildList;

	protected Visibility visibility;
	protected Visibility visibilityOld;

	public HtmlTag() {
		// There are tags that do not need an id.
	}
	
	public HtmlTag(String id) {
		this.id = id;
	}
	
	
	public HtmlTag getParent() {
		return parent;
	}

	public void setParent(HtmlTag parent) {
		this.parent = parent;
	}


	/**
	 * null parameter will be silently ignored
	 * @param child
	 */
	public void addChild(HtmlTag child) {
		if (child != null) {
			if (children == null) {
				children = new ArrayList<HtmlTag>();
			}
			storeCurrentChildListAsOldChildlist();
			children.add(child);
			child.setParent(this);

			if (ErrorHelper.debbugingIsActive()) {
				getRoot().checkForDupicateIds();
			}

		}
	}

	/**
	 * null parameters will be silently ignored
	 * @param children
	 */
	public void addChildren(HtmlTag... children) {
		for (var tagRun : children) {
			addChild(tagRun);
		}
	}

	public void addChildren(ArrayList<HtmlTag> children) {
		for (var tagRun : children) {
			addChild(tagRun);
		}
	}


	public void setChildren(HtmlTag... children) {
		removeAllChildren();
		addChildren(children);
	}


	public void setChildren(ArrayList<HtmlTag> children) {
		removeAllChildren();
		addChildren(children);
	}
	
	
	public void removeChild(HtmlTag child) {
		int idxOfChild = children.indexOf(child);
		if (idxOfChild < 0) {
			String childString = child.toString();
			LOG.warn(String.format("Child %s was not in list of children of %s", childString.substring(0, Integer.max(80, childString.length())), this.toString().substring(0,  80)));			
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
	
	
	public boolean removeChildIfExists(HtmlTag child) {
		int idxOfChild = children.indexOf(child);
		if (idxOfChild < 0) {
			return false;
		}
		storeCurrentChildListAsOldChildlist();
		children.remove(idxOfChild);
		return true;
	}
	
	public void remove() {
		getParent().removeChild(this);
	}
	
	
	/**
	 * ***********************************************************************
	 * Modified child list detection mechanism:
	 *
	 * All HtmlTags maintain a childList.
	 *
	 * After the generation of HTML (for the browser), the oldChildList is set to null via resetOldChildlist().
	 *
	 * When during an Ajax-request, a child is added or removed from the child list, we store a copy of the current child
	 * list as old child list. This way, we are able to compare the old child list (the child list at the beginning of the
	 * Ajax request) with the new child list (the child list at the end of the Ajax request).
	 *
	 * So when the oldChildList is null, we know that there was no change in the child list. If it is not null, we
	 * have a copy of the old child list to calculate the difference between old and new.
	 *
	 *
	 ************************************************************************
	 */
	protected void storeCurrentChildListAsOldChildlist() {
		if (oldChildList == null) {
			if (children != null) {  // During initialization it is possible that the current children are null.

//				//XXXXXXXXXXXXXXX Debug.
//				if (this instanceof HtmlPage) {
//					LOG.debug("Gotcha!");
//				}

				oldChildList = new ArrayList<HtmlTag>(children);
			}
		}
	}

	/**
	 * To be invoked after generation of HTML for and sending of HTML to the client.
	 */
	public void resetOldChildlist() {
		oldChildList = null;
	}

	

	// SPEEDUP: Return null instead of an empty list. That avoids element creation.
	public ArrayList<HtmlTag> getRemovedChildren() {
		
		ArrayList<HtmlTag> result = new ArrayList<>();
		if (oldChildList != null) {
			for (var oldChildRun : oldChildList) {
				if (!children.contains(oldChildRun)) {
					result.add(oldChildRun);
				}
			}
		}

		return result;
	}


	public boolean getChildListDiffs( Out<ArrayList<HtmlTag>> addedChildren,  Out<ArrayList<HtmlTag>> removedChildren, Out<ArrayList<Integer>> newPositions) {
		return ListUtils.getListListDiffs(oldChildList, children, addedChildren, removedChildren, newPositions);
	}

	
	public boolean isNewChild() {
		HtmlTag parent = getParent();
		if (parent == null) {
			return false;  // Actually, if there is no parent, calling this method makes no sense.
		}
		
		return parent.isNewChild(this);
		
	}
	
	
	public boolean isNewChild(HtmlTag child) {
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
					LOG.debug("Invoking HtmlTag.isNewChild(HtmlTag child) on an unconnected HtmlTag");;
					return false;
				}
			}
		}
	}
	
	
	
	public boolean childListModified() {
		return oldChildList != null;
	}

	
	public AbstractHtmlPage getPage() {
		if (parent == null) {
			return null;
		}
		return parent.getPage();
	}


	public HtmlBody getBody() {
		if (parent == null) {
			return null;
		}
		return parent.getBody();
	}


	public HtmlTag getRoot() {
		if (parent == null) {
			return this;
		} else {
			return parent.getRoot();
		}
	}
	
	
	public  String getPageName() {
		AbstractHtmlPage page = getPage();
		return page == null ? null : page.getPageName();
	}
	


	public PageState getPageState() {
		var parent = getParent();
		if (parent != null) {
			return parent.getPageState();
		}
		return null;
	}
	
	
	public boolean isAjaxInProgress() {
		AbstractHtmlPage page = getPage();
		if (page != null) {
			return page.isAjaxInProgress();
		}
		return false;
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	

	public static ObjectNode getAttributesNodeAdding(ObjectNode node, String... keyValue) {
		ObjectNode attrNode = null;
		if (keyValue.length > 0) {
			attrNode = JsonUtils.createAttributesNode(node);
			JsonUtils.addAttributes(attrNode, keyValue);
		}
		return attrNode;
	}


	public static void addAttributeToAttributeSubnote(ObjectNode node, String key, String value) {
		ObjectNode attrNode = JsonUtils.createAttributesNode(node);
		if (value != null) {
			attrNode.put(key, value);
		} else {
			attrNode.putNull(key);
		}
	}



	public void addCssClass(ObjectNode attributeNode) {
		String clss = getCssClassesAsString();
		if (CommonUtils.hasInfo(clss)) {
			attributeNode.put("class", clss);
		}
	}


	public void addId(ObjectNode node) {
		node.put("id", id);
	}

	public void addIdAndCssClass(ObjectNode attributeNode) {
		attributeNode.put("id", id);
		String clss = getCssClassesAsString();
		if (CommonUtils.hasInfo(clss)) {
			attributeNode.put("class", clss);
		}
	}

	public void addAttribute(ObjectNode attributeNode, String key, String val) {
		attributeNode.put(key, val);
	}


	public void addAttributes(ObjectNode attributeNode, String[] kevValues) {
		JsonUtils.addAttributes(attributeNode, kevValues);
	}

	public void addIfNotNull(ObjectNode attributeNode, String key, String val) {
		if (CommonUtils.hasInfo(val)) {
			attributeNode.put(key, val);
		}
	}


	public static void addAttrsIfNotNull(LinkedHashMap<String, String> attrs, String key, String value) {
		if (key == null) {
			return;
		}

		if (value == null) {
			return;
		}

		if (attrs.containsKey(key)) {
			throw new CodingErrorException(String.format("Attempting to add a key twice to a LinkedHashMap. Key:%s, Value: %s", key, value));
		}

		attrs.put(key, value);
	}

	/**
	 * This method is invoked during the change detection. An Ajax call can modify the PageState, which might result
	 * in a change of the child list. So the child list needs to be recalculated during change detection and hence
	 * this method needs to be implemeneted.
	 *
	 * For performance reasons, HtmlTags that do not support child list modifications during Ajax calls (their child
	 * list stay stable after the initial creation) should do nothing in this method.
	 *
	 */
	public void updateChildList() {

	}
	
	public void setNotModifiedRecursively() {
		setNotModified();
		if (children != null) {
			for (HtmlTag run: children) {
				run.setNotModifiedRecursively();
			}
		}	
	}
	
	
	public void resetModificationMarkersRecursively() {
		setNotModified();
		resetOldChildlist();
		
		if (children != null) {
			for (HtmlTag run: children) {
				run.resetModificationMarkersRecursively();
			}
		}
	}

	public void checkForDupicateIds() {
		HashMap<String, HtmlTag> ids = new HashMap<>();
		checkForDupicateIds(ids);
	}

	public void checkForDupicateIds(HashMap<String, HtmlTag> ids) {
		if (CommonUtils.hasInfo(id)) {
			if (ids.containsKey(id)) {
				throw new CodingErrorException("Duplicate key " + id + " found for " + this);
			}
			ids.put(id, this);
		}

		if (children != null) {
			for (var childRun : this.children) {
				childRun.checkForDupicateIds(ids);
			}
		}
	}



	public void getModifiedTagsNew(ArrayList<Pair<HtmlTag, MsgClientUpdate>> modifiedTags) {
		checkForDupicateIds(); // TODO: Only for robustness and debugging. Maybe remove for production. SPEEDUP
		updateChildList();

		Out<ArrayList<HtmlTag>> addedChildren = new Out<>();
		Out<ArrayList<HtmlTag>> removedChildren2 = new Out<>();
		Out<ArrayList<Integer>> positions = new Out<>();

		boolean childListHasChanged  = ListUtils.getListListDiffs(this.oldChildList, this.children, addedChildren, removedChildren2, positions);
		// XXXXXXXXXXXXXXXXX

		if (isNewChild()) {
			MsgClientUpdate msg = MsgClientUpdate.newMsg(this);
			modifiedTags.add(new Pair(this, msg));
			setNotModified();

			// We do not collect further modifications on this new tag nor modifications of children of new tags,
			// because those children will be rendered freshly on the client side anyway.
			// Adding them to the modifications would result into unnecessary double update operations on the client side.
			// Hence we are mostly done here.
		} else {
			if (isContentOrGuiDefModified()) {

				if (id == null) {
					throw new CodingErrorException("Attempt to modify a tag without ID:" + this.toString());
				}
				MsgClientUpdate msg = MsgClientUpdate.modifiedMsg(this);
				modifiedTags.add(new Pair(this, msg));
				setNotModified();

				// We do not collect further modifications on this content-modified tag nor modifications of children of
				// it, because we will replace the whole tag on the client side.
				// Hence we are mostly done here.

			} else {

				if (isAttributesModified()) {

					// We generate an update message for each modified attribute.
					// Implement a compound message if a large number of attributes can be changed.
					HashMap<String, AttributeModification> modifications = getAttributeModifications();
					if (modifications != null) {
						for (var modRunEntry : modifications.entrySet()) {
							var modRun = modRunEntry.getValue();
							MsgClientUpdate msg =
									switch (modRun.modification()) {
										case MODIFIED -> MsgClientUpdate.attributeModified(this, modRun.key(), modRun.value());
										case NEW -> MsgClientUpdate.attributeNew(this, modRun.key(), modRun.value());
										case REMOVED -> MsgClientUpdate.attributeRemoved(this, modRun.key(), modRun.value());
									};
							modifiedTags.add(new Pair(this, msg));
						}
						setNotModified();
					}
				}

				if (isCssClassesModified()) {
					MsgClientUpdate msg = MsgClientUpdate.cssClassesChanged(this);
					modifiedTags.add(new Pair(this, msg));
					setNotModified();
				}

				if (isVisibilityModified()) {
					// So far we process only hidden here.
					MsgClientUpdate msg = MsgClientUpdate.visibilityModified(this);
					modifiedTags.add(new Pair(this, msg));
					setNotModified();
				}

				ArrayList<HtmlTag> removedChildren = getRemovedChildren();
				if (removedChildren.size() > 0) {	// This if is just so we can set a breakpoint easily.
					for (var run : removedChildren) {
						MsgClientUpdate msg = MsgClientUpdate.removedMsg(run);
						modifiedTags.add(new Pair(this, msg));
					}
					setNotModified();
				}

				if (children != null) {
					for (HtmlTag run : children) {
						run.getModifiedTagsNew(modifiedTags);
					}
				}
			}
		}
	}


	public HtmlTag getChild(String htmlId) {
		if (children != null) {
			for (HtmlTag run: children) {
				if (run.getId() != null && run.getId().equals(htmlId)) {
					return run;
				}
			}
		}
		return null;
	}
	
	
	/**
	 * Searches for a child element by ID.
	 * Performs a breadth-first search.
	 * Speed-up possible by implementing hashing.
	 * @param htmlId The id to search for. Must be non-null.
	 * @return The HTML tag with the given HTML ID if it exists, null otherwise.
	 */
	public HtmlTag getDescendent(String htmlId) {
		if (children != null) {
			for (HtmlTag run: children) {
				String childId = run.getId();
				if (htmlId.equals(childId)) {
					return run;
				}
			}
			for (HtmlTag run: children) {
				HtmlTag descendentOfChild = run.getDescendent(htmlId);
				if (descendentOfChild != null) {
					return descendentOfChild;
				}
			}
		}
		return null;
	}		
		
	
	
	public ArrayList<String> getCssClasses() {
		ArrayList<String> dynCss = getDynamicCssClasses();
		ArrayList<String> result;

		if (dynCss == null) {
			result = cssClasses;
		} else {
			if (cssClasses != null) {
				result = new ArrayList<String>(dynCss.size() + cssClasses.size());
				result.addAll(dynCss);
				result.addAll(cssClasses);
			} else {
				result = dynCss;
			}
		}
		return result;
	}
	
	public String getCssClassesAsString() {
		ArrayList<String> myCssClasses = getCssClasses();
		String result;
		if (myCssClasses != null && myCssClasses.size()>0) {
			result = StringUtils.join(myCssClasses, ' ');
		} else {
			result = "";
		}
		return result;
	}



	
	/**
	 * @return Returns e.g. the string (without outer double quotes) "class='myclass1 myclass2'")
	 *         Empty string if element has no class.
	 */
	public String getClassAttributeAndValue() {
		String classAttrValue = getCssClassesAsString();
		if (CommonUtils.hasInfo(classAttrValue)) { 
			return "class='" + classAttrValue + "'";
		} else {
			return "";
		}
	}
	
	
	/**
	 * 
	 * @param cssClasses List of classes
	 * @return Returns e.g. the string (without outer double quotes) "class='givenClass1 givenClass2'")
	 */
	public static String getClassAttributeAndValue(String... cssClasses) {    
		if (cssClasses.length >0) {
			String blankSeparated = String.join(" ", cssClasses);
			return "class='" + blankSeparated + "'";
		} else {
			return "";
		}
	}

	
	/**
	 * Adds a CSS class to the internal array.
	 * @param cssClass Handles gracefully null, empty and blank-only strings
	 */
	public HtmlTag addCssClass(String cssClass) {
		if (CommonUtils.hasInfo(cssClass)) {
			if (cssClasses == null) {
				cssClasses = new ArrayList<String>(5);
			}
			if (cssClasses.contains(cssClass)) {
				LOG.warn("Attempt to add the CSS class '" + cssClass + "' twice");
			} else {
				this.cssClasses.add(cssClass);
			}

		}
		return this;
	}


	public HtmlTag removeCssClass(String cssClass) {
		if (cssClasses != null) {
			boolean reallyRemoved = cssClasses.remove(cssClass);
			if (reallyRemoved) {
				// cssClassesModificationCount++;
			}
		}
		return this;
	}



	

	public GuiDef getGuiDef() {
		String errorInfo = "Class missing implementation: " + this.getClass().getName();
		throw new NotImplementedException(errorInfo);
	}

	public GuiDef getGuiDefUpdate() {
		GuiDef guiDef = getGuiDef();
		guiDef.setUpdateMode(GuiDef.UpdateMode.REPLACE);
		return guiDef;
	}


	public void setVisibilityIfVisibilityModified(GuiDef guiDef) {
		if (this.isVisibilityModified()) {
			guiDef.visibility = getVisibility();
		}
	}

	public Visibility calculateModificationOfVisibility() {
		if (this.isVisibilityModified()) {
			return getVisibility();
		}
		return null;
	}


	protected void addChildrenGuiDefs(GuiDef parentGuiDef) {
		if (children != null) {
			for (HtmlTag run : children) {
				parentGuiDef.appendChild(run.getGuiDef());
			}
		}
	}
	
//	public boolean isModified() {
//		return isAttributesModified() || isContentModified();
//	}
	
	public void setNotModified() {
		setAttributesNotModified();
		setContentOrGuiDefNotModified();
		setStyleNotModified();
		setCssClassesNotModified();
		setVisibilityNotModified();
	}

	/**
	 * Modification of the content.
	 * Note: The content should be considered not modified, if only HtmlTags that are registered as children of this
	 * HtmlTag are modified. (The modification of the explicit children are managed separately.)
	 * So the modification indicated by this flag is between the tags excluding the children
	 * @return
	 */
	public abstract boolean isContentOrGuiDefModified();
	public abstract void setContentOrGuiDefNotModified();


	

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + id;
	}


	public boolean isAttributesModified() {
		// For simple tags, we do not manage attribute change only only handling.
		// (Any change triggers the complete replacement of the tag.)
		return isContentOrGuiDefModified();
	}

	public void setAttributesNotModified() {
		// For simple tags, we do not manage attribute only handling.
		// (Any change triggers the complete replacement of the tag.)
	}


	public boolean isCssClassesModified() {
		ArrayList<String> dynCssClasses = getDynamicCssClasses();


		if (dynCssClasses == null) {
			if (dynamicCssClassesOld == null) {
				return false;
			} else {
				return true;
			}
		} else {
			if (dynamicCssClassesOld == null) {
				return true;
			} else {
				if (dynCssClasses.equals(dynamicCssClassesOld)) {
					return false;
				} else {
					return true;
				}
			}
		}
	}


	public void setCssClassesNotModified() {
		dynamicCssClassesOld = getDynamicCssClasses();
	}
	public void setStyleNotModified() {
		visibilityOld = getVisibility();
	}


	public ArrayList<HtmlTag> getChildren() {
		return children;
	}


	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}


	public Visibility getVisibility() {
		return visibility;
	}

	public boolean isVisibilityModified() {
		if (visibilityOld != getVisibility()) {
			return true;
		} else {
			return false;
		}
	}

	public void setVisibilityNotModified() {
		visibilityOld = getVisibility();
	}

	public boolean isVisible() {
		var vis = getVisibility();
		return (vis != Visibility.VISIBILITY_HIDDEN && vis != Visibility.DISPLAY_NONE);
	}


	public HashMap<String, String> getAddedAttributes() {
		return null;
	}

	public HashMap<String, String> getRemovedAttributes() {
		return null;
	}


	public HashMap<String, AttributeModification> getAttributeModifications() {
		return null;
	}

	public String getStyleStringForVisibility() {

		Visibility visibility = getVisibility();
		if (visibility==null) {
			return "";
		}
		String result =
		switch(getVisibility()) {
			case DISPLAY_NONE -> "display:none;";
			case DISPLAY_BLOCK -> "display:block;";
			case VISIBILITY_HIDDEN -> "visibility:hidden";
			case VISIBILITY_VISIBLE -> "visibility:visible";
		};
		return result;
	}


	public boolean isCollapsed() {
		return false;
	}


	public ArrayList<String> getDynamicCssClasses() {
		return null;
	}


	public UpdateProcedure getUpdateProcedure() {
		return UpdateProcedure.GUI_DEF;
	}

	public String getTagName() {
		return "OVERLOAD_ME";
	}

	public boolean isModalWindow() {
		return false;
	}


	public boolean isInModalWindow() {
		if (isModalWindow()) {
			return true;
		} else {
			if (parent != null) {
				return parent.isModalWindow();
			} else {
				return false;
			}
		}
	}

	public ClientDataOrError getClientElementData() {
		return clientDataOrError;
	}

	public void setClientDataOrError(ClientDataOrError clientDataOrError) {
		this.clientDataOrError = clientDataOrError;
	}

}
