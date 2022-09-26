public class ReferenceVisitor extends UCELBaseVisitor<Boolean> {
    private Scope currentScope;

    public ReferenceVisitor() {
        currentScope = null;
    }








    private void enterScope() {
        enterScope(false);
    }

    private void enterScope(boolean isComponent) {
        this.currentScope = new Scope(this.currentScope, isComponent);
    }

    private void exitScope() {
        this.currentScope = this.currentScope.getParent();
    }
}
