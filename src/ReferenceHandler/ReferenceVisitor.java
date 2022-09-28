public class ReferenceVisitor extends UCELBaseVisitor<Boolean> {
    private Scope currentScope;

    public ReferenceVisitor() {
        this.currentScope = null;
    }

    public ReferenceVisitor(Scope scope) { this.currentScope = scope; }

    @Override
    public Boolean visitIdExpr(UCELParser.IdExprContext ctx) {
        String identifier = ctx.ID().getText();

        TableReference tableReference = null;

        try {
            tableReference = currentScope.find(identifier, true);
        } catch (Exception e) {
            //TODO logger
            return false;
        }

        ctx.reference = tableReference;
        return true;
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
