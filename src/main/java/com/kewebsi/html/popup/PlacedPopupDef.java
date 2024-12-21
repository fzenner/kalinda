package com.kewebsi.html.popup;

import com.fzenner.datademo.web.outmsg.GuiDef;

public class PlacedPopupDef extends GuiDef {

    public String relatedHtmlElementId;

    public PlacedPopupDef(String tag, String id, String relatedHtmlElementId) {
        super(tag, id);
        this.relatedHtmlElementId = relatedHtmlElementId;
    }


}
