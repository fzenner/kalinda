package com.kewebsi.html;

import com.kewebsi.controller.FieldAssistant;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.service.PageVarError;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Marker interface for objects that hold the state of a page (per user)
 * @author master
 *
 */
public class PageState {

    /**
     * Prefix for the HTML IDs of the HTML Tags generated vor the variables of this state object.
     */
    protected String pageStateId;


    boolean showPageStateError = false;

    public PageState() {
    }

    public PageState(String pageStateId) {
        this.pageStateId = pageStateId;
    }



   private LinkedHashMap<String, PageStateVarIntf> pageStateVars = new LinkedHashMap<>();

    // public HashMap<String, PageState> childStates = new HashMap<>();

    public void registerPageStateVar(PageStateVarIntf pageStateVar) {
        String key = pageStateVar.getPageVarId();

        if (key == null) {
            throw new CodingErrorException("Registering PageStateVar with null key.");
        }

        if (pageStateVars.get(key) != null) {
            throw new CodingErrorException("Duplicate registering of a PageStateVar: " + key);
        }
        pageStateVars.put(key, pageStateVar);
    }

    public void unregisterPageStateVar(PageStateVarIntf pageStateVar) {
        var val = pageStateVars.remove(pageStateVar.getPageVarId());
        if (val == null) {
            throw new CodingErrorException("Attempt to unregister an unregistered page variable:" + pageStateVar.getPageVarId());
        }
    }


    public PageStateVarIntf getPageStateVar(String htmlId) {
        return pageStateVars.get(htmlId);
    }


    public PageStateVarIntf getPageStateVar(FieldAssistant fieldAssistant) {

        for (var pageStateVar : pageStateVars.values()) {
            if (pageStateVar.getFieldAssistant() == fieldAssistant) {
                return pageStateVar;
            }
        }
        return null;
    }

    public Collection<PageStateVarIntf> getPageStateVars() {
        return pageStateVars.values();
    }

    public void setPageStateVar(String htmlId, String value) {
        pageStateVars.get(htmlId).setStringValueFromClient(value);
    }


//    public void registerChildState(String childStateId, PageState pageState) {
//        if (childStates.containsKey(childStateId)) {
//            throw new CodingErrorException("Duplicate registering of child state " + childStateId);
//        }
//
//        childStates.put(childStateId, pageState);
//    }
//
//    public PageState getChildState(String childStateId) {
//        return childStates.get(childStateId);
//    }
//

    public void clearPageVars() {
        for (var pageVar : pageStateVars.values()) {
            pageVar.clear();
            pageVar.clearError();
        }
    }


    public void clearErrors() {
        for (var pageVar : pageStateVars.values()) {
            pageVar.clearError();
        }
    }

//    public Object getEntity(String entitySlot) {
//        throw new CodingErrorException("PageState does not hold any entities.");
//    }
//
//
//    public PowerTableModel<?> getPowerTableModel(String tableModelSlot) {
//        throw new CodingErrorException("PageState does not hold any entities.");
//    }

    // public LinkedHashMap<String, PageStateVar> getPageStateVars() {
    //     return pageStateVars;
    // }

    public String getPageStateId() {
        return pageStateId;
    }


    public PageStateError getFirstErrorInPageVars() {
        for (var psv : pageStateVars.values()) {
            if (psv.hasEffectiveError()) {
                return new PageStateError(psv.getEffectiveError());
            }
        }
        return null;
    }

    /**
     * Returns the first page variable that has either an error or is relevant but empty.
     * Attaches an error to the page variable.
     * @return
     */
    public PageStateError getFirstInvalidFieldAndMarkFieldWithError() {
        for (var psv : pageStateVars.values()) {
            PageVarError pageVarError = psv.validateAndCeckIfInvalidNull();
            if (pageVarError != null) {
                return new PageStateError(pageVarError);
            }
        }
        return null;
    }


    public PageStateError getError() {
        return getFirstErrorInPageVars();
    }

    public boolean hasError() {
        if (getError() != null) {
            return true;
        }
        return false;
    }

//    public boolean verifyAllMandatoryVarsNotNullSetError() {
//        for (var psv : pageStateVars.values()) {
//            if (psv.isRelevant()) {
//                if (psv.isNull()) {
//                    psv.setError("Value required");
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

    public boolean pageVarsWereEdited() {
        for (var psv : pageStateVars.values()) {
            if (psv.stringValueWasEditied()) {
                return true;
            }
        }
        return false;
    }


}
