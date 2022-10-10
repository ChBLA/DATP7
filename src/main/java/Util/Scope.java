package Util;

import java.util.ArrayList;

public class Scope {

    private Scope parent;
    private ArrayList<DeclarationInfo> declarationInfos;
    private boolean isComponent;

    public Scope(Scope parent, boolean isComponent) {
        this.parent = parent;
        this.declarationInfos = new ArrayList<>();
        this.isComponent = isComponent;
    }

    public Scope(Scope parent, boolean isComponent, ArrayList<DeclarationInfo> variables) {
        this.parent = parent;
        this.declarationInfos = variables;
        this.isComponent = isComponent;
    }

    public DeclarationReference add(DeclarationInfo v) {
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

    public DeclarationReference find(String s, boolean isVarID) throws Exception {
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
            throw new Exception("Identifier Not Found In Scope");
        }
    }

    public DeclarationInfo get(DeclarationReference tableReference) throws Exception {
        if (tableReference.getRelativeScope() > 0) {
            if (parent == null) {
                throw new Exception("Scope has no parent");
            }
            return parent.get(tableReference.moveOutOfScope());
        } else {
            return declarationInfos.get(tableReference.getDeclarationId());
        }
    }

}