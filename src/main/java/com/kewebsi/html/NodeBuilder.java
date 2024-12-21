package com.kewebsi.html;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kewebsi.util.JsonUtils;

import static com.kewebsi.html.HtmlTag.*;

public class NodeBuilder {

    protected String tagname;
    protected ObjectNode attrs;
    protected ArrayNode children;

    protected ObjectMapper objectMapper;

    public NodeBuilder() {
        ObjectMapper objectMapper = new ObjectMapper();
    }

    public NodeBuilder(String tagName,  String... attrKeyValue) {
        createNode(tagName, attrKeyValue);
    }

    public NodeBuilder createNode(String tagname, String... attrKeyValue) {

        this.tagname = tagname;

        if (attrKeyValue.length > 0) {
            if (attrs == null) {
                attrs = objectMapper.createObjectNode();
            }
            JsonUtils.addAttributes(attrs, attrKeyValue);
        }
        return this;
    }


    public NodeBuilder addChildren(ObjectNode... newChildren) {
        if (children == null) {
            children = objectMapper.createArrayNode();
            for (var childNode : newChildren) {
                children.add(childNode);
            }
        }
        return this;
    }

    public NodeBuilder addChildren(NodeBuilder... newChildren) {
        if (children == null) {
            children = objectMapper.createArrayNode();
            for (var childNode : newChildren) {
                children.add(childNode.getNode());
            }
        }
        return this;
    }

    public void addAttributes(String[] attrKeyValue) {
        if (attrKeyValue.length > 0) {
            if (attrs == null) {
                attrs = objectMapper.createObjectNode();
            }
            JsonUtils.addAttributes(attrs, attrKeyValue);
        }
    }

    public ObjectNode getNode() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put(TAGNAME, tagname);
        tagname = null;                                   // Avoid accidental re-use of the builder.
        if (attrs != null) {
            objectNode.putIfAbsent(ATTRS, attrs);
            attrs = null;                                // Avoid accidental re-use of the builder.
        }
        if (children != null) {
            objectNode.set(CHILDREN, children);
            children = null;                             // Avoid accidental re-use of the builder.
        }
        return objectNode;



    }

}
