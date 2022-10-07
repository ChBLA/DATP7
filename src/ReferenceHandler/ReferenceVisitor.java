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

        DeclarationReference tableReference = null;

        try {
            tableReference = currentScope.find(identifier, true);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx,"Variable '" + identifier + "' has not been declared in scope"));
            return false;
        }

        ctx.reference = tableReference;
        return true;
    }

    @Override
    public Boolean visitFuncCall(UCELParser.FuncCallContext ctx) {
        String identifier = ctx.ID().getText();

        DeclarationReference tableReference = null;

        try {
            tableReference = currentScope.find(identifier, true);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx,"Function '" + identifier + "' has not been declared in scope"));
            return false;
        }

        ctx.reference = tableReference;
        visit(ctx.arguments());
        return true;
    }

    @Override
    public Boolean visitVariableDecl(UCELParser.VariableDeclContext ctx) {
        boolean b = true;

        for(UCELParser.VariableIDContext idCtx : ctx.variableID()) {
            Boolean valid = visit(idCtx);
            b = b && (valid != null) && valid;
        }

        return b;
    }

    @Override
    public Boolean visitVariableID(UCELParser.VariableIDContext ctx) {
        String identifier = ctx.ID().getText();

        //TODO: maybe delegate to Scope.add
        if(!currentScope.isUnique(identifier, true)) {
            logger.log(new ErrorLog(ctx, "The variable name '" + identifier + "' already defined in scope"));
            return false;
        }

        boolean valid = true;

        for (UCELParser.ArrayDeclContext arrayDecl : ctx.arrayDecl()) {
            valid = valid && visit(arrayDecl);
        }

        ctx.reference = currentScope.add(new DeclarationInfo(identifier));
        if(ctx.initialiser() != null)
            valid = valid && visit(ctx.initialiser());

        return valid;
    }

    @Override
    public Boolean visitBlock(UCELParser.BlockContext ctx) {

        boolean success = true;
        enterScope();
        ctx.scope = currentScope;

        for(UCELParser.LocalDeclarationContext ldc : ctx.localDeclaration())
            success &= visit(ldc);

        for(UCELParser.StatementContext sc : ctx.statement())
            success &= visit(sc);

        exitScope();

        return success;
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

    public Scope getCurrentScope() {
        return currentScope;
    }
}
