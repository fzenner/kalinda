package com.kewebsi.controller;

import com.fzenner.datademo.web.outmsg.MsgClientUpdate;
import com.fzenner.datademo.web.outmsg.GuiDef;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.html.AttributeModification;
import com.kewebsi.html.HtmlTag;
import com.kewebsi.html.table.HtmlPowerTable;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

// TODO: Move to a different package. It is not an HTML element.
public class UpdateProcessor {


    static final Logger LOG = LoggerFactory.getLogger(UpdateProcessor.class);

    public static void getModifiedTags(HtmlTag htmlTag, ArrayList<Pair<HtmlTag, MsgClientUpdate>> modifiedTags) {
        htmlTag.checkForDupicateIds(); // TODO: Only for robustness and debugging. Maybe remove for production. SPEEDUP
        htmlTag.updateChildList();
        if (htmlTag.isNewChild()) {
            MsgClientUpdate msg = MsgClientUpdate.newMsg(htmlTag);
            modifiedTags.add(new Pair(htmlTag, msg));
            htmlTag.setNotModified();

            // We do not collect further modifications on this new tag nor modifications of children of new tags,
            // because those children will be rendered freshly on the client side anyway.
            // Adding them to the modifications would result into unnecessary double update operations on the client side.
            // Hence we are mostly done here.
        } else {
            if (htmlTag.isContentOrGuiDefModified()) {
                if (htmlTag.getId() == null) {
                    throw new CodingErrorException("Attempt to modify a tag without ID:" + htmlTag.toString());
                }

                MsgClientUpdate updateData;
                if (htmlTag instanceof HtmlPowerTable<?>) {
                    // Create custom powertable modification
                    HtmlPowerTable powerTable = (HtmlPowerTable) htmlTag;
                    GuiDef guiDef = new GuiDef(htmlTag.getTagName(), htmlTag.getId());
                    var tableUpdateDateForGui = powerTable.getUpdateData();
                    if (tableUpdateDateForGui == null) {
                        LOG.warn("No updates found although table content was modified.");
                    }
                    guiDef.state = tableUpdateDateForGui;
                    guiDef.customUpdateOperation = "UPDATE_TABLE_PAYLOAD";
                    updateData = new MsgClientUpdate(powerTable.getId(), MsgClientUpdate.UpdateOperation.UPDATE_BY_DEF_CUSTOM, guiDef);

                } else {
                    updateData = MsgClientUpdate.modifiedMsg(htmlTag);
                }
                modifiedTags.add(new Pair(htmlTag, updateData));
                htmlTag.setNotModified();

                // We do not collect further modifications on this content-modified tag nor modifications of children of
                // it, because we will replace the whole tag on the client side.
                // Hence we are mostly done here.

            } else {

                if (htmlTag.isAttributesModified()) {

                    // We generate an update message for each modified attribute.
                    // Implement a compound message if a large number of attributes can be changed.
                    HashMap<String, AttributeModification> modifications = htmlTag.getAttributeModifications();
                    if (modifications != null) {
                        for (var modRunEntry : modifications.entrySet()) {
                            var modRun = modRunEntry.getValue();
                            MsgClientUpdate msg =
                                    switch (modRun.modification()) {
                                        case MODIFIED -> MsgClientUpdate.attributeModified(htmlTag, modRun.key(), modRun.value());
                                        case NEW -> MsgClientUpdate.attributeNew(htmlTag, modRun.key(), modRun.value());
                                        case REMOVED -> MsgClientUpdate.attributeRemoved(htmlTag, modRun.key(), modRun.value());
                                    };
                            modifiedTags.add(new Pair(htmlTag, msg));
                        }
                        htmlTag.setNotModified();
                    }
                }

                if (htmlTag.isCssClassesModified()) {
                    MsgClientUpdate msg = MsgClientUpdate.cssClassesChanged(htmlTag);
                    modifiedTags.add(new Pair(htmlTag, msg));
                    htmlTag.setNotModified();
                }

//                if (htmlTag.isVisibilityModified()) {
//                    // So far we process only hidden here.
//                    MsgClientUpdate msg = MsgClientUpdate.visibilityModified(htmlTag);
//                    modifiedTags.add(new Pair(htmlTag, msg));
//                    htmlTag.setNotModified();
//                }

                ArrayList<HtmlTag> removedChildren = htmlTag.getRemovedChildren();
                if (removedChildren.size() > 0) {	// This if is just so we can set a breakpoint easily.
                    for (var run : removedChildren) {
                        MsgClientUpdate msg = MsgClientUpdate.removedMsg(run);
                        modifiedTags.add(new Pair(htmlTag, msg));
                    }
                    htmlTag.setNotModified();
                }
                var children = htmlTag.getChildren();
                if (children != null) {
                    for (HtmlTag run : children) {
                        getModifiedTags(run, modifiedTags);
                    }
                }
            }
        }
    }
}
