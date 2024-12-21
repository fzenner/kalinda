package com.kewebsi.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.web.outmsg.GuiDef;

public class HtmlBody extends HtmlTag {

    public AbstractHtmlPage page;

    protected boolean contentModified = false;

    public HtmlBody(AbstractHtmlPage page) {
        this.id = page.getPageName();
        this.page = page;
    }

    @Override
    public AbstractHtmlPage getPage() {
        return page;
    }

    @Override
    public String getPageName() {
        return page.getPageName();
    }

    public String getHtml() {
        return "<body id='" + getId() + "'>" + "XX LOADING11" + "</body>";
    }


    @Override
    public boolean isContentOrGuiDefModified() {
        return contentModified;
    }

    @Override
    public void setContentOrGuiDefNotModified() {
        this.contentModified = false;
    }

    public void forceReload() {
        this.contentModified = true;
    }

    @Override
    public GuiDef getGuiDef() {

        GuiDef guiDef = new GuiDef("body", getId());
        addChildrenGuiDefs(guiDef);
        guiDef.addAttribute("pagename", getPageName());
        return guiDef;
    }

    @Override
    public HtmlBody getBody() {
        return this;
    }
}
