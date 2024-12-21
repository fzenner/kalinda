package com.kewebsi.html;

public class SimpleTag extends SmartTag {

    protected String tagname;


    public SimpleTag(String tagname) {
        this.tagname = tagname;
    }

    public SimpleTag(String tagname, String... attrsKeyValue) {
        this.tagname = tagname;
        this.addAttributes(attrsKeyValue);
    }

    public SimpleTag(String tagname, HtmlTag... children) {
        this.tagname = tagname;
        this.addChildren(children);
    }

    public SimpleTag(String tagname, String[] attrsKeyValue, HtmlTag... children) {
        this.tagname = tagname;
        this.addAttributes(attrsKeyValue);
        this.addChildren(children);
    }

    public SimpleTag txt(String text) {
        this.text = text;
        return this;
    }

    @Override
    public String getTag() {
        return tagname;
    }

    public static SimpleTag tag(String tagname) {
        return new SimpleTag(tagname);
    }


    public static SimpleTag tag(String tagname, String [] attrKeyValue) {
        return new SimpleTag(tagname, attrKeyValue);
    }

    public static SimpleTag tag(String tagname, HtmlTag... children) {
        return new SimpleTag(tagname, children);
    }

    public static SimpleTag tag(String tagname, String[] attrKeyValue, HtmlTag... children) {
        var result = new SimpleTag(tagname, attrKeyValue, children);
        return result;
    }

    public SimpleTag childTag(String tagname) {
        var child = new SimpleTag(tagname);
        addChild(child);
        return child;
    }

    public SimpleTag childTag(String tagname, String... attrKeyValue) {
        var child = new SimpleTag(tagname, attrKeyValue);
        addChild(child);
        return child;
    }

    public SimpleTag childTag(String tagname, HtmlTag... children) {
        var child = new SimpleTag(tagname, children);
        addChild(child);
        return child;
    }

    public SimpleTag childTag(String tagname, String[] attrKeyValue, HtmlTag... children) {
        var child = new SimpleTag(tagname, attrKeyValue, children);
        addChild(child);
        return child;
    }

    public SimpleTag cssClass(String cssClassName) {
        addCssClass(cssClassName);
        return this;
    };




    public static SimpleTag tr() {
        return new Tr();
    }

    public static SimpleTag tr(String [] attrKeyValue) {
        return new Tr(attrKeyValue);
    }

    public static SimpleTag tr(String[] attrKeyValue, HtmlTag... children) {
        var result = new Tr(attrKeyValue, children);
        return result;
    }

    public static SimpleTag tr(HtmlTag... children) {
        var result = new Tr(children);
        return result;
    }


    public static SimpleTag td() {
        return new Td();
    }

    public static SimpleTag td(String... attrKeyValue) {
        return new Td(attrKeyValue);
    }

    public static SimpleTag td(String[] attrKeyValue, HtmlTag... children) {
        var result = new Td(attrKeyValue, children);
        return result;
    }

    public static SimpleTag td(HtmlTag... children) {
        var result = new Td(children);
        return result;
    }

    public static SimpleTag td(String text) {
        var result = new Td();
        result.txt(text);
        return result;
    }


    public static SimpleTag th() {
        return new Th();
    }

    public static SimpleTag th(String [] attrKeyValue) {
        return new Th(attrKeyValue);
    }

    public static SimpleTag th(String[] attrKeyValue, HtmlTag... children) {
        var result = new Th(attrKeyValue, children);
        return result;
    }

    public static SimpleTag th(HtmlTag... children) {
        var result = new Td(children);
        return result;
    }

    public static SimpleTag th(String text) {
        var result = new Th();
        result.txt(text);
        return result;
    }

    public static SimpleTag thead() {
        return new Thead();
    }

    public static SimpleTag thead(String [] attrKeyValue) {
        return new Thead(attrKeyValue);
    }

    public static SimpleTag thead(String[] attrKeyValue, HtmlTag... children) {
        var result = new Thead(attrKeyValue, children);
        return result;
    }

    public static SimpleTag thead(HtmlTag... children) {
        var result = new Thead(children);
        return result;
    }

    public static SimpleTag tbody() {
        return new Tbody();
    }

    public static SimpleTag tbody(String [] attrKeyValue) {
        return new Tbody(attrKeyValue);
    }

    public static SimpleTag tbody(String[] attrKeyValue, HtmlTag... children) {
        var result = new Tbody(attrKeyValue, children);
        return result;
    }

    public static SimpleTag tbody(HtmlTag... children) {
        var result = new Tbody(children);
        return result;
    }





}
