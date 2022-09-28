public class ReferenceVisitor extends UCELBaseVisitor<Boolean> {
    private Scope currentScope;

    public ReferenceVisitor() {
        this.currentScope = null;
    }

    public ReferenceVisitor(Scope scope) { this.currentScope = scope; }

    @Override
    public Boolean visitRefExprWrapper(UCELParser.RefExprWrapperContext ctx) {
        if(!(ctx.children.get(0) instanceof UCELParser.RefExpressionContext)) {
            //TODO logger parser error
            return false;
        }

        UCELParser.RefExpressionContext node = (UCELParser.RefExpressionContext) ctx.children.get(0);
        String identifer = node.ID().getText();

        TableReference tableReference = null;

        try {
            tableReference = currentScope.find(identifer, true);
        } catch (Exception e) {
            //TODO logger
            return false;
        }

        node.reference = tableReference;
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
