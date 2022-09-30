public class ReferenceVisitor extends UCELBaseVisitor<Boolean> {
    private Scope currentScope;
    private Logger logger;

    public ReferenceVisitor(Scope scope) {
        this.currentScope = scope;
        this.logger = new Logger();
    }

    public ReferenceVisitor(Logger logger) {
        this.currentScope = null;
        this.logger = logger;
    }

    @Override
    public Boolean visitIdExpr(UCELParser.IdExprContext ctx) {
        String identifier = ctx.ID().getText();

        TableReference tableReference = null;

        try {
            tableReference = currentScope.find(identifier, true);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx,"Identifier '" + identifier + "' has not been declared in scope"));
            return false;
        }

        ctx.reference = tableReference;
        return true;
    }

    @Override
    public Boolean visitFuncCall(UCELParser.FuncCallContext ctx) {
        String identifier = ctx.ID().getText();

        TableReference tableReference = null;

        try {
            tableReference = currentScope.find(identifier, true);
        } catch (Exception e) {
            //TODO logger
            return false;
        }

        ctx.reference = tableReference;
        visit(ctx.arguments());
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
