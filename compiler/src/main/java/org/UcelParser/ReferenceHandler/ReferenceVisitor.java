package org.UcelParser.ReferenceHandler;

import org.UcelParser.UCELParser_Generated.*;
import org.UcelParser.Util.*;
import org.UcelParser.Util.Logging.*;

import java.util.ArrayList;

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
    protected Boolean aggregateResult(Boolean aggregate, Boolean nextResult) {
        return (nextResult == null || nextResult) && (aggregate == null || aggregate);
    }

    @Override
    public Boolean visitFunction(UCELParser.FunctionContext ctx) {

        try {
            if(!currentScope.isUnique(ctx.ID().getText(), false)) {
                return false;
            }
            DeclarationReference declRef = currentScope.add(new DeclarationInfo(ctx.ID().getText(), ctx));
            ctx.reference = declRef;

        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Compiler Error: " + e.getMessage()));
        }

        if(!visit(ctx.type())) {
            return false;
        }

        if(!visit(ctx.parameters())) {
            return false;
        }

        enterScope();
        ctx.scope = currentScope;

        if(!visit(ctx.block())) {
            return false;
        }

        ctx.occurrences = new ArrayList<>();
        exitScope();
        return true;
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

        ctx.reference = currentScope.add(new DeclarationInfo(identifier, ctx));
        if(ctx.initialiser() != null)
            valid = valid && visit(ctx.initialiser());

        return valid;
    }

    @Override
    public Boolean visitIteration(UCELParser.IterationContext ctx) {
        String identifier = ctx.ID().getText();
        visit(ctx.type());

        try {
            if(!currentScope.isUnique(identifier, true)) {
                logger.log(new ErrorLog(ctx, "Variable '" + identifier + "' already exists in scope"));
                return false;
            }
            DeclarationReference declRef = currentScope.add(new DeclarationInfo(identifier, ctx));
            ctx.reference = declRef;
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Compiler Error" + e.getMessage()));
            return false;
        }

        return visit(ctx.statement());
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

    @Override
    public Boolean visitIncrementPost(UCELParser.IncrementPostContext ctx) {
        return handleIncrementDecrement(ctx);
    }

    @Override
    public Boolean visitIncrementPre(UCELParser.IncrementPreContext ctx) {
        return handleIncrementDecrement(ctx);
    }

    @Override
    public Boolean visitDecrementPost(UCELParser.DecrementPostContext ctx) {
        return handleIncrementDecrement(ctx);
    }

    @Override
    public Boolean visitDecrementPre(UCELParser.DecrementPreContext ctx) {
        return handleIncrementDecrement(ctx);
    }

    private Boolean handleIncrementDecrement(UCELParser.ExpressionContext ctx) {
        UCELParser.ExpressionContext expr = ctx.getRuleContext(UCELParser.ExpressionContext.class, 0);
        if(expr instanceof UCELParser.IdExprContext ||
                expr instanceof UCELParser.StructAccessContext) {
            return visit(expr);
        } else {
            logger.log(new ErrorLog(ctx, "Operator only valid for a reference expressions, " +
                    "such as a variable or a struct field"));
            return false;
        }
    }

    @Override
    public Boolean visitAssignExpr(UCELParser.AssignExprContext ctx) {
        UCELParser.ExpressionContext expr = ctx.expression(0);
        if(expr instanceof UCELParser.IdExprContext ||
                expr instanceof UCELParser.StructAccessContext) {
            return visit(expr);
        } else {
            logger.log(new ErrorLog(ctx, "Left side of an assignment requires a reference expressions, " +
                    "such as a variable or a struct field"));
            return false;
        }
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
