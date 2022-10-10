package Util;

public class DeclarationReference {
    private int declarationId;
    private int relativeScope;

    public DeclarationReference(int scopeLevel, int declarationId) {
        this.relativeScope = scopeLevel;
        this.declarationId = declarationId;
    }

    public int getDeclarationId() {
        return declarationId;
    }

    public int getRelativeScope() {
        return relativeScope;
    }

    public void incrementScopeLevel() {
        this.relativeScope++;
    }

    public void decrementScopeLevel() {
        if (this.relativeScope < 1)
            throw new RuntimeException(); //TODO: InvalidScopeLevelException
        this.relativeScope--;
    }

    public DeclarationReference moveOutOfScope() {
        return new DeclarationReference(relativeScope - 1, declarationId);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof DeclarationReference))
            return false;

        DeclarationReference t = (DeclarationReference) o;

        return t.getRelativeScope() == this.relativeScope &&
                t.getDeclarationId() == this.declarationId;
    }
}
