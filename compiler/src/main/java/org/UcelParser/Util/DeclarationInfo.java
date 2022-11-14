package org.UcelParser.Util;

import org.UcelParser.Util.Value.InterpreterValue;
import org.antlr.v4.runtime.ParserRuleContext;

public class DeclarationInfo implements NameGenerator {

    private ParserRuleContext node;
    private Scope scope;
    private String identifier;
    private Type type;

    private InterpreterValue value;

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
    public String generateName(String componentPrefix) {
        return componentPrefix + (componentPrefix.isEmpty() ? "" : "_") + this.scope.getPrefix() + "_" + this.identifier;
    }

    public InterpreterValue getValue() {
        return value;
    }

    public void setValue(InterpreterValue value) {
        this.value = value;
    }

}