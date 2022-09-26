public class TableReference {
    public int variable;
    public int relativeScope;

    public TableReference(int scopeLevel, int variable) {
        this.relativeScope = scopeLevel;
        this.variable = variable;
    }

    public void incrementScopeLevel() {
        this.relativeScope++;
    }

    public void decrementScopeLevel() {
        if (this.relativeScope < 1)
            throw new RuntimeException(); //TODO: InvalidScopeLevelException
        this.relativeScope--;
    }
}
