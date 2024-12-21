package com.kewebsi.html;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kewebsi.util.JsonUtils;

public class TagDef {

    protected String tagname;
    protected ObjectNode attrs;
    protected ArrayNode children;

    public TagDef(String tagName,  String... attrKeyValue) {
        createNode(tagName, attrKeyValue);
    }

    public TagDef createNode(String tagname, String... attrKeyValue) {

        this.tagname = tagname;

        if (attrKeyValue.length > 0) {
            if (attrs == null) {
                ObjectMapper objectMapper = new ObjectMapper();
                attrs = objectMapper.createObjectNode();
            }
            JsonUtils.addAttributes(attrs, attrKeyValue);
        }
        return this;
    }

    public TagDef addChildren(ObjectNode... newChildren) {
        if (children == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            children = objectMapper.createArrayNode();
            for (var childNode : newChildren) {
                children.add(childNode);
            }
        }
        return this;
    }

    public TagDef addChild(String tagName, String... attrKeyValue) {
        var child = new TagDef(tagName, attrKeyValue);
        // XXXXXXXXXXXXXXXXXXXXXXXX OPEN OPEN OPEN
        return this;
    }

    public TagDef addAttributes(String[] attrKeyValue) {
        if (attrKeyValue.length > 0) {
            if (attrs == null) {
                ObjectMapper objectMapper = new ObjectMapper();
                attrs = objectMapper.createObjectNode();
            }
            JsonUtils.addAttributes(attrs, attrKeyValue);
        }
        return this;
    }


}
