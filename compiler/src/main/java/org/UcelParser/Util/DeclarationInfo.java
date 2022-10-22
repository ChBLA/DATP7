package org.UcelParser.Util;

import org.antlr.v4.runtime.ParserRuleContext;

public class DeclarationInfo {

    private ParserRuleContext node;
    private Scope scope;
    private String identifier;
    private Type type;

    //region Only used for testing
    public DeclarationInfo() {
        //Only for tests
    }
    public DeclarationInfo(String identifier) {
        this(identifier, null, null);
    }

    public DeclarationInfo(String identifier, Type type) {
        this(identifier, type, null);
    }
    //endregion

    public DeclarationInfo(String identifier, ParserRuleContext node) {
        this(identifier, null, node);
    }

    public DeclarationInfo(String identifier, Type type, ParserRuleContext node) {
        this.identifier = identifier;
        this.type = type;
        this.node = node;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }
    public Scope getScope() {
        return this.scope;
    }

    public ParserRuleContext getNode() {
        return this.node;
    }

    public void setType(Type type) {
        this.type = type;
    }
    public Type getType() { return this.type; }

    public boolean isCalled(String s) {
        return s.equals(identifier);
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof DeclarationInfo)) return false;

        DeclarationInfo o = (DeclarationInfo) other;

        return ((this.type == null && o.getType() == null) || this.type.equals(o.getType())) &&
                o.isCalled(identifier);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String generateName() {
        return this.scope.getPrefix() + "_" + this.identifier;
    }

}