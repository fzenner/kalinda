package com.kewebsi.html;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kewebsi.util.CommonUtils;
import org.apache.tomcat.util.buf.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Base class for isomorphic cration of HTML elements
 */
public abstract class StandardHtmlElement extends BasicHtmlParent{

    private ArrayList<String> cssClasses;
	protected ArrayList<String> dynamicCssClassesOld;
	protected HtmlAttributes dynamicAttributesOld;


	public StandardHtmlElement(String id) {
		this.id = id;
	}


    public void addCssClass(ObjectNode attributeNode) {
		String clss = getCssClassesAsString();
		if (CommonUtils.hasInfo(clss)) {
			attributeNode.put("class", clss);
		}
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
		if (myCssClasses != null && !myCssClasses.isEmpty()) {
			result = StringUtils.join(myCssClasses, ' ');
		} else {
			result = "";
		}
		return result;
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

    public ArrayList<String> getDynamicCssClasses() {
		return null;
	}

	public HtmlAttributes getDynamicAttributes() {
		return null;
	}

	public void setAttributesNotModified() {
		var currentAttributes = getDynamicAttributes();
		if (currentAttributes == null) {
			dynamicAttributesOld = null;
		} else {
			dynamicAttributesOld = new HtmlAttributes(getDynamicAttributes());
		}
	}

	public boolean isAttributesModified() {
		return dynamicAttributesModified();
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


}
