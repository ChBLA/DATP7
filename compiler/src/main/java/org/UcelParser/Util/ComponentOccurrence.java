package org.UcelParser.Util;

import org.UcelParser.UCELParser_Generated.UCELParser;

import java.util.List;

public class ComponentOccurrence {
    private final UCELParser.CompConContext node;
    private final DeclarationInfo[] parameters;
    private final DeclarationInfo[] interfaces;
    private String prefix = "";

    public ComponentOccurrence(UCELParser.CompConContext node, DeclarationInfo[] parameters, DeclarationInfo[] interfaces) {
        this.node = node;
        this.parameters = parameters;
        this.interfaces = interfaces;
    }

    public UCELParser.CompConContext getNode() {
        return this.node;
    }

    public DeclarationInfo[] getParameters() {
        return this.parameters;
    }

    public DeclarationInfo[] getInterfaces() {
        return this.interfaces;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
