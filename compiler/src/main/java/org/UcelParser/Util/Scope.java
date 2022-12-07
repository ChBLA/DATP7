package org.UcelParser.Util;

import org.UcelParser.Util.Exception.CouldNotFindException;
import org.UcelParser.Util.Exception.NoParentScopeException;
import org.UcelParser.Util.Value.InterpreterValue;
import org.antlr.v4.codegen.model.decl.Decl;

import java.util.ArrayList;
import java.util.List;

public class Scope {

    private static final UniquePrefixGenerator upg = new UniquePrefixGenerator();
    private final String prefix;
    private Scope parent;
    private ArrayList<DeclarationInfo> declarationInfos;
    private boolean isComponent;

    public Scope(Scope parent, boolean isComponent) {
        this.parent = parent;
        this.declarationInfos = new ArrayList<>();
        this.isComponent = isComponent;
        this.prefix = upg.getNewPrefix();
    }

    public Scope(Scope parent, boolean isComponent, ArrayList<DeclarationInfo> variables) {
        this.parent = parent;
        this.declarationInfos = variables;
        this.isComponent = isComponent;
        this.prefix = upg.getNewPrefix();
    }

    public DeclarationReference add(DeclarationInfo v) {
        v.setScope(this);
        declarationInfos.add(v);
        return new DeclarationReference(0, declarationInfos.size() - 1);
    }

    public Scope getParent() {
        return parent;
    }

    public boolean isUnique(String s, boolean isVarID) {
        for (int i = 0; i < declarationInfos.size(); i++) {
            if (declarationInfos.get(i).isCalled(s)) {
                return false;
            }
        }

        if (parent != null && !(isComponent && isVarID)) {
            return parent.isUnique(s, isVarID);
        }

        return true;
    }

    public DeclarationReference find(String s, boolean isVarID) throws CouldNotFindException {
        for (int i = 0; i < declarationInfos.size(); i++) {
            if (declarationInfos.get(i).isCalled(s)) {
                return new DeclarationReference(0, i);
            }
        }

        if (parent != null && !(isComponent && isVarID)) {
            DeclarationReference result = parent.find(s, isVarID);
            result.incrementScopeLevel();
            return result;
        } else {
            throw new CouldNotFindException("Identifier Not Found In Scope");
        }
    }

    public DeclarationInfo get(DeclarationReference tableReference) throws CouldNotFindException {
        if (tableReference.getRelativeScope() > 0) {
            if (parent == null) {
                throw new CouldNotFindException(tableReference);
            }
            return parent.get(tableReference.moveOutOfScope());
        } else {
            return declarationInfos.get(tableReference.getDeclarationId());
        }
    }

    public Scope getScope(DeclarationReference tableReference) throws CouldNotFindException {
        if (tableReference.getRelativeScope() > 0) {
            if (parent == null) {
                throw new CouldNotFindException(tableReference);
            }
            return parent.getScope(tableReference.moveOutOfScope());
        } else {
            return this;
        }
    }

    public String getPrefix() {
        return this.prefix;
    }

    public List<DeclarationInfo> replaceDeclarationInfoForRef(DeclarationReference reference, DeclarationInfo newInfo) {
        try {
            declarationInfos.set(declarationInfos.indexOf(this.get(reference)), newInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return declarationInfos;
    }

    public InterpreterValue[] getValues() {
        InterpreterValue[] values = new InterpreterValue[declarationInfos.size()];

        for(int i = 0; i < values.length; i++) {
            values[i] = declarationInfos.get(i).getValue();
        }
        return values;
    }

    public void setValues(InterpreterValue[] values) {
        for(int i = 0; i < values.length; i++) {
            declarationInfos.get(i).setValue(values[i]);
        }
    }

}