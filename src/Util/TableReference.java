public class TableReference {
    private int variable;
    private int relativeScope;

    public TableReference(int scopeLevel, int variable) {
        this.relativeScope = scopeLevel;
        this.variable = variable;
    }

    public int getVariable() {
        return variable;
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

    public TableReference moveOutOfScope() {
        return new TableReference(relativeScope - 1, variable);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof TableReference))
            return false;

        TableReference t = (TableReference) o;

        return t.getRelativeScope() == this.relativeScope &&
                t.getVariable() == this.variable;
    }
}
