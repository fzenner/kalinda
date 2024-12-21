package com.kewebsi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.gui.EntityNavigatorPage;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.inmsg.MsgInvokeServiceWithSimpleParams;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.html.*;
import com.kewebsi.html.search.AtomicSearchConstraint;
import com.kewebsi.html.search.EntityNavigatorState;
import com.kewebsi.service.*;
import com.kewebsi.util.JsonUtils;

import java.util.ArrayList;

public class SearchDivController {


    protected DtoAssistant dtoAssistant;

    public SearchDivController(DtoAssistant dtoAssistant) {
        this.dtoAssistant = dtoAssistant;
    }

    public static final StaticGuiDelegate executeEntityEditorSearch = new StaticGuiDelegate(SearchDivController::executeEntityEditorSearch, "executeEntityEditorSearch", GuiCallbackRegistrar.getInstance());




    public static MsgAjaxResponse executeEntityEditorSearch(JsonNode rootNode, UserSession userSession, AbstractHtmlPage page) {

        MsgInvokeServiceWithSimpleParams msg = JsonUtils.parseJsonFromClient(rootNode, MsgInvokeServiceWithSimpleParams.class);
        EntityNavigatorPage entityNavigatorPage = (EntityNavigatorPage) page;

        EntityNavigatorState<?> entityNavigatorState = entityNavigatorPage.getEntityEditorState();

        ArrayList<AtomicSearchConstraint> ascArray = entityNavigatorState.getAtomicSearchConstraints();

        boolean errorInInputData = false;
        String whereClause = "";
        try {

            boolean isFirstConstraint = true;
            for (AtomicSearchConstraint c : ascArray) {
                if (c.isConstraintIsActive()) {

                    boolean dataFieldsOk = false;
                    PageStateError pageStateError = c.getFirstInvalidFieldAndMarkFieldWithError();
                    if (pageStateError != null) {
                        return MsgAjaxResponse.createErrorMsg(pageStateError);
                    }

                    var fieldAssistant = c.getFieldAssistant();
                    if (! isFirstConstraint ) {
                        whereClause += " AND ";
                    }
                    switch (c.getFieldType()) {
                        case INT ->  {
                            whereClause += buildIntConstraint(c);
                            isFirstConstraint = false;
                        }
                        case LONG -> {
                            whereClause += buildLongConstraint(c);
                            isFirstConstraint = false;
                        }
                        case ENM -> {
                            whereClause += buildEnumConstraint(c);
                            isFirstConstraint = false;
                        }
                        case STR -> {
                            whereClause += buildStringConstraint(c);
                            isFirstConstraint = false;
                        }

                        case LOCALDATETIME -> {
                            whereClause += buildDateConstraint(c);
                            isFirstConstraint = false;
                        }
                    }
                }
            }
        } catch (PageVarError error) {
            errorInInputData = true;
        }

        if (errorInInputData) {
            // We could create a popup or another error indication. For now we rely on the error display on the
            // input fields.
            return MsgAjaxResponse.createSuccessMsg();
        }

        DtoAssistant dtoAssistant = entityNavigatorPage.getEntityAssistant();
        var resultList = StaticEntityService.searchForEntities(dtoAssistant, null, whereClause);

        entityNavigatorState.getTableModel().setData(resultList);
        return MsgAjaxResponse.createSuccessMsg();
    }


    public static String buildIntConstraint(AtomicSearchConstraint asc) throws PageVarError {
        String result;
        var fieldAssistant = asc.getFieldAssistant();
        String dbColName = fieldAssistant.getDbColName();
        PageVarIntColdLink var = (PageVarIntColdLink) asc.getEqualOrMinVar();
        var valEqualOrMin = var.getValidatedVal();

        switch (asc.getConstraintType()) {
            case EQUALS -> {
                if (valEqualOrMin == null) {
                    result = dbColName + " IS NULL";
                } else {
                    result = dbColName + "=" + valEqualOrMin;
                }
            }
            case BETWEEN -> {
                PageVarIntColdLink varMax = (PageVarIntColdLink) asc.getMaxVar();
                var valMax = varMax.getValidatedVal();
                result = dbColName + ">=" + valEqualOrMin + " and " + dbColName + "<=" + valMax;
            }
            default -> {
                throw new CodingErrorException("Unhandled enum: " + asc.getConstraintType().name());
            }
        }
        return result;
    }


    public static String buildLongConstraint(AtomicSearchConstraint asc) throws PageVarError {
        String result;

        switch (asc.getConstraintType()) {
            case EQUALS -> {
                var variable = (PageVarLongColdLink) asc.getEqualOrMinVar();
                var val = variable.getValidatedVal();

                var fieldAssistant = asc.getFieldAssistant();
                String dbColName = fieldAssistant.getDbColName();

                result = dbColName + "=" + val;
            }
            case BETWEEN -> {
                var varMin = (PageVarLongColdLink) asc.getEqualOrMinVar();
                var valMin = varMin.getNonNullVal();
                var varMax = (PageVarLongColdLink) asc.getMaxVar();
                var valMax = varMax.getNonNullVal();

                var fieldAssistant = asc.getFieldAssistant();
                String dbColName = fieldAssistant.getDbColName();

                result = dbColName + ">=" + valMin + " and " + dbColName + "<=" + valMax;
            }
            default -> {
                throw new CodingErrorException("Unhandled enum: " + asc.getConstraintType().name());
            }
        }
        return result;
    }



    public static String buildEnumConstraint(AtomicSearchConstraint asc) throws PageVarError {
        String result;

        var variable = (PageVarEnumColdLink) asc.getEqualOrMinVar();
        var val = variable.getValidatedVal();
        var fieldAssistant = asc.getFieldAssistant();
        String dbColName = fieldAssistant.getDbColName();
        if (val != null) {
            String formattedVal = escapeSpecialCharactersForSql(val.name());
            result = dbColName + "='" + formattedVal + "'";
        } else {
            result = dbColName + " is null";
        }

        return result;
    }



    public static String buildDateConstraint(AtomicSearchConstraint asc) throws PageVarError {
        String result;

        var fieldAssistant = asc.getFieldAssistant();
        String dbColName = fieldAssistant.getDbColName();

        switch (asc.getConstraintType()) {
            case EQUALS -> {
                PageVarLocalDateTimeColdLink var = (PageVarLocalDateTimeColdLink) asc.getEqualOrMinVar();
                var val = var.getValOrThrowError();




                int y = val.getYear();
                int m = val.getMonthValue();
                int d = val.getDayOfMonth();

                String format = "year(%s)=%d and month(%s)=%d and day(%s)=%d";
                result = String.format(format, dbColName, y, dbColName, m, dbColName, d);
            }
            case BETWEEN -> {

                PageVarLocalDateTimeColdLink varMin = (PageVarLocalDateTimeColdLink) asc.getEqualOrMinVar();
                var valMin = varMin.getValOrThrowError();
                int yMin = valMin.getYear();
                int mMin = valMin.getMonthValue();
                int dMin = valMin.getDayOfMonth();
                int minLimit = yMin *10000 + mMin * 100 + dMin;
                String formatMin = "year(%s)*10000 + month(%s)*100 + day(%s) >= %d";
                String larger = String.format(formatMin, dbColName, dbColName, dbColName, minLimit);

                PageVarLocalDateTimeColdLink varMax = (PageVarLocalDateTimeColdLink) asc.getMaxVar();
                var valMax = varMax.getValOrThrowError();
                int yMax = valMax.getYear();
                int mMax = valMax.getMonthValue();
                int dMax = valMax.getDayOfMonth();
                int maxLimit = yMax *10000 + mMax * 100 + dMax;
                String formatMax = "year(%s)*10000 + month(%s)*100 + day(%s) <= %d";
                String smaller = String.format(formatMax, dbColName, dbColName, dbColName, maxLimit);

                result = larger + " and " + smaller;

            }
            default -> {
                throw new CodingErrorException("Unhandled enum: " + asc.getConstraintType().name());
            }
        }
        return result;
    }








    public static String buildStringConstraint(AtomicSearchConstraint asc) throws PageVarError {
        var variable = asc.getEqualOrMinVar();
        var val = variable.getDisplayString().trim().toLowerCase();

        var fieldAssistant = asc.getFieldAssistant();
        String dbColName = fieldAssistant.getDbColName();

        boolean searchLike = val.contains("*");

        String result;
        if (searchLike) {
            String formattedVal = escapeSpecialCharactersForSql(val);
            formattedVal = replaceStarWithPercent(formattedVal);
            result = "lower(" + dbColName + ") like'" + formattedVal + "'";

        } else {
            result = "lower(" + dbColName + ") ='" + val + "'";
        }

        return result;
    }


    public static String escapeSpecialCharactersForSql(String in) {
        final String HIBERNATE_ESCAPE_CHAR = "\\";
        String out = in.replace("\\",  HIBERNATE_ESCAPE_CHAR + "\\");
        out.replace("_",   HIBERNATE_ESCAPE_CHAR + "_");
        out.replace("%",   HIBERNATE_ESCAPE_CHAR + "%");
        return out;
    };


    public static String replaceStarWithPercent(String in) {
        String out = in.replaceAll("\\*+", "%");
        return out;
    }

}
